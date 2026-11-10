package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.LevelEventPacket;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.LevelEventType;

/**
 * v113 LevelEventPacket (LevelEventType type, Vector3f position, int data)
 * → 0.15.x LevelEventPacket (int eventID, float x,y,z, int datae).
 *
 * <p>v113 LevelEventType is an interface with subtypes (LevelEvent,
 * ParticleType, SoundEvent). Each enum constant has an int id that
 * roughly aligns with the 0.15.x eventID short. We extract the id via
 * the enum ordinal-based mapping or use the value directly when available.</p>
 */
public class LevelEventT implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    LevelEventPacket packet = (LevelEventPacket) pk;
    LevelEventType type = packet.getType();
    if (type == null) return;
    // LevelEventType is an interface; concrete subtypes are LevelEvent,
    // ParticleType, SoundEvent enums. We use the ordinal of the enum
    // constant as the 0.15.x eventID. This is approximate and may need
    // per-event tuning; the 0.15.x LevelEventPacket.Events constants
    // define a different numbering scheme but many overlap.
    int eventId;
    if (type instanceof Enum){
      eventId = ((Enum<?>) type).ordinal();
    } else {
      return;
    }
    org.CreadoresProgram.CraftsMine.network.protocol.LevelEventPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.LevelEventPacket();
    npk.eventID = eventId;
    Vector3f pos = packet.getPosition();
    if (pos != null) {
      npk.x = pos.getX();
      npk.y = pos.getY();
      npk.z = pos.getZ();
    }
    npk.datae = packet.getData();
    player.sendDataCraftsman(npk);
  }
}
