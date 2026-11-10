package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.UpdateBlockPacket;
import com.nukkitx.math.vector.Vector3i;

/**
 * Translates a v113 UpdateBlockPacket (server tells client a block changed)
 * into the 0.15.x UpdateBlockPacket and forwards it to the upstream client.
 *
 * <p>v113 identifies blocks by a runtime id that maps into the server's
 * block palette (NBT state hash → runtime id). 0.15.x uses raw
 * blockId:blockData byte pairs. The conversion is done by
 * {@link org.CreadoresProgram.CraftsMine.utils.BlockMapper} which was
 * ported from the Dr1xyDev fork and resolves runtime id → (blockId, blockData).</p>
 *
 * <p>If BlockMapper is not available (not yet wired on the Player) or
 * returns an unknown block, the translator drops the packet rather than
 * sending a wrong block id to the 0.15.x client.</p>
 */
public class UpdateBlock implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    UpdateBlockPacket packet = (UpdateBlockPacket) pk;
    Vector3i pos = packet.getBlockPosition();
    if (pos == null) return;
    int runtimeId = packet.getBlockRuntimeId();

    // Resolve runtime id → (blockId, blockData) via the player's BlockMapper.
    org.CreadoresProgram.CraftsMine.utils.BlockMapper bm = player.getBlockMapper();
    if (bm == null) {
      // BlockMapper not yet wired for this player; drop the update rather
      // than sending a wrong block id. Once Player.java initializes the
      // BlockMapper from the server's block palette, updates will flow.
      return;
    }
    int blockId;
    byte blockData;
    try {
      blockId = bm.getLegacyId(runtimeId);
      blockData = bm.getLegacyMeta(runtimeId);
    } catch (Throwable t) {
      return;
    }
    if (blockId < 0) return;

    // Build the 0.15.x UpdateBlockPacket with a single Entry.
    // 0.15.x wire: int x, int z, byte y, byte blockId,
    //              byte((flags<<4) | (blockData & 0xf)).
    // v113 sends flags as a Set<Flag>; map NEIGHBORS+NETWORK to FLAG_ALL
    // (which is what the 0.15.x client expects for a regular update).
    int flags = org.CreadoresProgram.CraftsMine.network.protocol.UpdateBlockPacket.FLAG_ALL;
    org.CreadoresProgram.CraftsMine.network.protocol.UpdateBlockPacket.Entry entry =
        new org.CreadoresProgram.CraftsMine.network.protocol.UpdateBlockPacket.Entry(
            pos.getX(), pos.getZ(), pos.getY(), blockId, blockData, flags);
    org.CreadoresProgram.CraftsMine.network.protocol.UpdateBlockPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.UpdateBlockPacket();
    npk.records = new org.CreadoresProgram.CraftsMine.network.protocol.UpdateBlockPacket.Entry[]{ entry };
    player.sendDataCraftsman(npk);
  }
}
