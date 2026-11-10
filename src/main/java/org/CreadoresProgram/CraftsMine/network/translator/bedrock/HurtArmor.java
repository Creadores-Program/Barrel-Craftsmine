package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.HurtArmorPacket;

/** v113 HurtArmorPacket (int damage) → 0.15.x HurtArmorPacket (byte health). Clamp to 0..255. */
public class HurtArmor implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    HurtArmorPacket packet = (HurtArmorPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.HurtArmorPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.HurtArmorPacket();
    int dmg = packet.getDamage();
    npk.health = (byte) Math.max(0, Math.min(255, dmg));
    player.sendDataCraftsman(npk);
  }
}
