package org.CreadoresProgram.CraftsMine.network.protocol;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.ProtocolInfo;
public class DisconnectPacket extends MCDPacket{
  public String message;
  
  @Override
  public byte pid(){
    return ProtocolInfo.DISCONNECT_PACKET;
  }
  @Override
  public void encode(){
      this.reset();
      this.putString(this.message);
  }
  @Override
  public void decode(){
      this.message = this.getString();
  }
}
