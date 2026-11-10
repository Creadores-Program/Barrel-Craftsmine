package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.MobArmorEquipmentPacket;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import org.CreadoresProgram.CraftsMine.inventory.Item;

/**
 * v113 MobArmorEquipmentPacket (runtimeEntityId, ItemData helmet/chestplate/
 * leggings/boots) → 0.15.x MobArmorEquipmentPacket (eid, Item[4] slots).
 *
 * <p>0.15.x slots[0]=helmet, slots[1]=chestplate, slots[2]=leggings,
 * slots[3]=boots. Item NBT is dropped (id/damage/count only).</p>
 */
public class MobArmorEquipment implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    MobArmorEquipmentPacket packet = (MobArmorEquipmentPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.MobArmorEquipmentPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.MobArmorEquipmentPacket();
    npk.eid = packet.getRuntimeEntityId();
    npk.slots = new Item[4];
    npk.slots[0] = toItem(packet.getHelmet());
    npk.slots[1] = toItem(packet.getChestplate());
    npk.slots[2] = toItem(packet.getLeggings());
    npk.slots[3] = toItem(packet.getBoots());
    player.sendDataCraftsman(npk);
  }

  private static Item toItem(ItemData data){
    if (data == null || data.getId() <= 0) return new Item(0, 0, 0, new byte[0]);
    return new Item(data.getId(), data.getDamage(), data.getCount(), new byte[0]);
  }
}
