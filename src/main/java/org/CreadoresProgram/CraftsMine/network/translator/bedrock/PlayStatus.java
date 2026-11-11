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
 * <p>v113 PlayStatusPacket.Status enum ordinals (written as int by the
 * v113 serializer) are DIRECTLY compatible with 0.15.x LoginStatusPacket
 * int constants:</p>
 *
 * <pre>
 *   v113 ordinal  | v113 Status              | 0.15.x LoginStatusPacket
 *   ------------- | ------------------------ | ---------------------------
 *   0             | LOGIN_SUCCESS            | LOGIN_SUCCESS (0)
 *   1             | LOGIN_FAILED_CLIENT_OLD  | LOGIN_FAILED_CLIENT (1)
 *   2             | LOGIN_FAILED_SERVER_OLD  | LOGIN_FAILED_SERVER (2)
 *   3             | PLAYER_SPAWN             | PLAYER_SPAWN (3)
 *   4+            | (various failure modes)  | LOGIN_FAILED_SERVER (2)
 * </pre>
 *
 * <p><b>CRITICAL FIX</b>: The previous implementation mapped PLAYER_SPAWN
 * to LOGIN_SUCCESS, which broke the 0.15.x spawn sequence. The 0.15.x
 * client login/spawn flow is:</p>
 * <ol>
 *   <li>Client sends LoginPacket → server</li>
 *   <li>Server sends PlayStatus(LOGIN_SUCCESS=0) → client: "login OK,
 *       proceed to resource packs"</li>
 *   <li>Server sends StartGamePacket → client: world initialization</li>
 *   <li>Server sends PlayStatus(PLAYER_SPAWN=3) → client: "spawn into
 *       the world now"</li>
 * </ol>
 *
 * <p>If the proxy sends LOGIN_SUCCESS (0) instead of PLAYER_SPAWN (3) in
 * step 4, the 0.15.x client never receives the spawn signal. It stays
 * stuck in the "logging in..." state, and when subsequent packets arrive
 * (SetEntityMotion, SetEntityData, UpdateAttributes, etc.) the client
 * is not in the correct state to process them — leading to a crash
 * during terrain construction or entity spawn.</p>
 */
public class PlayStatus implements BedrockPacketTranslator{
  // CRITICAL: PlayStatus(LOGIN_SUCCESS) is the packet that transitions the
  // 0.15.x client out of the "Locating Server" screen. It MUST be sent
  // inline on the Netty inbound thread (immediate=true), NOT queued to
  // the per-player bedrockTranslateQueue. The f97e056 commit removed
  // immediate() thinking it would improve ordering, but the queue
  // introduced a 10-50ms delay (or worse if the queue's single thread
  // was busy) before the client received LOGIN_SUCCESS — long enough
  // for the 0.15.x client to remain stuck at "Locating Server" forever
  // in some setups. Restoring immediate()=true makes PlayStatus
  // bypass the queue and reach the client the instant the v113 server
  // sends it. The LoginStatusPacket.encode() already calls
  // setShouldSendImmediate(true) so the RakNet layer sends it with
  // PRIORITY_IMMEDIATE (separate UDP datagram, no queueing).
  @Override
  public boolean immediate(){ return true; }
  @Override
  public void translate(BedrockPacket pk, Player player){
    PlayStatusPacket packet = (PlayStatusPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.LoginStatusPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.LoginStatusPacket();
    switch(packet.getStatus()){
      case LOGIN_SUCCESS:
        npk.status = org.CreadoresProgram.CraftsMine.network.protocol.LoginStatusPacket.LOGIN_SUCCESS;
        break;
      case PLAYER_SPAWN:
        // CRITICAL: forward PLAYER_SPAWN as PLAYER_SPAWN (3), NOT as
        // LOGIN_SUCCESS (0). The 0.15.x client needs this exact status
        // to complete the spawn sequence after StartGamePacket. Mapping
        // it to LOGIN_SUCCESS causes the client to get stuck and crash
        // when subsequent packets (SetEntityMotion, etc.) arrive while
        // the client is still in the "logging in" state.
        npk.status = org.CreadoresProgram.CraftsMine.network.protocol.LoginStatusPacket.PLAYER_SPAWN;
        break;
      case LOGIN_FAILED_CLIENT_OLD:
        npk.status = org.CreadoresProgram.CraftsMine.network.protocol.LoginStatusPacket.LOGIN_FAILED_CLIENT;
        break;
      case LOGIN_FAILED_SERVER_OLD:
        npk.status = org.CreadoresProgram.CraftsMine.network.protocol.LoginStatusPacket.LOGIN_FAILED_SERVER;
        break;
      default:
        // LOGIN_FAILED_INVALID_TENANT, LOGIN_FAILED_EDITION_MISMATCH_*, etc.
        // 0.15.x has no equivalent; default to LOGIN_FAILED_SERVER.
        npk.status = org.CreadoresProgram.CraftsMine.network.protocol.LoginStatusPacket.LOGIN_FAILED_SERVER;
        break;
    }
    player.sendDataCraftsman(npk);
  }
}
