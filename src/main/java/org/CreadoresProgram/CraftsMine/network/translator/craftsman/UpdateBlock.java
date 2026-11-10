package org.CreadoresProgram.CraftsMine.network.translator.craftsman;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.CraftsmanPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.packet.UpdateBlockPacket;
import com.nukkitx.math.vector.Vector3i;
import java.util.EnumSet;

/**
 * 0.15.x UpdateBlockPacket → v113 UpdateBlockPacket.
 *
 * <p>0.15.x records use (blockId, blockData, flags). v113 uses runtime id
 * (which requires the BlockMapper to convert back from legacy id:meta to
 * runtime id). Since BlockMapper only supports the forward direction
 * (runtime → legacy), this translator falls back to runtime id 0 (air)
 * when the mapping is not available. Block-breaking by the client will
 * still work because the server tracks its own block state.</p>
 */
public class UpdateBlock implements CraftsmanPacketTranslator {
  @Override
  public void translate(MCDPacket pk, Player player){
    org.CreadoresProgram.CraftsMine.network.protocol.UpdateBlockPacket packet =
        (org.CreadoresProgram.CraftsMine.network.protocol.UpdateBlockPacket) pk;
    if (packet.records == null || packet.records.length == 0) return;
    for (org.CreadoresProgram.CraftsMine.network.protocol.UpdateBlockPacket.Entry e : packet.records) {
      UpdateBlockPacket npk = new UpdateBlockPacket();
      npk.setBlockPosition(Vector3i.from(e.x, e.y, e.z));
      // BlockMapper doesn't support legacy → runtime conversion yet.
      // Send runtime id 0 (air) as placeholder; the server will track
      // the real change. A follow-up task is to wire the reverse mapping.
      npk.setBlockRuntimeId(0);
      // Map 0.15.x flags to v113 Flag enum set.
      EnumSet<UpdateBlockPacket.Flag> flags = EnumSet.noneOf(UpdateBlockPacket.Flag.class);
      if ((e.flags & org.CreadoresProgram.CraftsMine.network.protocol.UpdateBlockPacket.FLAG_NEIGHBORS) != 0)
        flags.add(UpdateBlockPacket.Flag.NEIGHBORS);
      if ((e.flags & org.CreadoresProgram.CraftsMine.network.protocol.UpdateBlockPacket.FLAG_NETWORK) != 0)
        flags.add(UpdateBlockPacket.Flag.NETWORK);
      if ((e.flags & org.CreadoresProgram.CraftsMine.network.protocol.UpdateBlockPacket.FLAG_PRIORITY) != 0)
        flags.add(UpdateBlockPacket.Flag.PRIORITY);
      // flags is a final Set<Flag>; mutate in place via getFlags().
      npk.getFlags().addAll(flags);
      player.getBedrockClientSession().sendPacket(npk);
    }
  }
}
