package org.CreadoresProgram.CraftsMine.network.translator;

/**
 * STUB - ReloadAuthP previously sent a PlayerAuthInputPacket to the
 * downstream Bedrock server on every player-input tick. PlayerAuthInputPacket
 * does NOT exist in NukkitX v113 (it was introduced in protocol 422 /
 * Bedrock 1.19.30). In v113, the equivalent is MovePlayerPacket which
 * carries position, rotation and motion. The periodic-sender logic will
 * be re-implemented against MovePlayerPacket in a follow-up session.
 *
 * <p>For now this class is a no-op so the project compiles. Player input
 * still reaches the server through the MovePlayerC craftsman translator
 * which converts the upstream 0.15.x MovePlayerPacket to a v113
 * MovePlayerPacket.</p>
 */
public class ReloadAuthP{
  public ReloadAuthP(org.CreadoresProgram.CraftsMine.player.Player player){
    // No-op: PlayerAuthInputPacket was removed when migrating to v113.
    // Periodic input is now sent via MovePlayerPacket directly from the
    // MovePlayerC translator on every received 0.15.x MovePlayerPacket.
  }
}
