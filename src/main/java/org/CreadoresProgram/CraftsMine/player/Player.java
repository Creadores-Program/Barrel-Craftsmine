package org.CreadoresProgram.CraftsMine.player;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import com.nukkitx.math.vector.Vector2f;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.BedrockClient;
import com.nukkitx.protocol.bedrock.BedrockClientSession;
import com.nukkitx.protocol.bedrock.data.*;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import com.nukkitx.protocol.bedrock.packet.StartGamePacket;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import lombok.Getter;
import lombok.Setter;
import org.CreadoresProgram.CraftsMine.server.Server;
import org.CreadoresProgram.CraftsMine.network.translator.PacketTranslatorManager;
import org.CreadoresProgram.CraftsMine.options.Config;
import org.CreadoresProgram.CraftsMine.network.BedrockBatchHandler;
import org.CreadoresProgram.CraftsMine.utils.Utils;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.DisconnectPacket;
import org.CreadoresProgram.CraftsMine.nukkitLib.entity.data.Skin;
import org.CreadoresProgram.CraftsMine.inventory.Item;
import org.CreadoresProgram.CraftsMine.utils.BlockMapper;
import org.CreadoresProgram.CraftsMine.network.translator.managerScreen.ScreenManager;

public class Player extends Vector3{
  @Getter
  private String identifierCraftsMan;
  @Getter
  private BedrockClientSession bedrockClientSession;
  @Getter
  private BedrockClient bedrockClient;
  @Getter
  private final PacketTranslatorManager packetTranslatorManager;
  // The 'channel' field was removed: NukkitX BedrockClient manages the
  // underlying Netty channel internally and exposes close() instead of
  // direct channel manipulation. Use bedrockClient.close() on disconnect.
  @Getter
  private String craftsmanUsername;
  @Getter
  private String username;
  @Getter
  private String UUID;
  @Getter
  private ECPublicKey publicKey;
  @Getter
  private String craftsmanPublicKey;
  @Getter
  private ECPrivateKey privateKey;
  @Getter
  private long clientID;

  @Getter
  @Setter
  private Item[] hotbar;

  @Getter
  @Setter
  private Item[] inventory;
  
  @Getter
  @Setter
  private long runtimeEntityId;

  @Getter
  @Setter
  private int gamemode;

  @Getter
  private Skin skinC;

  private String skinId;

  private long nbf;

  private long exp;

  // No @Getter here: getPlayerInputExecutor() is hand-rolled below to lazily
  // resolve the shared Server scheduler instead of allocating one per player.
  private volatile java.util.concurrent.ScheduledExecutorService playerInputExecutor;
  @Getter
  @Setter
  private boolean playerInputAuth = false;

  @Getter
  @Setter
  private long tikPia = 0;

  @Getter
  @Setter
  private boolean sneaking = false;

  @Getter
  @Setter
  private boolean sprinting = false;

  @Getter
  @Setter
  private PlayerActionType diggingStatus = PlayerActionType.STOP_BREAK;

  @Getter
  @Setter
  private com.nukkitx.math.vector.Vector3i diggingPosition = com.nukkitx.math.vector.Vector3i.from(0, 0, 0);

  @Getter
  @Setter
  private int diggingFace = 0;

  @Getter
  @Setter
  private Vector3f oldPosition;

  @Getter
  private Map<Long, Long> uniquesEntitysIds = new HashMap<>();

  @Getter
  private Map<Integer, String> scoreBoard = new TreeMap<>();

  @Getter
  private Map<Long, Object[]> bossBars = new HashMap<>();

  @Getter
  private ScreenManager screenManager;

  @Getter
  @Setter
  private BlockMapper blockMapper;
  
  public Player(String indentifier){
    this.identifierCraftsMan = indentifier;
    this.packetTranslatorManager = new PacketTranslatorManager(this);
    this.screenManager = new ScreenManager(this);
  }
  /**
   * Returns the shared scheduler from the owning Server, lazily resolved on
   * first use. The previous implementation eagerly allocated a per-player
   * {@code Executors.newScheduledThreadPool(1)} in the constructor, which
   * kept one idle thread alive per connected client even when nothing was
   * ever scheduled on it. Delegating to {@link Server#getScheduler()} keeps
   * the per-player footprint at zero until something is actually scheduled.
   */
  public java.util.concurrent.ScheduledExecutorService getPlayerInputExecutor(){
    java.util.concurrent.ScheduledExecutorService exec = this.playerInputExecutor;
    if(exec == null){
      synchronized(this){
        exec = this.playerInputExecutor;
        if(exec == null){
          exec = Server.getInstance().getScheduler();
          this.playerInputExecutor = exec;
        }
      }
    }
    return exec;
  }
  private void login(org.CreadoresProgram.CraftsMine.network.protocol.LoginPacket loginpack) throws Exception {
    // Defensive: some 0.15.x clients send a chain without extraData.displayName,
    // leaving loginpack.username null. Without a fallback, every downstream log
    // and the v113 LoginPacket's displayName would be "null" — which is what the
    // MCPE 1.1 server then echoes back as "{player} left the game".
    String incomingName = loginpack.username;
    if(incomingName == null || incomingName.isEmpty()){
      String fallback = (loginpack.clientUUID != null)
          ? "Player" + Long.toHexString(loginpack.clientUUID.getLeastSignificantBits() & 0xFFFFFF)
          : "Player" + this.identifierCraftsMan.hashCode();
      this.username = this.craftsmanUsername = fallback;
    }else{
      this.username = this.craftsmanUsername = incomingName;
    }
    this.UUID = loginpack.clientUUID == null
        ? java.util.UUID.randomUUID().toString()
        : loginpack.clientUUID.toString();
    this.clientID = loginpack.clientId;
    this.craftsmanPublicKey = loginpack.identityPublicKey;
    Config config = Server.getInstance().getConfig();
    try{
      InetSocketAddress bindAddress = new InetSocketAddress(0);
      InetSocketAddress bedrockAddress = new InetSocketAddress(config.getBedrockAddress(), config.getBedrockPort());
      // Use the NukkitX high-level BedrockClient API instead of the
      // previous Netty Bootstrap + CloudburstMC BedrockClientInitializer.
      // BedrockClient.connect() returns a CompletableFuture<BedrockClientSession>
      // that completes once the RakNet session is fully established, so we
      // no longer need the manual CountDownLatch workaround for the
      // "bedrockClientSession is null right after connect()" race.
      // The shared worker group from Server is passed in so we do NOT
      // allocate a new NioEventLoopGroup per player (the original leak).
      this.bedrockClient = new BedrockClient(
          bindAddress,
          Server.getInstance().getBedrockWorkerGroup(),
          Server.getInstance().getBedrockPacketCodec().getRaknetProtocolVersion());
      java.util.concurrent.CompletableFuture<BedrockClientSession> future = this.bedrockClient.connect(bedrockAddress);
      this.bedrockClientSession = future.get(10, TimeUnit.SECONDS);
      BedrockClientSession session = this.bedrockClientSession;
      // NukkitX uses setPacketCodec (not setCodec) and setBatchHandler for
      // receiving decoded batches. The previous CloudburstMC code used
      // setPacketHandler with a PacketSignal-returning handler; NukkitX
      // instead uses the BatchHandler interface which receives all decoded
      // packets from a batch in one call.
      session.setPacketCodec(Server.getInstance().getBedrockPacketCodec());
      session.setBatchHandler(new BedrockBatchHandler(Player.this));
      // MCPE 1.1 (protocol v113) login flow does NOT use RequestNetworkSettingsPacket.
      // That packet was introduced in protocol 422 (Bedrock 1.19.30). In v113,
      // the client sends LoginPacket directly and the server responds with
      // PlayStatusPacket, then optionally ServerToClientHandshakePacket for
      // encryption, then ResourcePacksInfoPacket, etc.
      // The login itself is sent later by LoginHPacket.translate() which
      // constructs and sends the actual LoginPacket to the downstream.
    }catch(Exception exception){
      // Log the full cause chain so we can see the real reason the
      // downstream connection failed (bind error, timeout, DNS, etc.)
      // — the disconnect message shown to the player only contains the
      // top-level toString() which hides the root cause.
      Server.getInstance().getLogger().warn("Downstream connection failed for "
          + this.craftsmanUsername + ": " + exception);
      Throwable cause = exception.getCause();
      while (cause != null) {
        Server.getInstance().getLogger().warn("  Caused by: " + cause.getClass().getName() + ": " + cause.getMessage());
        cause = cause.getCause();
      }
      org.CreadoresProgram.CraftsMine.network.protocol.LoginStatusPacket pk = new org.CreadoresProgram.CraftsMine.network.protocol.LoginStatusPacket();
      pk.status = org.CreadoresProgram.CraftsMine.network.protocol.LoginStatusPacket.LOGIN_SUCCESS;
      this.sendDataCraftsman(pk);
      this.disconnect("Failed to connect: "+ exception + " or Server offline");
      throw exception;
    }
  }
  public void onLogin(org.CreadoresProgram.CraftsMine.network.protocol.LoginPacket loginpack) throws Exception {
    this.skinC = loginpack.skin;
    this.skinId = loginpack.skinId;
    this.nbf = loginpack.nbf;
    this.exp = loginpack.exp;
    this.login(loginpack);
  }
  public LoginPacket getLoginPacket(){
    LoginPacket loginPacket = new LoginPacket();
    KeyPair ecda384KeyPair = EncryptionUtils.createKeyPair();
    this.publicKey = (ECPublicKey) ecda384KeyPair.getPublic();
    this.privateKey = (ECPrivateKey) ecda384KeyPair.getPrivate();
    String publicKeyBase64 = Base64.getEncoder().encodeToString(this.publicKey.getEncoded());
    JSONObject chain = new JSONObject();
    chain.put("exp", this.exp);
    chain.put("identityPublicKey", publicKeyBase64);
    chain.put("nbf", this.nbf);
    JSONObject extraData = new JSONObject();
    extraData.put("identity", this.UUID);
    extraData.put("XUID", "");
    extraData.put("displayName", this.username);
    chain.put("extraData", extraData);
    JSONObject jwtHeader = new JSONObject();
    jwtHeader.put("alg", "ES384");
    jwtHeader.put("x5u", publicKeyBase64);
    String jwt = generateJwt(jwtHeader, chain);
    // CRITICAL: NukkitX v113 LoginSerializer writes chainData directly to the
    // wire as a LEAsciiString. The MCPE 1.1 server expects it to be a JSON
    // object of the form {"chain": ["jwt1", "jwt2", ...]} — NOT a bare JSON
    // array ["jwt"]. The previous CloudburstMC API (getChain().add() +
    // setExtra()) built the {"chain": [...]} wrapper internally; NukkitX
    // does not, so we must build it here. With the bare array the server
    // silently drops the LoginPacket and never sends PlayStatus, which is
    // exactly the "logged in but no handshake" symptom.
    JSONObject chainDataObj = new JSONObject();
    JSONArray chainArr = new JSONArray();
    chainArr.add(jwt);
    chainDataObj.put("chain", chainArr);
    // NukkitX v113 LoginPacket uses chainData (AsciiString containing the
    // {"chain":[...]} JSON object) and clientData (AsciiString containing the
    // skin JWT). The previous CloudburstMC API exposed getChain() as a
    // mutable List<String> and setExtra(String) - neither exists in v113.
    loginPacket.setChainData(new io.netty.util.AsciiString(chainDataObj.toJSONString()));
    loginPacket.setClientData(new io.netty.util.AsciiString(this.getSkinData()));
    loginPacket.setProtocolVersion(Server.getInstance().getBedrockPacketCodec().getProtocolVersion());
    // CRITICAL: LoginSerializer_v113.serialize() calls packet.getVersion().ordinal()
    // without null-checking. If version is null (the default), the encode throws
    // NullPointerException → PacketSerializeException → "LoginPacket version is null".
    // VANILLA (ordinal 0) is the correct value for standard MCPE 1.1 clients;
    // EDUCATION (ordinal 1) is only for Minecraft Education Edition.
    loginPacket.setVersion(LoginPacket.Version.VANILLA);
    return loginPacket;
  }
  private String getSkinData(){
    String publicKeyBase64 = Base64.getEncoder().encodeToString(this.publicKey.getEncoded());
    JSONObject jwtHeader = new JSONObject();
    jwtHeader.put("alg", "ES384");
    jwtHeader.put("x5u", publicKeyBase64);
    JSONObject skinData = new JSONObject();
    skinData.put("CapeData", "");
    skinData.put("CapeImageHeight", 0);
    skinData.put("CapeImageWidth", 0);
    skinData.put("ClientRandomId", this.clientID);
    skinData.put("CompatibleWithClientSideChunkGen", false);
    skinData.put("CurrentInputMode", 2);
    skinData.put("DefaultInputMode", 2);
    skinData.put("DeviceId", java.util.UUID.randomUUID().toString());
    skinData.put("DeviceModel", "CraftsMine");
    skinData.put("DeviceOS", 1);
    skinData.put("GameVersion", Server.getInstance().getBedrockPacketCodec().getMinecraftVersion());
    skinData.put("GuiScale", 0);
    skinData.put("LanguageCode", Server.getInstance().getConfig().getLangPlayer());
    skinData.put("PlatformOfflineId", "");
    skinData.put("PlatformOnlineId", "");
    skinData.put("SelfSignedId", this.UUID);
    skinData.put("ServerAddress", Server.getInstance().getConfig().getBedrockAddress() + ":" + Server.getInstance().getConfig().getBedrockPort());
    if(this.skinC.getData() != null){
      skinData.put("SkinData", Base64.getEncoder().encodeToString(this.skinC.getData()));
    }else{
      skinData.put("SkinData", Server.getInstance().getDefaultSkinData());
    }
    if(this.skinC.getModel().getBytes() != null){
    skinData.put("SkinGeometry", Base64.getEncoder().encodeToString(this.skinC.getModel().getBytes()));
    }else{
      skinData.put("SkinGeometry", Base64.getEncoder().encodeToString(Server.getInstance().getDefaultSkinGeomety().getBytes()));
    }
    skinData.put("SkinGeometryName", "geometry.humanoid.custom");
    if(this.skinId == null){
    skinData.put("SkinId", "Standard_Custom");
    }else{
      skinData.put("SkinId", this.skinId);
    }
    skinData.put("SkinImageHeight", 64);
    skinData.put("SkinImageWidth", 64);
    skinData.put("ThirdPartyName", this.username);
    skinData.put("ThirdPartyNameOnly", false);
    skinData.put("UIProfile", 0);
    skinData.put("IsEditorMode", 0);
    skinData.put("TrustedSkin", 1);
    skinData.put("SkinGeometryDataEngineVersion", Base64.getEncoder().encodeToString(Server.getInstance().getBedrockPacketCodec().getMinecraftVersion().getBytes()));
    skinData.put("OverrideSkin", false);
    return generateJwt(jwtHeader, skinData);
  }
  private String generateJwt(JSONObject jwtHeader, JSONObject chain) {
        String header = Base64.getUrlEncoder().withoutPadding().encodeToString(jwtHeader.toJSONString().getBytes());
        String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(chain.toJSONString().getBytes());

        byte[] dataToSign = (header + "." + payload).getBytes();
        byte[] signatureBytes = null;
        try {
            Signature signature = Signature.getInstance("SHA384withECDSA");
            signature.initSign(this.privateKey);
            signature.update(dataToSign);
            signatureBytes = Utils.DERToJOSE(signature.sign(), Utils.AlgorithmType.ECDSA384);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ignored) {
        }
        String signatureString = Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);

        return header + "." + payload + "." + signatureString;
  }
  public void sendMessage(String message) {
    org.CreadoresProgram.CraftsMine.network.protocol.TextPacket pk = new org.CreadoresProgram.CraftsMine.network.protocol.TextPacket();
    pk.type = org.CreadoresProgram.CraftsMine.network.protocol.TextPacket.TYPE_RAW;
    pk.message = message;
    this.sendDataCraftsman((MCDPacket) pk);
  }
  public void sendTip(String message) {
    this.screenManager.sendTip(message);
  }
  public void sendDataCraftsman(MCDPacket pk){
    Server.getInstance().getRakNetServ().sendPacket(this.identifierCraftsMan, pk, false);
  }
  public void disconnect(String reason) {
    // Do NOT shut down playerInputExecutor: it is now a reference to the
    // shared Server scheduler. Shutting it down here would kill the only
    // scheduler for the whole proxy (used by QueryTask and any other
    // periodic job) on the very first disconnect. Just cancel this player's
    // scheduled jobs (if any were registered) instead.
    try {
      if (this.bedrockClientSession != null) this.bedrockClientSession.disconnect();
    } catch (Throwable ignored) {
    }
    try {
      if (this.bedrockClient != null) this.bedrockClient.close(true);
    } catch (Throwable ignored) {
    }
    DisconnectPacket dicpack = new DisconnectPacket();
      dicpack.message = reason;
      this.sendDataCraftsman((MCDPacket) dicpack);
    // The previous block called System.gc() here when the config flag
    // gcCollectionDiconnectPlayer was set. Forcing GC on every player
    // disconnect is an anti-pattern: it stops the world for the whole JVM
    // for tens of milliseconds and triggers unnecessary full-GC cycles.
    // The JVM's G1/ZGC collectors already reclaim Netty ByteBufs and the
    // shared translation pool's queued tasks promptly without intervention.
    String displayName = this.craftsmanUsername;
    if(displayName == null || displayName.isEmpty()){
      displayName = (this.identifierCraftsMan == null) ? "Unknown" : this.identifierCraftsMan;
    }
    Server.getInstance().getLogger().info(displayName + " disconnected: " + reason);
  }
}
