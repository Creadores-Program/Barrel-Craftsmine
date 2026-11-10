package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.SetEntityLinkPacket;
import com.nukkitx.protocol.bedrock.data.entity.EntityLinkData;

/**
 * v113 SetEntityLinkPacket (EntityLinkData{vehicleUniqueId, passengerUniqueId,
 * Type, unknownByte}) → 0.15.x SetEntityLinkPacket (long rider, long riding,
 * byte type).
 *
 * <p>v113 EntityLinkData.Type enum: REMOVE=0, ADD=1. 0.15.x constants:
 * TYPE_REMOVE=0, TYPE_RIDE=1, TYPE_PASSENGER=2. The mapping is 1:1 for
 * REMOVE/ADD → RIDE; PASSENGER (which would be a separate v113 concept)
 * is not produced by v113 servers here.</p>
 */
public class SetEntityLink implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    SetEntityLinkPacket packet = (SetEntityLinkPacket) pk;
    EntityLinkData link = packet.getEntityLink();
    if (link == null) return;
    org.CreadoresProgram.CraftsMine.network.protocol.SetEntityLinkPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.SetEntityLinkPacket();
    // 0.15.x "rider" = the entity being carried; "riding" = the vehicle.
    // v113 vehicleUniqueId is the vehicle; passengerUniqueId is the rider.
    npk.rider = link.getPassengerUniqueId();
    npk.riding = link.getVehicleUniqueId();
    npk.type = link.getType() == EntityLinkData.Type.ADD
        ? org.CreadoresProgram.CraftsMine.network.protocol.SetEntityLinkPacket.TYPE_RIDE
        : org.CreadoresProgram.CraftsMine.network.protocol.SetEntityLinkPacket.TYPE_REMOVE;
    player.sendDataCraftsman(npk);
  }
}
