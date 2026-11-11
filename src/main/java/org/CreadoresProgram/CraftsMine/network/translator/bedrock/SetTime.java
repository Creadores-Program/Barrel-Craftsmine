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
    // Normalize into the 0..23999 range the client expects (the field is
    // documented as "time of the day 0..24000"; v113 servers can send
    // negative values from plugins like /time set -6000 or raw doDaylightCycle
    // offsets, which the legacy client does not clamp itself).
    int time = packet.getTime() % 24000;
    if (time < 0) time += 24000;
    npk.time = time;
    npk.started = true;
    player.sendDataCraftsman(npk);
  }
}
