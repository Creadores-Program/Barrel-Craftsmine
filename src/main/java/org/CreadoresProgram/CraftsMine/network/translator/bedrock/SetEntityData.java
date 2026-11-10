package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.SetEntityDataPacket;
import java.util.HashMap;

/**
 * v113 SetEntityDataPacket → 0.15.x SetEntityDataPacket.
 *
 * <p>Metadata is forwarded as an empty map for now. The v113 EntityDataMap
 * uses a different key space (EntityData enum) than 0.15.x (raw int keys
 * with a (type<<5)|key header byte format). Wiring the bidirectional
 * metadata translator is a follow-up task; for now the entity will
 * update without visual metadata changes.</p>
 */
public class SetEntityData implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    SetEntityDataPacket packet = (SetEntityDataPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.SetEntityDataPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.SetEntityDataPacket();
    npk.eid = packet.getRuntimeEntityId();
    npk.metadata = new HashMap<>();
    player.sendDataCraftsman(npk);
  }
}
