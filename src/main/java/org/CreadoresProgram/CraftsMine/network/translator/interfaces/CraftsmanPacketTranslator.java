package org.CreadoresProgram.CraftsMine.network.translator.interfaces;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.player.Player;

public interface CraftsmanPacketTranslator{
  void translate(MCDPacket pk, Player player);
}
