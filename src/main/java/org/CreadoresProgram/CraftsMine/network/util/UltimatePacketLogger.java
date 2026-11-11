package org.CreadoresProgram.CraftsMine.network.util;

import org.CreadoresProgram.CraftsMine.network.protocol.DisconnectPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.server.Server;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Ultimate Packet Debug Logger.
 *
 * <h2>Goal</h2>
 * When the MCPE 0.15.x client crashes (silent disconnect, decode error,
 * freeze, etc.) there is usually no clue on the proxy side: by the time
 * the socket closes the offending packet has already been flushed and the
 * RakNet session torn down. This logger keeps an in-memory ring buffer of
 * the last {@value #BUFFER_PER_CLASS} packets <strong>per class</strong>
 * that the proxy sent to the 0.15.x client, so that after a crash the
 * snapshot can be dumped to disk and the last entry of each class becomes
 * a prime suspect.
 *
 * <h2>Why per-class and not per-packet</h2>
 * A 0.15.x session typically fires dozens of MoveEntity packets per
 * second. Keeping only the last 5 globally would be useless: by the time
 * the client crashed, all 5 entries would be MoveEntity. Instead, the
 * buffer is keyed by packet class name, so you get the last 5 AddEntity,
 * the last 5 MobEquipment, the last 5 LevelEvent, ... etc. The classes
 * that fire often (MoveEntity, MovePlayer) naturally churn their own ring
 * and you can focus on the rarer packet types whose last instance
 * immediately preceded the crash.
 *
 * <h2>Threading</h2>
 * {@link #record(Object, MCDPacket)} is called from the per-player
 * Bedrock translate queue thread (single-thread FIFO per player). Each
 * player has its own per-class ring buffer stored in a
 * {@link ConcurrentHashMap} keyed by the player's RakNet identifier.
 * {@link java.util.concurrent.ConcurrentLinkedDeque} is used inside each
 * ring so that concurrent reads during {@link #dumpSnapshot} are safe.
 *
 * <h2>Exclusions</h2>
 * {@link DisconnectPacket} is excluded because it is the consequence of a
 * crash, never the cause - the proxy sends it on disconnect, so without
 * filtering it would always be the last entry and hide the real culprit.
 *
 * <h2>Output</h2>
 * {@link #dumpSnapshot(Object, String, String)} writes a single text file
 * at {@code ultimate-packet-debug.log} in the proxy's working directory
 * (overwritten on each call). The dump is also logged through
 * {@link Server#getLogger()} so the operator sees it in stderr without
 * having to open the file.
 */
public final class UltimatePacketLogger {
  /** Maximum entries kept per packet class. */
  static final int BUFFER_PER_CLASS = 5;

  /** Default dump file path (relative to proxy working dir). */
  static final String DEFAULT_DUMP_PATH = "ultimate-packet-debug.log";

  private static final SimpleDateFormat TS = new SimpleDateFormat("HH:mm:ss.SSS");

  /**
   * Per-player ring buffers. Outer map: playerId (the RakNet identifier
   * string from {@code Player.getIdentifierCraftsMan()}) → inner map
   * (packet class simple name → ring of last N descriptions).
   */
  private static final Map<Object, Map<String, Deque<String>>> PLAYER_BUFFERS = new ConcurrentHashMap<>();

  /** Configurable dump path. Defaults to {@link #DEFAULT_DUMP_PATH}. */
  private static volatile String dumpPath = DEFAULT_DUMP_PATH;

  private UltimatePacketLogger() {}

  public static void setDumpPath(String path) {
    if (path != null && !path.isEmpty()) {
      dumpPath = path;
    }
  }

  /**
   * Records a packet that is about to be sent to the 0.15.x client.
   * DisconnectPacket is filtered out (it is the symptom, not the cause).
   *
   * @param playerId the player's RakNet identifier (any hashable object)
   * @param pk the packet about to be sent
   */
  public static void record(Object playerId, MCDPacket pk) {
    if (playerId == null || pk == null) return;
    // DisconnectPacket is the consequence of a crash, never the cause.
    if (pk instanceof DisconnectPacket) return;
    String className = pk.getClass().getSimpleName();
    String entry = TS.format(new Date()) + "  " + className + "  " + describe(pk);
    Map<String, Deque<String>> perClass = PLAYER_BUFFERS.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>());
    Deque<String> dq = perClass.computeIfAbsent(className, k -> new ConcurrentLinkedDeque<>());
    dq.addLast(entry);
    while (dq.size() > BUFFER_PER_CLASS) {
      dq.pollFirst();
    }
  }

  /**
   * Dumps the current ring-buffer snapshot for the given player to
   * {@code ultimate-packet-debug.log} (overwrites). Safe to call from the
   * disconnect handler on the Netty thread.
   *
   * @param playerId the player whose buffers should be dumped
   * @param playerName display name for the dump header (or null)
   * @param reason the disconnect reason passed to {@code Player.disconnect}
   */
  public static synchronized void dumpSnapshot(Object playerId, String playerName, String reason) {
    Map<String, Deque<String>> perClass = (playerId == null) ? null : PLAYER_BUFFERS.get(playerId);
    Server logOwner = Server.getInstance();
    StringBuilder console = new StringBuilder();
    console.append("=== Ultimate Packet Debug Snapshot ===").append('\n');
    console.append("Player: ").append(playerName == null ? "(unknown)" : playerName).append('\n');
    console.append("Disconnect reason: ").append(reason == null ? "(none)" : reason).append('\n');
    console.append("Generated: ").append(new Date()).append('\n');
    console.append("Per-class ring buffer (last ").append(BUFFER_PER_CLASS)
           .append(" packets sent to 0.15.x client; Disconnect excluded)").append('\n');
    console.append("NOTE: the LAST entry of each class is the most recently sent.").append('\n');
    console.append('\n');
    if (perClass == null || perClass.isEmpty()) {
      console.append("(no packets were recorded for this player)").append('\n');
    } else {
      List<String> sortedClasses = new ArrayList<>(perClass.keySet());
      java.util.Collections.sort(sortedClasses);
      for (String cls : sortedClasses) {
        Deque<String> dq = perClass.get(cls);
        if (dq == null || dq.isEmpty()) continue;
        console.append("--- ").append(cls).append(" (").append(dq.size()).append(" entries) ---").append('\n');
        int idx = 1;
        for (String s : dq) {
          console.append("  [").append(idx++).append("] ").append(s).append('\n');
        }
        console.append('\n');
      }
    }
    String text = console.toString();
    // Print to console first - this always reaches the operator even if
    // the file write fails.
    if (logOwner != null) {
      try {
        logOwner.getLogger().info(text);
      } catch (Throwable ignored) {
        // Logging must never break the disconnect path.
      }
    }
    // Then write the file (overwrite).
    try (PrintWriter pw = new PrintWriter(new FileWriter(dumpPath, false))) {
      pw.print(text);
    } catch (Throwable ignored) {
      // Disk full / permission denied - the console dump already showed
      // the data, so this is best-effort.
    }
    // Clear the per-player buffers after dump so a re-login doesn't mix
    // stale entries with fresh ones.
    if (perClass != null) {
      perClass.clear();
    }
    PLAYER_BUFFERS.remove(playerId);
  }

  /**
   * Builds a short human-readable description of a packet's public
   * non-static fields. Reflection is used so the logger does not need to
   * know about every concrete packet class.
   */
  private static String describe(MCDPacket pk) {
    StringBuilder sb = new StringBuilder();
    try {
      // Walk the full inheritance chain (MCDPacket subclasses commonly
      // declare fields in the leaf class but the parent may add some too).
      Class<?> c = pk.getClass();
      while (c != null && c != Object.class) {
        for (Field f : c.getDeclaredFields()) {
          if (Modifier.isStatic(f.getModifiers())) continue;
          if (Modifier.isTransient(f.getModifiers())) continue;
          f.setAccessible(true);
          Object v = f.get(pk);
          if (sb.length() > 0) sb.append(", ");
          sb.append(f.getName()).append('=').append(truncate(String.valueOf(v)));
        }
        c = c.getSuperclass();
      }
    } catch (Throwable t) {
      return "(describe-failed: " + t.getClass().getSimpleName() + ": " + t.getMessage() + ")";
    }
    return sb.length() == 0 ? "(no fields)" : sb.toString();
  }

  private static String truncate(String s) {
    if (s == null) return "null";
    return s.length() <= 80 ? s : s.substring(0, 77) + "...";
  }
}
