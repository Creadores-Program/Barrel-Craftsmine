package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.SetPlayerGameTypePacket;

/** v113 SetPlayerGameTypePacket (GameType) → 0.15.x SetPlayerGameTypePacket (int gamemode). */
public class SetPlayerGameType implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    SetPlayerGameTypePacket packet = (SetPlayerGameTypePacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.SetPlayerGameTypePacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.SetPlayerGameTypePacket();
    // GameType enum ordinal matches 0.15.x gamemode ints
    // (SURVIVAL=0, CREATIVE=1, ADVENTURE=2, SPECTATOR=3).
    npk.gamemode = packet.getGameType() == null ? 0 : packet.getGameType().ordinal();
    player.sendDataCraftsman(npk);
  }
}
