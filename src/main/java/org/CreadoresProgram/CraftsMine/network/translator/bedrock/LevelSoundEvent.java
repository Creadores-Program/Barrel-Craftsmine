package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.LevelSoundEventPacket;
import com.nukkitx.math.vector.Vector3f;

/**
 * v113 LevelSoundEventPacket (SoundEvent, Vector3f, int volume/pitch,
 * boolean babySound/relativeVolumeDisabled) → 0.15.x LevelEventPacket
 * (eventID, x/y/z, datae) as a workaround.
 *
 * <p>0.15.x has no LevelSoundEventPacket; the protocol 84 era used
 * LevelEventPacket with SOUND_* event ids (1000-1052 region). Since the
 * v113 SoundEvent enum ordinal doesn't align cleanly with those ids,
 * we drop the packet for now. Sound is non-critical for gameplay.</p>
 */
public class LevelSoundEvent implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    // Sound packets don't have a 0.15.x equivalent; silently drop.
    // The 0.15.x client will simply not hear the sound.
    LevelSoundEventPacket packet = (LevelSoundEventPacket) pk;
    if (packet == null) return;
  }
}
