package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.ResourcePacksInfoPacket;

/**
 * The MCPE 1.1 server sends ResourcePacksInfoPacket during login to ask the
 * client which resource packs it wants. The 0.15.x client cannot handle v113
 * resource packs, so the proxy sends a ResourcePackClientResponsePacket with
 * COMPLETED status directly to the server, bypassing the client.
 *
 * <p>This lets the login flow proceed without resource pack negotiation
 * between the 0.15.x client and the v113 server.</p>
 */
public class ResourcePacksInfo implements BedrockPacketTranslator{
  @Override
  public boolean immediate(){ return true; }
  @Override
  public void translate(BedrockPacket pk, Player player){
    ResourcePacksInfoPacket packet = (ResourcePacksInfoPacket) pk;
    // Build a ResourcePackClientResponsePacket with status COMPLETED (2)
    // to tell the server "no packs needed, proceed". This is the response
    // a vanilla v113 client would send if it had no packs to download.
    com.nukkitx.protocol.bedrock.packet.ResourcePackClientResponsePacket resp =
        new com.nukkitx.protocol.bedrock.packet.ResourcePackClientResponsePacket();
    resp.setStatus(com.nukkitx.protocol.bedrock.packet.ResourcePackClientResponsePacket.Status.COMPLETED);
    // packIds is a final List<String>; Lombok doesn't generate a setter.
    // It is initialised to an empty ArrayList by default, so we leave it
    // empty (no packs requested).
    player.getBedrockClientSession().sendPacketImmediately(resp);
  }
}
