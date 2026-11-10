package org.CreadoresProgram.CraftsMine.network.translator.craftsman;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.CraftsmanPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.packet.SetDifficultyPacket;
import com.nukkitx.protocol.bedrock.data.DifficultyType;

/** 0.15.x SetDifficultyPacket (int) → v113 SetDifficultyPacket (DifficultyType). */
public class SetDifficulty implements CraftsmanPacketTranslator {
  @Override
  public void translate(MCDPacket pk, Player player){
    org.CreadoresProgram.CraftsMine.network.protocol.SetDifficultyPacket packet =
        (org.CreadoresProgram.CraftsMine.network.protocol.SetDifficultyPacket) pk;
    SetDifficultyPacket npk = new SetDifficultyPacket();
    DifficultyType[] values = DifficultyType.values();
    int ordinal = Math.max(0, Math.min(values.length - 1, packet.difficulty));
    npk.setDifficulty(values[ordinal]);
    player.getBedrockClientSession().sendPacket(npk);
  }
}
