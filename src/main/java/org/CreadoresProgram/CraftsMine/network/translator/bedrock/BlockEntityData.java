package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.BlockEntityDataPacket;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NBTOutputStream;
import java.io.ByteArrayOutputStream;

/**
 * v113 BlockEntityDataPacket (Vector3i blockPosition, NbtMap data) →
 * 0.15.x BlockEntityDataPacket (int x,y,z, byte[] namedTag).
 *
 * <p>NbtMap.toByteArray() returns the serialised NBT payload in the
 * little-endian format that 0.15.x expects.</p>
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
      // NbtMap has no toByteArray() method; serialize via NBTOutputStream
      // which writes the little-endian NBT payload the 0.15.x client
      // expects in the namedTag field.
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try (NBTOutputStream out = new NBTOutputStream(new java.io.DataOutputStream(baos))) {
        out.writeTag(data);
      }
      npk.namedTag = baos.toByteArray();
    } catch (Throwable t) {
      // If serialization fails (rare), drop the packet rather than
      // crashing the translator thread.
      return;
    }
    player.sendDataCraftsman(npk);
  }
}
