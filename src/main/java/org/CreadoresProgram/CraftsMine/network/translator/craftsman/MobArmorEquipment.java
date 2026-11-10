package org.CreadoresProgram.CraftsMine.network.translator.craftsman;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.CraftsmanPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.packet.MobArmorEquipmentPacket;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;

/**
 * 0.15.x MobArmorEquipmentPacket (eid, Item[4] slots) → v113
 * MobArmorEquipmentPacket (runtimeEntityId, helmet, chestplate, leggings, boots).
 */
public class MobArmorEquipment implements CraftsmanPacketTranslator {
  @Override
  public void translate(MCDPacket pk, Player player){
    org.CreadoresProgram.CraftsMine.network.protocol.MobArmorEquipmentPacket packet =
        (org.CreadoresProgram.CraftsMine.network.protocol.MobArmorEquipmentPacket) pk;
    MobArmorEquipmentPacket npk = new MobArmorEquipmentPacket();
    npk.setRuntimeEntityId(packet.eid);
    npk.setHelmet(toItemData(packet.slots.length > 0 ? packet.slots[0] : null));
    npk.setChestplate(toItemData(packet.slots.length > 1 ? packet.slots[1] : null));
    npk.setLeggings(toItemData(packet.slots.length > 2 ? packet.slots[2] : null));
    npk.setBoots(toItemData(packet.slots.length > 3 ? packet.slots[3] : null));
    player.getBedrockClientSession().sendPacket(npk);
  }

  private static ItemData toItemData(org.CreadoresProgram.CraftsMine.inventory.Item item) {
    if (item == null || item.realId <= 0) return ItemData.AIR;
    return ItemData.builder().id(item.realId).damage(item.realData).count(((Integer) item.itemIns[2])).build();
  }
}
