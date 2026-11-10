package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.ChunkRadiusUpdatedPacket;

/**
 * Translates a v113 ChunkRadiusUpdatedPacket (server confirms the chunk
 * radius the client will receive) into the 0.15.x ChunkRadiusUpdatedPacket.
 *
 * <p>Both packets are identical: a single int {@code radius} field.</p>
 */
public class ChunkRadiusUpdated implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    ChunkRadiusUpdatedPacket packet = (ChunkRadiusUpdatedPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.ChunkRadiusUpdatedPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.ChunkRadiusUpdatedPacket();
    npk.radius = packet.getRadius();
    player.sendDataCraftsman(npk);
  }
}
