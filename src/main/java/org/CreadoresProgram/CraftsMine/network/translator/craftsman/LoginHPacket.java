package org.CreadoresProgram.CraftsMine.network.translator.craftsman;
import org.CreadoresProgram.CraftsMine.player.Player;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.CraftsmanPacketTranslator;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.server.Server;
import org.CreadoresProgram.CraftsMine.network.protocol.DisconnectPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.LoginStatusPacket;

/**
 * Receives the 0.15.x LoginPacket from the upstream client, validates the
 * protocol id (80..90 = 0.15.x family), calls Player.onLogin() to bind the
 * client's identity + skin to the Player and open the downstream BedrockClient
 * session to the MCPE 1.1 server, and finally sends the v113 LoginPacket
 * (built by Player.getLoginPacket()) to the downstream.
 *
 * <p>Adapted from the original Barrel-Craftsmine implementation. The
 * CloudburstMC line {@code bedrockClientSession.getPeer().getCodecHelper()
 * .setBlockDefinitions(...)} was removed because NukkitX v113 has no such
 * API - block definitions are not registered on the session codec helper
 * in v113, the runtime block palette is consulted directly via
 * BlockMapper when needed.</p>
 */
public class LoginHPacket implements CraftsmanPacketTranslator{
  @Override
  public void translate(MCDPacket pk, Player player){
    org.CreadoresProgram.CraftsMine.network.protocol.LoginPacket packet =
        (org.CreadoresProgram.CraftsMine.network.protocol.LoginPacket) pk;
    // Reject clients outside the 0.15.x protocol family (80..90).
    // 0.15.x wire protocol ids are 81..85 but some forks use 80 or 90.
    if(packet.protocol > 90){
      LoginStatusPacket sta = new LoginStatusPacket();
      sta.status = LoginStatusPacket.LOGIN_SUCCESS;
      player.sendDataCraftsman(sta);
      DisconnectPacket disc = new DisconnectPacket();
      disc.message = "§cPlease enter\nIP: "+Server.getInstance().getConfig().getBedrockAddress()+" Port: "+Server.getInstance().getConfig().getBedrockPort();
      player.sendDataCraftsman(disc);
      return;
    }
    if(packet.protocol < 80){
      LoginStatusPacket discsub = new LoginStatusPacket();
      discsub.status = LoginStatusPacket.LOGIN_FAILED_CLIENT;
      player.sendDataCraftsman(discsub);
      return;
    }
    // Enforce max players when the proxy is NOT in synchronous (query-MOTD) mode.
    if(!(Server.getInstance().getConfig().isSynchServer())){
      if(Server.getInstance().getBedrockPlayers().size() > Server.getInstance().getConfig().getMaxplayers()){
        LoginStatusPacket stas = new LoginStatusPacket();
        stas.status = LoginStatusPacket.LOGIN_SUCCESS;
        player.sendDataCraftsman(stas);
        DisconnectPacket dicm = new DisconnectPacket();
        dicm.message = "disconnectionScreen.serverFull";
        player.sendDataCraftsman(dicm);
        return;
      }
    }
    try{
      // Bind the client identity to the Player and open the downstream
      // BedrockClient session to the MCPE 1.1 server. This blocks up to
      // 10 seconds on the RakNet handshake.
      // Save the client's real protocol version: several 0.15.x packets
      // changed layout inside the family (AddEntityPacket gained the
      // rotation scale + modifiers int in 0.15.4 / protocol 82), so the
      // encoders must match the exact client version. Verified against
      // Nukkit-Retro (v0_15_4 branch) and PocketMine master-legacy
      // (CURRENT_PROTOCOL = 81, layout without modifiers).
      int clientProtocol = packet.protocol;
      if (clientProtocol < 80 || clientProtocol > 90) {
        clientProtocol = 84; // fallback: the protocol the MOTD advertises
      }
      player.setProtocol(clientProtocol);
      player.onLogin(packet);
      // Player.getLoginPacket() builds a v113 LoginPacket with chainData +
      // clientData as AsciiString (the v113 wire format). Ship it.
      player.getBedrockClientSession().sendPacketImmediately(player.getLoginPacket());
    }catch(Exception e){
      org.CreadoresProgram.CraftsMine.network.protocol.LoginStatusPacket spk =
          new org.CreadoresProgram.CraftsMine.network.protocol.LoginStatusPacket();
      spk.status = org.CreadoresProgram.CraftsMine.network.protocol.LoginStatusPacket.LOGIN_SUCCESS;
      player.sendDataCraftsman(spk);
      player.disconnect("Failed to connect: "+ e+" or Server Offline");
      return;
    }
    String loginName = player.getCraftsmanUsername();
    if(loginName == null || loginName.isEmpty()){
      loginName = "Unknown";
    }
    Server.getInstance().getLogger().info(loginName+" Logged in");
  }
}
