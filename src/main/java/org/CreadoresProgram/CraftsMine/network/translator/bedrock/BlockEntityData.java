package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.BlockEntityDataPacket;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.nbt.NBTOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;

/**
 * v113 BlockEntityDataPacket (Vector3i blockPosition, NbtMap data) →
 * 0.15.x BlockEntityDataPacket (int x,y,z, byte[] namedTag).
 *
 * <p><b>CRITICAL FIX</b>: The previous implementation used
 * {@code new NBTOutputStream(new DataOutputStream(baos))} which writes
 * NBT in <b>BIG-ENDIAN</b> format (Java NBT convention). MCPE 0.15.x
 * expects <b>LITTLE-ENDIAN</b> network NBT for block entity data, the
 * same format used by the v113 BedrockPacketHelper for all NBT on the
 * wire. Sending big-endian NBT to the 0.15.x client causes it to
 * misparse the block entity payload — reading garbage tag types and
 * lengths — which crashes the client during chunk rendering when it
 * tries to instantiate the block entity (chest, sign, etc.) from the
 * corrupt NBT.</p>
 *
 * <p>The fix uses {@link NbtUtils#createNetworkWriter} which creates a
 * little-endian NBT writer, matching what the v113 BedrockPacketHelper
 * uses on the wire ({@code helper.writeTag()} internally calls
 * {@code NbtUtils.createNetworkWriter}).</p>
 */
public class BlockEntityData implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    BlockEntityDataPacket packet = (BlockEntityDataPacket) pk;
    Vector3i pos = packet.getBlockPosition();
    NbtMap data = packet.getData();
    if (pos == null || data == null) return;
    org.CreadoresProgram.CraftsMine.network.protocol.BlockEntityDataPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.BlockEntityDataPacket();
    npk.x = pos.getX();
    npk.y = pos.getY();
    npk.z = pos.getZ();
    try {
      // Serialize the NbtMap in LITTLE-ENDIAN network NBT format
      // (matching the v113 BedrockPacketHelper wire format and what
      // the 0.15.x client expects). We use a Netty ByteBuf as the
      // backing buffer because NbtUtils.createNetworkWriter expects
      // an OutputStream and ByteBufOutputStream is the standard Netty
      // adapter.
      ByteBuf buf = Unpooled.buffer();
      try (NBTOutputStream writer = NbtUtils.createNetworkWriter(
              new ByteBufOutputStream(buf))) {
        writer.writeTag(data);
      }
      int len = buf.readableBytes();
      npk.namedTag = new byte[len];
      buf.readBytes(npk.namedTag);
      buf.release();
    } catch (Throwable t) {
      // If serialization fails (rare), drop the packet rather than
      // crashing the translator thread or sending corrupt NBT.
      return;
    }
    player.sendDataCraftsman(npk);
  }
}
