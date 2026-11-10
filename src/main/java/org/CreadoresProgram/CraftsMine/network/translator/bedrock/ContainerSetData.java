package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.ContainerSetDataPacket;

/**
 * v113 ContainerSetDataPacket (byte windowId, int property, int value) →
 * 0.15.x WindowDataPacket (byte windowid, int property, int value).
 *
 * <p>Wire difference: v113 uses VarInt32 for property/value;
 * 0.15.x uses putShort for both. The field values are the same integers.</p>
 */
public class ContainerSetData implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    ContainerSetDataPacket packet = (ContainerSetDataPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.WindowDataPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.WindowDataPacket();
    npk.windowid = packet.getWindowId();
    npk.property = packet.getProperty();
    npk.value = packet.getValue();
    player.sendDataCraftsman(npk);
  }
}
