package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.StartGamePacket;
import com.nukkitx.protocol.bedrock.data.GameType;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;

/**
 * Translates a v113 StartGamePacket from the MCPE 1.1 server into the
 * simpler 0.15.x StartGamePacket format and forwards it to the upstream
 * client so the player spawns into the world.
 *
 * <p>v113 StartGamePacket fields mapped to 0.15.x:</p>
 * <ul>
 *   <li>{@code seed} → {@code seed}</li>
 *   <li>{@code dimension.ordinal()} → {@code dimension} (byte)</li>
 *   <li>{@code generator.ordinal()} → {@code generator} (int)</li>
 *   <li>{@code worldGameType.ordinal()} → {@code gamemode} (int)</li>
 *   <li>{@code runtimeEntityId} → {@code eid} (long)</li>
 *   <li>{@code spawnPosition} → {@code spawnX/Y/Z} (int)</li>
 *   <li>{@code playerPosition} → {@code x/y/z} (float; 0.15.x y is feet, v113 y is eye +1.62)</li>
 *   <li>{@code achievementsDisabled} → {@code b1}</li>
 *   <li>{@code eduMode} → {@code b2}</li>
 *   <li>{@code commandsEnabled} → {@code b3}</li>
 *   <li>{@code levelId} (or worldName) → {@code unknownstr}</li>
 * </ul>
 *
 * <p>Fields present in v113 but with no 0.15.x equivalent (dayCycleStopTime,
 * rainLevel, lightningLevel, game rules, currentTick) are dropped — the
 * 0.15.x client will use defaults.</p>
 */
public class StartGame implements BedrockPacketTranslator{
  @Override
  public boolean immediate(){ return true; }
  @Override
  public void translate(BedrockPacket pk, Player player){
    StartGamePacket packet = (StartGamePacket) pk;
    // Save runtime entity id on the Player so subsequent packets can use it
    // when constructing MovePlayerC etc. The 0.15.x client eid == v113
    // runtime entity id (both are server-assigned longs).
    player.setRuntimeEntityId(packet.getRuntimeEntityId());
    player.setGamemode(packet.getWorldGameType() == null ? 0 : packet.getWorldGameType().ordinal());

    org.CreadoresProgram.CraftsMine.network.protocol.StartGamePacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.StartGamePacket();
    npk.seed = (int) Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, packet.getSeed()));
    npk.dimension = packet.getDimension() == null ? 0 : (byte) packet.getDimension().ordinal();
    npk.generator = packet.getGenerator() == null ? 1 : packet.getGenerator().ordinal();
    GameType gt = packet.getWorldGameType();
    npk.gamemode = gt == null ? 0 : gt.ordinal();
    npk.eid = packet.getRuntimeEntityId();

    Vector3i spawn = packet.getSpawnPosition();
    if (spawn != null) {
      npk.spawnX = spawn.getX();
      npk.spawnY = spawn.getY();
      npk.spawnZ = spawn.getZ();
    }

    Vector3f pos = packet.getPlayerPosition();
    if (pos != null) {
      // 0.15.x StartGamePacket.encode() writes player x/y/z as floats.
      // The 0.15.x client expects y to be the player's feet position.
      // v113 sends eye position; subtract 1.62 to get feet (matches the
      // 0.15.x StartGamePacket.decode() convention in Barrel-Craftsmine).
      npk.x = pos.getX();
      npk.y = pos.getY() - 1.62f;
      npk.z = pos.getZ();
    }

    npk.b1 = packet.isAchievementsDisabled();
    npk.b2 = packet.isEduMode();
    npk.b3 = packet.isCommandsEnabled();
    npk.unknownstr = packet.getLevelId() != null ? packet.getLevelId() : "";

    player.sendDataCraftsman(npk);
  }
}
