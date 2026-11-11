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
      // Serialize the NbtMap in LITTLE-ENDIAN standard NBT format WITH
      // root name (createWriterLE), NOT the network format (createNetworkWriter).
      //
      // CRITICAL FIX (verified against MySoft/PocketMine 0.15.x):
      // MySoft Player.php line 4879:
      //   $nbt = $this->getProtocol() >= ProtocolInfo::PROTOCOL_90
      //       ? new NetworkLittleEndianNBTStream()   // 0.16+: NO root name
      //       : new LittleEndianNBTStream();          // 0.15.x: WITH root name
      //
      // MCPE 0.15.x (protocol 81) expects block entity NBT in the
      // STANDARD little-endian format that includes the root tag name
      // (typically an empty string, encoded as short 0x0000 + 0 bytes).
      // The v113 network format (createNetworkWriter) OMITS the root
      // name entirely — the 0.15.x client would then misinterpret the
      // first tag-type byte as the root-name length short, read garbage,
      // and crash while parsing the block entity NBT during chunk
      // rendering (chest, sign, hopper, etc.).
      ByteBuf buf = Unpooled.buffer();
      try (NBTOutputStream writer = NbtUtils.createWriterLE(
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
