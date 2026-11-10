package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.ClientToServerHandshakePacket;
import com.nukkitx.protocol.bedrock.packet.ServerToClientHandshakePacket;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jose.JWSHeader;

import javax.crypto.SecretKey;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;

/**
 * Handles the encryption handshake from the MCPE 1.1 server.
 *
 * <p>Two wire formats exist among MCPE 1.1 servers:</p>
 * <ul>
 *   <li><b>Nukkit vanilla 1.1.x</b> — sends a single JWT string in
 *       publicKey; serverToken is empty/unused. The JWT payload contains
 *       the ECDH "salt".</li>
 *   <li><b>Rooti fork</b> — sends two separate strings: publicKey (the
 *       raw base64 EC key) and serverToken (the JWT with the salt
 *       payload).</li>
 * </ul>
 *
 * <p>This translator tries both formats:</p>
 * <ol>
 *   <li>Try publicKey as a JWT. If it parses and has a salt, use it.</li>
 *   <li>Otherwise try serverToken as a JWT.</li>
 *   <li>Otherwise treat publicKey as a raw base64 EC key and serverToken
 *       as the salt (Rooti 2-field layout).</li>
 * </ol>
 */
public class ServerToClientHandshake implements BedrockPacketTranslator {

    @Override
    public boolean immediate() {
        return true;
    }

    @Override
    public void translate(BedrockPacket pk, Player player) {
        try {
            ServerToClientHandshakePacket packet = (ServerToClientHandshakePacket) pk;
            String publicKeyField = packet.getPublicKey() == null ? "" : packet.getPublicKey();
            String serverTokenField = packet.getServerToken() == null ? "" : packet.getServerToken();

            SignedJWT signedJWT = null;
            if (!publicKeyField.isEmpty() && looksLikeJwt(publicKeyField)) {
                try {
                    signedJWT = SignedJWT.parse(publicKeyField);
                } catch (Exception ignored) {
                    signedJWT = null;
                }
            }
            if (signedJWT == null && !serverTokenField.isEmpty() && looksLikeJwt(serverTokenField)) {
                try {
                    signedJWT = SignedJWT.parse(serverTokenField);
                } catch (Exception ignored) {
                    signedJWT = null;
                }
            }

            if (signedJWT != null) {
                JWSHeader header = signedJWT.getHeader();
                String x5u = header.getX509CertURL() == null
                    ? null : header.getX509CertURL().toString();
                if (x5u == null || x5u.isEmpty()) {
                    throw new IllegalStateException("Server handshake JWT missing x5u header");
                }
                ECPublicKey serverKey = parseEcKey(x5u);
                String payloadJson = signedJWT.getPayload().toString();
                com.alibaba.fastjson2.JSONObject payloadObj =
                    com.alibaba.fastjson2.JSON.parseObject(payloadJson);
                Object saltObj = payloadObj.get("salt");
                String saltStr = saltObj == null ? "" : saltObj.toString();
                SecretKey key = com.nukkitx.protocol.bedrock.util.EncryptionUtils.getSecretKey(
                        player.getPrivateKey(),
                        serverKey,
                        Base64.getDecoder().decode(saltStr)
                );
                player.getBedrockClientSession().enableEncryption(key);
            } else {
                if (publicKeyField.isEmpty()) {
                    throw new IllegalStateException("Server handshake packet has neither JWT nor publicKey");
                }
                ECPublicKey serverKey = parseEcKey(publicKeyField);
                byte[] salt = serverTokenField.isEmpty()
                    ? new byte[16]
                    : Base64.getDecoder().decode(serverTokenField);
                SecretKey key = com.nukkitx.protocol.bedrock.util.EncryptionUtils.getSecretKey(
                        player.getPrivateKey(),
                        serverKey,
                        salt
                );
                player.getBedrockClientSession().enableEncryption(key);
            }
        } catch (Exception e) {
            org.CreadoresProgram.CraftsMine.server.Server.getInstance()
                .getLogger().warn("Encryption handshake failed: " + e.getMessage());
            return;
        }

        ClientToServerHandshakePacket clientToServerHandshake = new ClientToServerHandshakePacket();
        player.getBedrockClientSession().sendPacketImmediately(clientToServerHandshake);
    }

    private static boolean looksLikeJwt(String s) {
        if (s == null || s.isEmpty()) return false;
        int dots = 0;
        for (int i = 0; i < s.length(); i++) if (s.charAt(i) == '.') dots++;
        return dots == 2 && !s.contains(" ") && s.length() > 40;
    }

    private static ECPublicKey parseEcKey(String base64Key) throws Exception {
        try {
            return com.nukkitx.protocol.bedrock.util.EncryptionUtils.generateKey(base64Key);
        } catch (Throwable ignored) {
        }
        byte[] der = Base64.getDecoder().decode(base64Key.replaceAll("\\s", ""));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(der);
        KeyFactory kf = KeyFactory.getInstance("EC");
        return (ECPublicKey) kf.generatePublic(spec);
    }
}
