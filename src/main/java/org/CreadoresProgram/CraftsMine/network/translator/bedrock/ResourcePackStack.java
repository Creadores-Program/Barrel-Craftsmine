package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.ResourcePackStackPacket;

/**
 * The MCPE 1.1 server sends ResourcePackStackPacket after the client ack'd
 * the ResourcePacksInfo. Since the proxy sent a COMPLETED response with no
 * packs, this packet contains an empty stack. The 0.15.x client cannot
 * consume v113 resource pack stacks, so the proxy simply acks by sending
 * another ResourcePackClientResponsePacket with COMPLETED status.
 *
 * <p>This advances the login flow past the resource pack phase without
 * involving the 0.15.x client.</p>
 */
public class ResourcePackStack implements BedrockPacketTranslator{
  @Override
  public boolean immediate(){ return true; }
  @Override
  public void translate(BedrockPacket pk, Player player){
    ResourcePackStackPacket packet = (ResourcePackStackPacket) pk;
    // Ack the stack by sending COMPLETED again. The v113 server will now
    // proceed to send StartGamePacket.
    com.nukkitx.protocol.bedrock.packet.ResourcePackClientResponsePacket resp =
        new com.nukkitx.protocol.bedrock.packet.ResourcePackClientResponsePacket();
    resp.setStatus(com.nukkitx.protocol.bedrock.packet.ResourcePackClientResponsePacket.Status.COMPLETED);
    // packIds is a final List<String>; default empty is what we want.
    player.getBedrockClientSession().sendPacketImmediately(resp);
  }
}
