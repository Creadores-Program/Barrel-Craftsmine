package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.TextPacket;

/**
 * Translates a v113 TextPacket (server broadcasts chat/system/popup/tip
 * messages) into the 0.15.x TextPacket and forwards it to the upstream
 * client.
 *
 * <p>v113 TextPacket fields: type (enum), sender (String), message (String),
 * parameters (List&lt;String&gt;). The v113 enum values match the 0.15.x
 * byte constants: RAW=0, CHAT=1, TRANSLATION=2, POPUP=3, TIP=4, SYSTEM=5,
 * WHISPER=6, ANNOUNCEMENT=7. The 0.15.x client supports up to SYSTEM=5;
 * WHISPER/ANNOUNCEMENT are clamped to CHAT.</p>
 */
public class TextPack implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    TextPacket packet = (TextPacket) pk;
    // Null/type guard: a v113 server must never send a TextPacket with a
    // null type, but a malformed one would NPE the switch below and kill
    // this player's translate queue. Treat null type as RAW.
    if (packet.getType() == null) {
      org.CreadoresProgram.CraftsMine.network.protocol.TextPacket npk =
          new org.CreadoresProgram.CraftsMine.network.protocol.TextPacket();
      npk.type = org.CreadoresProgram.CraftsMine.network.protocol.TextPacket.TYPE_RAW;
      npk.source = "";
      npk.message = packet.getMessage() == null ? "" : packet.getMessage();
      npk.parameters = new String[0];
      player.sendDataCraftsman(npk);
      return;
    }
    org.CreadoresProgram.CraftsMine.network.protocol.TextPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.TextPacket();
    byte type;
    switch(packet.getType()){
      case RAW: type = org.CreadoresProgram.CraftsMine.network.protocol.TextPacket.TYPE_RAW; break;
      case CHAT: type = org.CreadoresProgram.CraftsMine.network.protocol.TextPacket.TYPE_CHAT; break;
      case TRANSLATION: type = org.CreadoresProgram.CraftsMine.network.protocol.TextPacket.TYPE_TRANSLATION; break;
      case POPUP: type = org.CreadoresProgram.CraftsMine.network.protocol.TextPacket.TYPE_POPUP; break;
      case TIP: type = org.CreadoresProgram.CraftsMine.network.protocol.TextPacket.TYPE_TIP; break;
      case SYSTEM: type = org.CreadoresProgram.CraftsMine.network.protocol.TextPacket.TYPE_SYSTEM; break;
      // WHISPER and ANNOUNCEMENT don't exist in 0.15.x; clamp to CHAT so
      // the message is at least visible to the client.
      case WHISPER:
      case ANNOUNCEMENT:
      default:
        type = org.CreadoresProgram.CraftsMine.network.protocol.TextPacket.TYPE_CHAT;
        break;
    }
    npk.type = type;
    // v113 sender corresponds to 0.15.x source (used for CHAT/POPUP types).
    npk.source = packet.getSender() == null ? "" : packet.getSender();
    npk.message = packet.getMessage() == null ? "" : packet.getMessage();
    if(packet.getParameters() != null && !packet.getParameters().isEmpty()){
      npk.parameters = packet.getParameters().toArray(new String[0]);
    } else {
      npk.parameters = new String[0];
    }
    player.sendDataCraftsman(npk);
  }
}
