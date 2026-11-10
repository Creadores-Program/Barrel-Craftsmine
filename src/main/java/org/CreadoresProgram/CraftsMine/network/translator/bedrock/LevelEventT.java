package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.LevelEventPacket;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.LevelEventType;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;

/**
 * v113 LevelEventPacket (LevelEventType type, Vector3f position, int data)
 * → 0.15.x LevelEventPacket (int eventID, float x,y,z, int datae).
 *
 * <p><b>CRITICAL FIX</b>: The previous implementation extracted the
 * 0.15.x eventID via {@code ((Enum<?>) type).ordinal()} — the enum
 * ordinal (0, 1, 2, 3, ...). That is <b>completely wrong</b> because
 * the Bedrock LevelEvent id space is segmented:</p>
 * <ul>
 *   <li>0 = UNDEFINED</li>
 *   <li>1000..1052 = sounds (SOUND_CLICK=1000, SOUND_FIZZ=1004, etc.)</li>
 *   <li>2000..2014 = particles (PARTICLE_SHOOT=2000, PARTICLE_CRACK_BLOCK=2014)</li>
 *   <li>3000..3005 = world events (START_RAINING=3001, STOP_THUNDERSTORM=3004)</li>
 *   <li>3500..3508 = block/cauldron events</li>
 *   <li>3600..3601 = block break progress</li>
 * </ul>
 *
 * <p>Using ordinal() yielded IDs in the range 0..~50, which the 0.15.x
 * client interpreted as either UNDEFINED or as invalid event ids in the
 * "0..999" reserved range. The client then either silently ignored the
 * event or — for some events with side effects on world state — crashed
 * during the resulting state inconsistency.</p>
 *
 * <p>The correct id is obtained from the {@link BedrockPacketHelper}
 * associated with the v113 codec, which is the SAME helper that
 * {@link com.nukkitx.protocol.bedrock.v113.serializer.LevelEventSerializer_v113}
 * uses to serialize the packet on the wire. We reach it via
 * {@code player.getBedrockClientSession().getPacketCodec().getHelper()}.
 * Because the v113 id table matches the 0.15.x id table exactly
 * (both inherit the same Bedrock 1.0+ event id conventions), the id
 * can be forwarded verbatim to the 0.15.x client.</p>
 */
public class LevelEventT implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    LevelEventPacket packet = (LevelEventPacket) pk;
    LevelEventType type = packet.getType();
    if (type == null) return;

    // Resolve the int eventID via the v113 codec's helper. This is the
    // same lookup the v113 serializer uses on the wire, so the id we
    // forward to the 0.15.x client matches what the v113 server actually
    // sent (and matches the 0.15.x event id table for shared events).
    int eventId;
    try {
      BedrockPacketHelper helper = player.getBedrockClientSession()
          .getPacketCodec().getHelper();
      eventId = helper.getLevelEventId(type);
    } catch (Throwable t) {
      // If for any reason the helper lookup fails (closed session, null
      // codec, etc.) skip the packet rather than forwarding an
      // UNDEFINED/ordinal-based id that could crash the client.
      return;
    }
    // UNDEFINED maps to id 0 in the v113 helper — that is the sentinel
    // for "unknown event". Forwarding id=0 to the 0.15.x client is
    // harmless (it does nothing) but pointless; skip it.
    if (eventId == 0 && type != com.nukkitx.protocol.bedrock.data.LevelEventType.UNDEFINED) {
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
