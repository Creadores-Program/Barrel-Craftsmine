package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.MobEffectPacket;
import org.CreadoresProgram.CraftsMine.utils.EffectMapper;

/**
 * Translates a v113 MobEffectPacket (server adds/removes/modifies a potion
 * effect on an entity) into the 0.15.x MobEffectPacket and forwards it to
 * the upstream client.
 *
 * <p>v113 effect ids 1..23 are unchanged from 0.15.x. Effects added after
 * 0.15.x (LEVITATION=24, SLOW_FALLING=27, CONDUIT_POWER=29, etc.) are
 * dropped via {@link EffectMapper#mapToLegacy(int)} which returns -1 for
 * effects that do not exist in 0.15.x. FATAL_POISON=25 is remapped to
 * POISON=19 (same visual debuff).</p>
 *
 * <p>v113 MobEffectPacket.Event enum maps 1:1 to 0.15.x eventId constants:
 * ADD=1, MODIFY=2, REMOVE=3.</p>
 */
public class MobEffect implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    MobEffectPacket packet = (MobEffectPacket) pk;
    int legacyEffectId = EffectMapper.mapToLegacy(packet.getEffectId());
    if (legacyEffectId == EffectMapper.DROP) {
      // Effect does not exist in 0.15.x; silently drop the packet.
      return;
    }
    org.CreadoresProgram.CraftsMine.network.protocol.MobEffectPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.MobEffectPacket();
    npk.eid = packet.getRuntimeEntityId();
    switch(packet.getEvent()){
      case ADD:    npk.eventId = org.CreadoresProgram.CraftsMine.network.protocol.MobEffectPacket.EVENT_ADD; break;
      case MODIFY: npk.eventId = org.CreadoresProgram.CraftsMine.network.protocol.MobEffectPacket.EVENT_MODIFY; break;
      case REMOVE: npk.eventId = org.CreadoresProgram.CraftsMine.network.protocol.MobEffectPacket.EVENT_REMOVE; break;
      default:     return;
    }
    npk.effectId = legacyEffectId;
    npk.amplifier = packet.getAmplifier();
    npk.particles = packet.isParticles();
    npk.duration = packet.getDuration();
    player.sendDataCraftsman(npk);
  }
}
