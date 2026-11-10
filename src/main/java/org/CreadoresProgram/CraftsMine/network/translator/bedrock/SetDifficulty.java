package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.SetDifficultyPacket;

/** v113 SetDifficultyPacket (DifficultyType) → 0.15.x SetDifficultyPacket (int). */
public class SetDifficulty implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    SetDifficultyPacket packet = (SetDifficultyPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.SetDifficultyPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.SetDifficultyPacket();
    // DifficultyType enum ordinal matches the 0.15.x difficulty int values
    // (PEACEFUL=0, EASY=1, NORMAL=2, HARD=3).
    npk.difficulty = packet.getDifficulty() == null ? 1 : packet.getDifficulty().ordinal();
    player.sendDataCraftsman(npk);
  }
}
