package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.SetTitlePacket;

/**
 * v113 SetTitlePacket (Type, text, fadeIn/stay/fadeOut times) — 0.15.x has
 * no SetTitlePacket. As a workaround, we send a TextPacket with TYPE_SYSTEM
 * so the player at least sees the title text in chat.
 */
public class SetTitle implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    SetTitlePacket packet = (SetTitlePacket) pk;
    if (packet.getText() == null || packet.getText().isEmpty()) return;
    // Send the title text as a system chat message as a fallback.
    org.CreadoresProgram.CraftsMine.network.protocol.TextPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.TextPacket();
    npk.type = org.CreadoresProgram.CraftsMine.network.protocol.TextPacket.TYPE_SYSTEM;
    npk.message = packet.getText();
    player.sendDataCraftsman(npk);
  }
}
