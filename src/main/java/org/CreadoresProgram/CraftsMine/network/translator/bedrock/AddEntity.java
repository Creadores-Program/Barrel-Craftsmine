package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.AddEntityPacket;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector2f;
import org.CreadoresProgram.CraftsMine.utils.EntityMapper;
import java.util.HashMap;

/**
 * Translates a v113 AddEntityPacket (server spawns a mob/object) into the
 * 0.15.x AddEntityPacket and forwards it to the upstream client.
 *
 * <p>For entity types that do NOT exist in 0.15.x (Wither, Pillager,
 * Ravager, etc.) the EntityMapper substitutes the closest available
 * entity (e.g. Wither → WitherSkeleton id 48). If the mapper returns DROP
 * (-1) the packet is silently dropped - the 0.15.x client would crash on
 * an unknown network id.</p>
 *
 * <p>Entity metadata is forwarded as an empty map for now. The 0.15.x
 * client will spawn the entity with default visual state (no nametag,
 * no scale overrides, no pose tweaks). Wiring the EntityDataMap →
 * legacy metadata byte format is a follow-up task.</p>
 */
public class AddEntity implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    AddEntityPacket packet = (AddEntityPacket) pk;
    int legacyType = EntityMapper.mapToLegacy(String.valueOf(packet.getEntityType()));
    if (legacyType == EntityMapper.DROP) return;

    org.CreadoresProgram.CraftsMine.network.protocol.AddEntityPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.AddEntityPacket();
    npk.eid = packet.getRuntimeEntityId();
    npk.type = legacyType;
    Vector3f pos = packet.getPosition();
    if (pos != null) {
      npk.x = pos.getX();
      npk.y = pos.getY();
      npk.z = pos.getZ();
    }
    Vector3f mot = packet.getMotion();
    if (mot != null) {
      npk.speedX = mot.getX();
      npk.speedY = mot.getY();
      npk.speedZ = mot.getZ();
    }
    Vector2f rot = packet.getRotation();
    if (rot != null) {
      // 0.15.x serializes yaw/pitch with a *0.71f scale factor on encode.
      // We set the raw value; the encode() method applies the factor.
      npk.yaw = rot.getY();
      npk.pitch = rot.getX();
    }
    npk.modifiers = 0;
    npk.metadata = new HashMap<>();
    player.sendDataCraftsman(npk);
  }
}
