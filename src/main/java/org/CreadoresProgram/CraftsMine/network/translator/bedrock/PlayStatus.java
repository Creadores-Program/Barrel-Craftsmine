package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.PlayStatusPacket;

/**
 * Translates a v113 PlayStatusPacket (sent by the MCPE 1.1 server after
 * receiving the client's LoginPacket) into a 0.15.x LoginStatusPacket and
 * forwards it to the upstream client.
 *
 * <p>v113 PlayStatusPacket.Status enum values:</p>
 * <ul>
 *   <li>LOGIN_SUCCESS → tell 0.15.x client login succeeded</li>
 *   <li>LOGIN_FAILED_CLIENT / LOGIN_FAILED_SERVER → tell 0.15.x client login failed</li>
 *   <li>PLAYER_SPAWN → tell 0.15.x client to spawn (mapped to LOGIN_SUCCESS
 *       because 0.15.x uses StartGamePacket to trigger spawn, not PlayStatus)</li>
 *   <li>other → default to LOGIN_SUCCESS to avoid stuck-login</li>
 * </ul>
 */
public class PlayStatus implements BedrockPacketTranslator{
  @Override
  public boolean immediate(){ return true; }
  @Override
  public void translate(BedrockPacket pk, Player player){
    PlayStatusPacket packet = (PlayStatusPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.LoginStatusPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.LoginStatusPacket();
    switch(packet.getStatus()){
      case LOGIN_SUCCESS:
      case PLAYER_SPAWN:
        // 0.15.x spawns the player via StartGamePacket, not PlayStatus.
        // PLAYER_SPAWN here is informational; we ack with LOGIN_SUCCESS so
        // the 0.15.x client stops showing the "logging in..." screen.
        npk.status = org.CreadoresProgram.CraftsMine.network.protocol.LoginStatusPacket.LOGIN_SUCCESS;
        break;
      case LOGIN_FAILED_CLIENT_OLD:
        npk.status = org.CreadoresProgram.CraftsMine.network.protocol.LoginStatusPacket.LOGIN_FAILED_CLIENT;
        break;
      case LOGIN_FAILED_SERVER_OLD:
        npk.status = org.CreadoresProgram.CraftsMine.network.protocol.LoginStatusPacket.LOGIN_FAILED_SERVER;
        break;
      default:
        // LOGIN_FAILED_INVALID_TENANT, LOGIN_FAILED_VANILLA_EDU, etc.
        // 0.15.x has no equivalent; default to LOGIN_FAILED_SERVER.
        npk.status = org.CreadoresProgram.CraftsMine.network.protocol.LoginStatusPacket.LOGIN_FAILED_SERVER;
        break;
    }
    player.sendDataCraftsman(npk);
  }
}
