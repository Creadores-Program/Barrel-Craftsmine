package org.CreadoresProgram.CraftsMine.network.translator.craftsman;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.CraftsmanPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.packet.UseItemPacket;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;

/**
 * 0.15.x UseItemPacket → v113 UseItemPacket. Click derecho: colocar bloque,
 * abrir puerta, usar cubeta/mechero. Mapeo casi 1:1 (mismo periodo).
 */
public class UseItem implements CraftsmanPacketTranslator {
  @Override
  public void translate(MCDPacket pk, Player player){
    org.CreadoresProgram.CraftsMine.network.protocol.UseItemPacket packet =
        (org.CreadoresProgram.CraftsMine.network.protocol.UseItemPacket) pk;
    UseItemPacket npk = new UseItemPacket();
    npk.setBlockPosition(Vector3i.from(packet.x, packet.y, packet.z));
    npk.setFace(packet.face & 0xff);
    npk.setFacePosition(Vector3f.from(packet.fx, packet.fy, packet.fz));
    npk.setPosition(Vector3f.from(packet.posX, packet.posY, packet.posZ));
    npk.setHotbarSlot(packet.slot);
    npk.setSlot(packet.slot);
    org.CreadoresProgram.CraftsMine.inventory.Item item = packet.item;
    if (item != null && item.realId > 0) {
      npk.setItem(ItemData.builder()
          .id(item.realId)
          .damage(item.realData)
          .count(((Integer) item.itemIns[2]))
          .build());
    } else {
      npk.setItem(ItemData.AIR);
    }
    player.getBedrockClientSession().sendPacket(npk);
  }
}
