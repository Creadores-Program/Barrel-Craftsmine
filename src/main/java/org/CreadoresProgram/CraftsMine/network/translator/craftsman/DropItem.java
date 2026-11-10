package org.CreadoresProgram.CraftsMine.network.translator.craftsman;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.CraftsmanPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.packet.PlayerActionPacket;
import com.nukkitx.protocol.bedrock.data.PlayerActionType;
import com.nukkitx.math.vector.Vector3i;

/**
 * 0.15.x DropItemPacket (type, Item) → v113 PlayerActionPacket con action
 * DROP_ITEM. Soltar item al suelo (tecla Q).
 */
public class DropItem implements CraftsmanPacketTranslator {
  @Override
  public void translate(MCDPacket pk, Player player){
    org.CreadoresProgram.CraftsMine.network.protocol.DropItemPacket packet =
        (org.CreadoresProgram.CraftsMine.network.protocol.DropItemPacket) pk;
    if (packet.type != 0) {
      // Drop desde cursor de inventario — no se puede traducir a
      // PlayerAction(DROP_ITEM). Se descarta; InventoryAction (pendiente)
      // cubrirá este caso.
      return;
    }
    PlayerActionPacket npk = new PlayerActionPacket();
    npk.setRuntimeEntityId(player.getRuntimeEntityId());
    npk.setAction(PlayerActionType.DROP_ITEM);
    npk.setBlockPosition(Vector3i.from(0, 0, 0));
    npk.setFace(0);
    player.getBedrockClientSession().sendPacket(npk);
  }
}
