package org.CreadoresProgram.CraftsMine.utils;
import org.CreadoresProgram.CraftsMine.meJustinLib.bedrockserverquery.data.BedrockQuery;
import org.CreadoresProgram.CraftsMine.server.Server;
import lombok.Getter;

/**
 * Periodic task that queries the configured Bedrock destination server and
 * propagates its MOTD to the RakNet upstream listener.
 *
 * Previously this class implemented {@code java.awt.event.ActionListener} so it
 * could be driven by a {@code javax.swing.Timer}. The GUI dependency has been
 * removed to keep the proxy headless, so the class now implements
 * {@code Runnable} instead and is scheduled by the shared
 * {@code ScheduledExecutorService} owned by {@link Server}.
 */
public class QueryTask implements Runnable{
  @Getter
  private static BedrockQuery infoServ = BedrockQuery.create(Server.getInstance().getConfig().getBedrockAddress(), Server.getInstance().getConfig().getBedrockPort());

  @Override
  public void run(){
    try {
      infoServ = BedrockQuery.create(Server.getInstance().getConfig().getBedrockAddress(), Server.getInstance().getConfig().getBedrockPort());
      Server.getInstance().getRakNetServ().setMOTD("", Server.getInstance().getConfig().getSubmotd());
    } catch (Exception ex) {
      // Queries are best-effort; never let them kill the scheduler thread.
      if (Server.getInstance() != null) {
        Server.getInstance().getLogger().warn("Bedrock query failed: " + ex.getMessage());
      }
    }
  }
}
