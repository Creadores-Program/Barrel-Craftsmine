package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.MobEquipmentPacket;
import org.CreadoresProgram.CraftsMine.inventory.Item;

/**
 * v113 MobEquipmentPacket (runtimeEntityId, ItemData item, inventorySlot,
 * hotbarSlot, containerId) → 0.15.x MobEquipmentPacket (eid, Item item,
 * int slot, int selectedSlot).
 *
 * <p>0.15.x has no containerId field; it is dropped. Item conversion:
 * id/damage/count carried over, NBT dropped (same approach as AddPlayer).</p>
 */
public class MobEquipment implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    MobEquipmentPacket packet = (MobEquipmentPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.MobEquipmentPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.MobEquipmentPacket();
    npk.eid = packet.getRuntimeEntityId();
    com.nukkitx.protocol.bedrock.data.inventory.ItemData item = packet.getItem();
    if (item != null && item.getId() > 0) {
      npk.item = new Item(item.getId(), item.getDamage(), item.getCount(), new byte[0]);
    } else {
      npk.item = new Item(0, 0, 0, new byte[0]);
    }
    // 0.15.x wire format (per Nukkit-Retro MobEquipmentPacket encode for
    // protocol < 1.6.0): putLong(eid) + putSlot(item) + putByte(slot)
    // + putByte(selectedSlot). The 0.15.x `slot` field is the inventory
    // slot the item occupies; `selectedSlot` is the hotbar slot that is
    // currently highlighted on the client.
    //
    // v113 MobEquipmentPacket fields (per NukkitX v113 source):
    //   - inventorySlot: where the item lives in the inventory
    //   - hotbarSlot: which hotbar slot is currently selected
    //
    // The previous translator set BOTH 0.15.x fields to hotbarSlot, which
    // desynced the client's inventory slot tracking: when the player
    // switched hotbar slots the client thought the item moved to a
    // different inventory slot, eventually triggering slot-mismatch
    // crashes when the player tried to use/equip the item.
    npk.slot = packet.getInventorySlot();
    npk.selectedSlot = packet.getHotbarSlot();
    player.sendDataCraftsman(npk);
  }
}
