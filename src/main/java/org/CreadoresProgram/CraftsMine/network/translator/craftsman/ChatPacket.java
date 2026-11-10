package org.CreadoresProgram.CraftsMine.network.translator.craftsman;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.CraftsmanPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.packet.TextPacket;

/**
 * Translates a 0.15.x TextPacket (sent by the upstream client when it
 * sends a chat message) into the v113 TextPacket and forwards it to the
 * MCPE 1.1 downstream server.
 *
 * <p>0.15.x TextPacket fields: type (byte), source, message, parameters[].
 * The 0.15.x client sends TYPE_RAW for normal chat with the message field
 * populated; the proxy normalises to TYPE_CHAT for v113 so the server's
 * chat handler picks it up.</p>
 *
 * <p>v113 TextPacket has no sourceName/xuid/platformChatId fields (those
 * were added in v1.16+); the player's identity is conveyed only via the
 * downstream LoginPacket chain that the proxy already sent. The v113
 * "sender" field corresponds to the 0.15.x "source" field.</p>
 */
public class ChatPacket implements CraftsmanPacketTranslator{
  @Override
  public void translate(MCDPacket pk, Player player){
    org.CreadoresProgram.CraftsMine.network.protocol.TextPacket chatPacket =
        (org.CreadoresProgram.CraftsMine.network.protocol.TextPacket) pk;
    TextPacket textPacket = new TextPacket();
    // Map 0.15.x byte type to v113 enum. 0.15.x chat arrives as TYPE_RAW;
    // forward as RAW so the v113 server's chat parser sees the player's
    // message verbatim.
    switch(chatPacket.type){
      case org.CreadoresProgram.CraftsMine.network.protocol.TextPacket.TYPE_RAW:
        textPacket.setType(TextPacket.Type.RAW);
        break;
      case org.CreadoresProgram.CraftsMine.network.protocol.TextPacket.TYPE_CHAT:
        textPacket.setType(TextPacket.Type.CHAT);
        break;
      case org.CreadoresProgram.CraftsMine.network.protocol.TextPacket.TYPE_TRANSLATION:
        textPacket.setType(TextPacket.Type.TRANSLATION);
        break;
      case org.CreadoresProgram.CraftsMine.network.protocol.TextPacket.TYPE_POPUP:
        textPacket.setType(TextPacket.Type.POPUP);
        break;
      case org.CreadoresProgram.CraftsMine.network.protocol.TextPacket.TYPE_TIP:
        textPacket.setType(TextPacket.Type.TIP);
        break;
      case org.CreadoresProgram.CraftsMine.network.protocol.TextPacket.TYPE_SYSTEM:
        textPacket.setType(TextPacket.Type.SYSTEM);
        break;
      default:
        textPacket.setType(TextPacket.Type.RAW);
        break;
    }
    textPacket.setSender(chatPacket.source == null ? "" : chatPacket.source);
    textPacket.setMessage(chatPacket.message == null ? "" : chatPacket.message);
    if(chatPacket.parameters != null && chatPacket.parameters.length > 0){
      java.util.List<String> params = new java.util.ArrayList<>();
      java.util.Collections.addAll(params, chatPacket.parameters);
      textPacket.setParameters(params);
    } else {
      textPacket.setParameters(new java.util.ArrayList<>());
    }
    player.getBedrockClientSession().sendPacket(textPacket);
  }
}
