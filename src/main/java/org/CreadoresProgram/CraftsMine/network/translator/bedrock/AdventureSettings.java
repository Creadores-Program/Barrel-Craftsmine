package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.AdventureSettingsPacket;
import com.nukkitx.protocol.bedrock.data.AdventureSetting;
import java.util.Set;

/**
 * v113 AdventureSettingsPacket (Set&lt;AdventureSetting&gt; settings) →
 * 0.15.x AdventureSettingsPacket (int flags, int userPermission,
 * int globalPermission).
 *
 * <p>0.15.x flags bit layout (from Nukkit 0.15.x source):</p>
 * <ul>
 *   <li>bit 0 (1): WORLD_IMMUTABLE</li>
 *   <li>bit 1 (2): NO_PVP</li>
 *   <li>bit 2 (4): NO_PVM</li>
 *   <li>bit 3 (8): NO_MVP</li>
 *   <li>bit 4 (16): NO_EVP</li>
 *   <li>bit 5 (32): AUTO_JUMP</li>
 *   <li>bit 6 (64): ALLOW_FLIGHT</li>
 *   <li>bit 7 (128): NO_CLIP</li>
 *   <li>bit 8 (256): WORLD_BUILDER</li>
 *   <li>bit 9 (512): FLYING</li>
 *   <li>bit 10 (1024): MUTED</li>
 * </ul>
 *
 * <p>v113 AdventureSetting enum order matches roughly. We compute the
 * int flags by ORing the ordinal-bit for each setting in the set.</p>
 */
public class AdventureSettings implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    AdventureSettingsPacket packet = (AdventureSettingsPacket) pk;
    Set<AdventureSetting> settings = packet.getSettings();
    if (settings == null) return;
    int flags = 0;
    for (AdventureSetting s : settings) {
      // Each AdventureSetting enum constant maps to bit (1 << ordinal).
      // The ordinals of v113 AdventureSetting are: WORLD_IMMUTABLE=0,
      // NO_PVP=1, NO_PVM=2, NO_MVP=3, NO_EVP=4, AUTO_JUMP=5,
      // ALLOW_FLIGHT=6, NO_CLIP=7, WORLD_BUILDER=8, FLYING=9, MUTED=10.
      flags |= (1 << s.ordinal());
    }
    org.CreadoresProgram.CraftsMine.network.protocol.AdventureSettingsPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.AdventureSettingsPacket();
    npk.flags = flags;
    // userPermission and globalPermission default to 0 (normal player).
    // The v113 packet has no equivalent; we leave them at 0.
    npk.userPermission = 0;
    npk.globalPermission = 0;
    player.sendDataCraftsman(npk);
  }
}
