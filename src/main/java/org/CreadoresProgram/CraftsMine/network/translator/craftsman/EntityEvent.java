package org.CreadoresProgram.CraftsMine.network.translator.craftsman;

import org.CreadoresProgram.CraftsMine.network.translator.interfaces.CraftsmanPacketTranslator;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.player.Player;

/**
 * STUB - This translator referenced CloudburstMC modern APIs that do not
 * exist in the vendored NukkitX v113 protocol tree (e.g. EntityEventPacket fields,
 * EntityDataTypes enum, ItemDefinition, etc.). It has been temporarily
 * reduced to a no-op so the project compiles. To be rewritten against
 * the v113 packet API in a follow-up session. See MIGRATION_STATUS.md.
 */
public class EntityEvent implements CraftsmanPacketTranslator{
  @Override
  public void translate(MCDPacket pk, Player player){
    // No-op: pending v113 API rewrite.
  }
}
