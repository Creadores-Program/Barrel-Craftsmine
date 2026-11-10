package org.CreadoresProgram.CraftsMine.network.translator.craftsman;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.CraftsmanPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.packet.RequestChunkRadiusPacket;

/**
 * Translates a 0.15.x RequestChunkRadiusPacket (sent by the upstream client
 * after StartGamePacket to ask the server for chunks within a given radius)
 * into the v113 RequestChunkRadiusPacket and forwards it to the MCPE 1.1
 * downstream server.
 *
 * <p>Both packets carry a single int {@code radius} field.</p>
 */
public class RequestChunkRadius implements CraftsmanPacketTranslator {
  @Override
  public void translate(MCDPacket pk, Player player){
    org.CreadoresProgram.CraftsMine.network.protocol.RequestChunkRadiusPacket packet =
        (org.CreadoresProgram.CraftsMine.network.protocol.RequestChunkRadiusPacket) pk;
    RequestChunkRadiusPacket npk = new RequestChunkRadiusPacket();
    npk.setRadius(packet.radius);
    player.getBedrockClientSession().sendPacket(npk);
  }
}
