package org.CreadoresProgram.CraftsMine.server;
import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.Yaml;
import org.CreadoresProgram.CraftsMine.utils.Logger;
import org.CreadoresProgram.CraftsMine.utils.TextFormat;
import org.CreadoresProgram.CraftsMine.utils.NbtBlockDefinitionRegistry;
import java.io.*;
import org.apache.commons.io.output.TeeOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;
import java.net.URL;
import com.nukkitx.nbt.NBTInputStream;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtType;
import com.nukkitx.nbt.NbtUtils;
// ===== MCPE 1.1 (protocol v113) codec vendored from ProxyPassV113 =====
// All CloudburstMC modern codecs (1.7/1.8/1.9/1.10) have been removed.
// The downstream now talks MCPE 1.1 (protocol v113) exclusively.
import com.nukkitx.protocol.bedrock.v113.Bedrock_v113;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import org.CreadoresProgram.CraftsMine.player.Player;
import org.CreadoresProgram.CraftsMine.options.Config;
import org.CreadoresProgram.CraftsMine.utils.FileManager;
import org.CreadoresProgram.CraftsMine.network.RaknetInterfaz;
import org.CreadoresProgram.CraftsMine.utils.QueryTask;
public class Server{
  @Getter
  private static Server instance = null;
  @Getter
  private final Map<String, Player> bedrockPlayers = new ConcurrentHashMap<>();
  @Getter
  private BedrockPacketCodec bedrockPacketCodec = Bedrock_v113.V113_CODEC;
  /**
   * MCPE 1.1 (protocol v113) codec vendored from ProxyPassV113. Exposed as a
   * public static final constant so {@link org.CreadoresProgram.CraftsMine.player.Player}
   * and the bedrock translators can reference it directly.
   *
   * <p>Constants exposed:</p>
   * <ul>
   *   <li>{@code V113_CODEC.getProtocolVersion()} returns {@code 113}</li>
   *   <li>{@code V113_CODEC.getMinecraftVersion()} returns {@code "1.1.0"}</li>
   *   <li>{@code V113_CODEC.getRaknetProtocolVersion()} returns {@code 8}</li>
   * </ul>
   */
  @Getter
  public static final BedrockPacketCodec V113_CODEC = Bedrock_v113.V113_CODEC;
  /**
   * Returns the protocol version exposed to clients via MOTD/ping.
   * @return 113 (MCPE 1.1)
   */
  public int getDownstreamProtocolVersion() {
    return V113_CODEC.getProtocolVersion();
  }
  @Getter
  private final Path dataPath;
  @Getter
  private Config config;
  @Getter
  private String defaultSkinData;
  @Getter
  private String defaultSkinGeomety;
  @Getter
  private Logger logger;
  @Getter
  private RaknetInterfaz rakNetServ;

  @Getter
  private NbtBlockDefinitionRegistry blockDefinitions;

  private ThreadTik ticker;
  private CommandExecutor commandDetect;
  @Getter
  private ScheduledExecutorService scheduler;
  /**
   * Shared translation thread pool. Previously each {@link Player} created its
   * own {@code ThreadPoolExecutor} with {@code maxPoolSize = Integer.MAX_VALUE}
   * and a {@code SynchronousQueue}, which meant that under any sustained burst
   * of packets the proxy spawned an unbounded number of worker threads (one
   * pool per connected client). This shared pool caps concurrency to
   * {@code 2 * availableProcessors} and queues up to 1024 pending tasks per
   * core before applying back-pressure via {@code CallerRunsPolicy}. That
   * keeps steady-state memory and thread churn flat regardless of player
   * count.
   */
  @Getter
  private final ThreadPoolExecutor translationExecutor = buildTranslationPool();
  /**
   * Shared Netty event loop group for all downstream Bedrock clients. The
   * original code allocated one {@code NioEventLoopGroup} per player inside
   * {@link Player#login} and only called {@code channel.disconnect()}, never
   * {@code shutdownGracefully()} - so every connect/disconnect cycle leaked
   * a Netty thread pool (default {@code 2 * cores} threads each).
   *
   * <p>IMPORTANT: we use {@link com.nukkitx.network.util.EventLoops#newEventLoopGroup(int)}
   * instead of {@code new NioEventLoopGroup(...)}. The RakNet Bootstrap
   * auto-detects the platform channel type (Epoll on Linux, Kqueue on Mac,
   * NIO elsewhere) via {@code EventLoops.getChannelType()} and sets the
   * matching DatagramChannel class on the Bootstrap. If we force a
   * NioEventLoopGroup but the platform is Linux with Epoll available,
   * the Bootstrap will try to open an EpollDatagramChannel with a
   * NioEventLoopGroup — which fails with "Failed to bind RakNetClient".
   * EventLoops.newEventLoopGroup() creates the correct EventLoopGroup
   * subclass for the detected channel type, so the Bootstrap's channel
   * class and the EventLoopGroup always match.</p>
   */
  @Getter
  private final io.netty.channel.EventLoopGroup bedrockWorkerGroup =
      com.nukkitx.network.util.EventLoops.newEventLoopGroup(
          Math.max(2, Runtime.getRuntime().availableProcessors()));

  private static ThreadPoolExecutor buildTranslationPool(){
    int cores = Runtime.getRuntime().availableProcessors();
    ThreadPoolExecutor pool = new ThreadPoolExecutor(
        Math.max(2, cores),
        Math.max(4, cores * 2),
        60L, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(1024),
        r -> {
          Thread t = new Thread(r, "CraftsMine-translate");
          t.setDaemon(true);
          return t;
        },
        new ThreadPoolExecutor.CallerRunsPolicy()
    );
    pool.allowCoreThreadTimeOut(true);
    return pool;
  }

  @Getter
  private String softwareVersion = "0.3.1";
  @Getter
  private String softwareName = "CraftsMine";
  @Getter
  private String softwareVersionName = "Prototype";
  @Getter
  private String softwareAuthor = "Creadores Program©";
  
  public Server(String dataPath){
    instance = this;
    this.logger = new Logger(TextFormat.GOLD.getAnsiCode()+"Crafts"+TextFormat.MATERIAL_GOLD.getAnsiCode()+"Mine");
    this.getLogger().info(softwareName+" v"+softwareVersion+" "+softwareVersionName+" Starting...");
    this.getLogger().info("Loading Configs...");
    this.dataPath = Paths.get(dataPath);
    if(!initConfig()){
      this.getLogger().emergency("Config file not found! Terminating...");
      System.exit(1);
    }
    if(this.getConfig().isConsoleLog()){
      try{
        FileOutputStream fos = new FileOutputStream("console.log", true);
            TeeOutputStream tos = new TeeOutputStream(System.out, fos);
            PrintStream ps = new PrintStream(tos, true);
            System.setOut(ps);
            System.setErr(ps);
      }catch(IOException ex){
        this.getLogger().critical("Critical error: ", ex);
        System.exit(1);
      }
    }
    // Shared single-thread scheduler replaces the previous per-task javax.swing.Timer
    // instances. Swing Timer requires AWT and spins a hidden thread per task; this
    // approach uses one daemon thread for both periodic tasks and avoids System.gc()
    // explicit calls (the JVM GC is automatic and tuned by G1 settings).
    this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
      Thread t = new Thread(r, "CraftsMine-scheduler");
      t.setDaemon(true);
      return t;
    });
    if(this.config.isSynchServer()){
      int tikQuery = 15;
      if(this.config.getTimeSynchServer() < 1){
        this.getLogger().warn("The value of timeSynchServer is invalid!");
        this.getLogger().info("Using the default time...");
      }else{
        tikQuery = (int) (this.config.getTimeSynchServer() / 1000L);
        if(tikQuery < 1) tikQuery = 15;
      }
      this.scheduler.scheduleAtFixedRate(new QueryTask(), tikQuery, tikQuery, TimeUnit.SECONDS);
    }
    // Downstream codec is hard-wired to MCPE 1.1 (protocol v113).
    // The previous switch statement selected between CloudburstMC modern
    // codecs (1.7/1.8/1.9/1.10) - those dependencies have been removed
    // entirely and the versionBedrockServer config field no longer exists.
    this.bedrockPacketCodec = Bedrock_v113.V113_CODEC;
    this.getLogger().info("Loading Block Definitions...");
    loadBlockDefinitions();
    this.getLogger().info("Loading Default Skin...");
    loadDefaultSkin();
    this.getLogger().info("Starting Server...");
    startServer();
  }
  private boolean initConfig() {
        File configFile = new File(dataPath.toFile(), "config.yml");
        if (!configFile.exists()) {
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                if (inputStream == null) {
                    return false;
                }
                Files.createDirectories(configFile.getParentFile().toPath());
                Files.copy(inputStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                return false;
            }
        }
        try {
            this.config = new Yaml().loadAs(Files.newBufferedReader(configFile.toPath()), Config.class);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
  private void loadDefaultSkin() {
        try {
            this.defaultSkinData = FileManager.getFileContents(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("skin/skin_data.txt")));
            this.defaultSkinGeomety = FileManager.getFileContents(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("skin/skin_geometry.json")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  private void loadBlockDefinitions(){
    try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream("block_palette.nbt")){
      assert inputStream != null;
      try(NBTInputStream nbtInputStream = NbtUtils.createGZIPReader(inputStream)){
        Object object = nbtInputStream.readTag();
        if(object instanceof NbtMap){
          NbtMap blocksTag = (NbtMap) object;
          blockDefinitions = new NbtBlockDefinitionRegistry(blocksTag.getList("blocks", NbtType.COMPOUND));
        }
      }
    } catch(IOException e){
      throw new RuntimeException("Failed to load block definitions", e);
    }
  }
  private void startServer(){
    rakNetServ = new RaknetInterfaz(this, config.getBindAddress(), config.getPort());
    rakNetServ.setMOTD(config.getMotd(), config.getSubmotd());
    this.ticker = new ThreadTik(this);
    this.ticker.start();
    this.commandDetect = new CommandExecutor(this);
    this.commandDetect.start();
    this.getLogger().info("Done!");
  }
  public void stopServer(){
    this.getLogger().info("Turning off Proxy...");
    for(Player player : this.bedrockPlayers.values()){
      player.disconnect(this.getConfig().getShutdownMessage());
    }
    this.getLogger().info("Turning off Threads...");
    this.ticker.isOn = false;
    this.commandDetect.isOn = false;
    this.scheduler.shutdownNow();
    this.translationExecutor.shutdownNow();
    try { this.bedrockWorkerGroup.shutdownGracefully().await(2, TimeUnit.SECONDS); } catch (InterruptedException ignored) {}
    this.getLogger().info("Turning off RakNet Server by Nukkit MagmaBlock Edition...");
    rakNetServ.shutdown();
    this.getLogger().info("CraftsMine Offline!");
    System.exit(0);
  }
  public class CommandExecutor extends Thread{
    private Server serv;
    public boolean isOn = true;
    public CommandExecutor(Server server){
      this.serv = server;
    }
    @Override
    public void run(){
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      while(this.isOn){
        String cmd = null;
        try{
        cmd = reader.readLine();
        }catch(Exception ex){
          
        }
        if(cmd == null || cmd == ""){
          return;
        }
        switch(cmd.toLowerCase()){
          case "stop":
          case "end":
            this.serv.stopServer();
            break;
          case "ver":
          case "version":
          case "about":
            this.serv.getLogger().info(TextFormat.AQUA.getAnsiCode()+"--SOFTWARE INFO--\n"+TextFormat.GREEN.getAnsiCode()+" "+this.serv.getSoftwareName()+" V"+this.serv.getSoftwareVersion()+" "+this.serv.getSoftwareVersionName()+" By "+this.serv.getSoftwareAuthor()+"\n"+TextFormat.GOLD.getAnsiCode()+"Crossplay Software MCPE 0.15.X/Craftsman-MCBE/Minecraft Bedrock(1.7|1.8|1.9|1.10)\n"+TextFormat.AQUA.getAnsiCode()+"-----------------");
            break;
          case "playerlist":
            this.serv.getLogger().info("Players: ("+this.serv.getBedrockPlayers().size()+"/"+this.serv.getConfig().getMaxplayers()+"):\n");
            for(Player playe : this.serv.getBedrockPlayers().values()){
              if(playe.getCraftsmanUsername() == null){
                continue;
              }
              this.serv.getLogger().info(playe.getCraftsmanUsername());
            }
            break;
          case "help":
          case "?":
            this.serv.getLogger().info(TextFormat.RED.getAnsiCode()+"'help': get help for commands in the console");
            this.serv.getLogger().info(TextFormat.YELLOW.getAnsiCode()+"'playerlist' get a list of online players");
            this.serv.getLogger().info(TextFormat.DARK_RED.getAnsiCode()+"'stop' stop the server");
            this.serv.getLogger().info(TextFormat.GOLD.getAnsiCode()+"'version' server information");
            break;
          default:
            this.serv.getLogger().info(TextFormat.RED.getAnsiCode()+"Use 'help' to help!");
            break;
        }
      }
      try{
        reader.close();
      }catch(Exception ex){
          
      }
    }
  }
  public class ThreadTik extends Thread{
    private Server serv;
    public boolean isOn = true;
      public ThreadTik(Server server){
        this.serv = server;
        setDaemon(true);
      }
      @Override
      public void run(){
        long time;
        while (this.isOn){
          time = System.currentTimeMillis();
          serv.getRakNetServ().onTick();
          time = System.currentTimeMillis() - time;
          if(time >= 50){
            continue;
          }else{
            try{
              Thread.sleep(50 - time);
            } catch(InterruptedException ex){
              return;
            }
          }
        }
      }
  }
}
