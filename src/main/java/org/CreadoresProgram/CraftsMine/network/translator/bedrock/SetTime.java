package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.SetTimePacket;

/** v113 SetTimePacket (int time) → 0.15.x SetTimePacket (int time, boolean started=true). */
public class SetTime implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    SetTimePacket packet = (SetTimePacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.SetTimePacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.SetTimePacket();
    npk.time = packet.getTime();
    npk.started = true;
    player.sendDataCraftsman(npk);
  }
}
