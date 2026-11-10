package org.CreadoresProgram.CraftsMine.network.translator.managerScreen;
import org.CreadoresProgram.CraftsMine.player.Player;
import org.CreadoresProgram.CraftsMine.network.protocol.TextPacket;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import lombok.Setter;
import lombok.Getter;
public class ScreenManager{
  @Setter
  private String scoreBoard = "";
  @Setter
  public String[] title = new String[]{ "", "" };
  @Setter
  private String tip = "";

  @Setter
  private String popup = "";

  @Setter
  private String bossBars = "";

  private Player player;
  public ScreenManager(Player player){
    this.player = player;
  }
  public void sendTip(String message){
    this.tip = message;
    this.updateScreenTip();
    Timer timer = new Timer(3000, new ActionListener(){
      public void actionPerformed(ActionEvent e){
        ScreenManager.this.setTip("");
      }
    });
    timer.setRepeats(false);
    timer.start();
  }
  public void sendScoreBoard(String score){
    this.scoreBoard = score;
    this.updateScreenTip();
  }
  public void sendTitle(String title){
    this.title[0] = title;
    this.updateScreenTip();
    Timer timer = new Timer(4000, new ActionListener(){
      public void actionPerformed(ActionEvent e){
        ScreenManager.this.title[0] = "";
      }
    });
    timer.setRepeats(false);
    timer.start();
  }
  public void sendSubtitle(String title){
    this.title[1] = title;
    this.updateScreenTip();
    Timer timer = new Timer(4000, new ActionListener(){
      public void actionPerformed(ActionEvent e){
        ScreenManager.this.title[1] = "";
      }
    });
    timer.setRepeats(false);
    timer.start();
  }

  public void sendPopup(String message){
    this.popup = message;
    this.updateScreenPopup();
    Timer timer = new Timer(3000, new ActionListener(){
      public void actionPerformed(ActionEvent e){
        ScreenManager.this.setPopup("");
      }
    });
    timer.setRepeats(false);
    timer.start();
  }
  public void sendBossBar(String message){
    this.bossBars = message;
    this.updateScreenPopup();
  }
  
  private void updateScreenTip(){
    TextPacket pk = new TextPacket();
    pk.type = TextPacket.TYPE_TIP;
    String msg = "";
    if(this.scoreBoard.replaceAll(" ", "").replaceAll("\n", "") != ""){
      msg += this.scoreBoard+"\n§f§r";
    }
    if(this.title[0].replaceAll(" ", "").replaceAll("\n", "") != ""){
      if(this.scoreBoard.replaceAll(" ", "").replaceAll("\n", "") == ""){
        msg += this.title[0] + "\n§f§r";
      }else{
        msg += "                                       "+this.title[0]+"\n§f§r";
      }
    }
    if(this.title[1].replaceAll(" ", "").replaceAll("\n", "") != ""){
      if(this.scoreBoard.replaceAll(" ", "").replaceAll("\n", "") == ""){
        msg += this.title[1] + "\n§f§r";
      }else{
        msg += "                                       "+this.title[1] + "\n§f§r";
      }
    }
    if(this.scoreBoard.replaceAll(" ", "").replaceAll("\n", "") == ""){
      msg += this.tip;
    }else{
      msg += "                                       "+this.tip;
    }
    pk.message = msg;
    pk.source = msg;
    this.player.sendDataCraftsman(pk);
  }
  private void updateScreenPopup(){
    TextPacket pk = new TextPacket();
    pk.type = TextPacket.TYPE_POPUP;
    String msg = "";
    if(this.bossBars.replaceAll(" ", "").replaceAll("\n", "") != ""){
      msg += this.bossBars+"§f§r";
    }
    msg += this.popup;
    pk.message = msg;
    pk.source = msg;
    this.player.sendDataCraftsman(pk);
  }
}
