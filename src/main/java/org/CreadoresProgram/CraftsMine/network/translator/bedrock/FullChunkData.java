package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.FullChunkDataPacket;

/**
 * Translates a v113 FullChunkDataPacket (sent by the MCPE 1.1 server) into
 * the 0.15.x FullChunkPacket and forwards it to the upstream client.
 *
 * <p>v113 FullChunkDataPacket wire: chunkX:VarInt32, chunkZ:VarInt32,
 * data:ByteArray (where the byte array is the chunk blob).</p>
 * <p>0.15.x FullChunkPacket wire: chunkX:int32, chunkZ:int32, order:byte,
 * dataLen:int32, data:byte[].</p>
 *
 * <p>The standard legacy chunk blob format (used by 0.15.x AND v113 servers
 * like Rooti/Nukkit) is exactly 83200 bytes + blockEntities NBT:</p>
 * <ul>
 *   <li>orderedIds: 32768 bytes (16*16*128 block IDs)</li>
 *   <li>orderedData: 16384 bytes (16384 nibbles = 32768/2)</li>
 *   <li>orderedSkyLight: 16384 bytes</li>
 *   <li>orderedBlockLight: 16384 bytes</li>
 *   <li>orderedHeightMap: 256 bytes</li>
 *   <li>orderedBiomeColors: 1024 bytes (256 ints * 4 bytes, big-endian)</li>
 * </ul>
 * Total: 32768 + 16384*3 + 256 + 1024 = 83200
 *
 * <p>Some servers (e.g. Nukkit variants) send a 1-byte chunk-format-version
 * prefix at the start of the blob (value 0 = legacy). The 0.15.x client does
 * NOT expect this prefix and crashes if present. We detect and strip it by
 * checking the blob size: if size == 83201 (+blockEntities) we assume the
 * first byte is a version prefix and skip it.</p>
 */
public class FullChunkData implements BedrockPacketTranslator{
  /** Expected blob size for a legacy 0.15.x/v113 chunk WITHOUT blockEntities. */
  private static final int LEGACY_BLOB_SIZE = 83200;
  /** Nukkit/Rooti variant with 256-byte biomeColors (64 ints) instead of 1024. */
  private static final int NUKKIT_BLOB_SIZE = 82432;

  @Override
  public void translate(BedrockPacket pk, Player player){
    FullChunkDataPacket packet = (FullChunkDataPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.FullChunkPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.FullChunkPacket();
    npk.chunkX = packet.getChunkX();
    npk.chunkZ = packet.getChunkZ();
    npk.order = org.CreadoresProgram.CraftsMine.network.protocol.FullChunkPacket.ORDER_COLUMNS;
    byte[] data = packet.getData();
    if (data == null) data = new byte[0];

    int blobSize = data.length;
    // Always log the blob size on the first few chunks so we can diagnose
    // format mismatches. After 5 chunks, stop logging to avoid spam.
    if (player.getTikPia() < 5) {
      StringBuilder hex = new StringBuilder();
      for (int i = 0; i < Math.min(16, data.length); i++) {
        hex.append(String.format("%02x ", data[i] & 0xff));
      }
      org.CreadoresProgram.CraftsMine.server.Server.getInstance().getLogger().info(
          "FullChunkData (" + npk.chunkX + "," + npk.chunkZ + ") blobSize=" + blobSize
          + " firstBytes=" + hex.toString().trim());
    }

    // Detect subchunk format (Bedrock 1.2+): chunk version byte = 1.
    // The 0.15.x client cannot process this format directly and crashes.
    // Convert it to the legacy column-ordered format using ChunkConverter.
    if (blobSize > 0 && data[0] == 1) {
      // Pass the player's BlockMapper so ChunkConverter can resolve
      // RUNTIME palette entries (bit 0 of paletteHeader = 1) to legacy
      // block IDs via the StartGamePacket block palette.
      byte[] converted = org.CreadoresProgram.CraftsMine.utils.ChunkConverter.convertToLegacy(
          data, player.getBlockMapper());
      if (converted != null) {
        if (player.getTikPia() < 5) {
          org.CreadoresProgram.CraftsMine.server.Server.getInstance().getLogger().info(
              "FullChunkData: converted subchunk format (" + blobSize + " bytes) to legacy ("
              + converted.length + " bytes)");
        }
        data = converted;
        blobSize = data.length;
      } else {
        if (player.getTikPia() < 5) {
          org.CreadoresProgram.CraftsMine.server.Server.getInstance().getLogger().warn(
              "FullChunkData: subchunk format detected but conversion failed, sending raw blob");
        }
      }
    } else if (blobSize > 0 && data[0] == 0) {
      // Chunk version 0: some servers prefix a version-0 byte to a legacy blob.
      // Strip it if the remaining size matches a known legacy format.
      if (blobSize == org.CreadoresProgram.CraftsMine.utils.ChunkConverter.LEGACY_BLOB_SIZE + 1
          || blobSize == 82433) {
        byte[] stripped = new byte[data.length - 1];
        System.arraycopy(data, 1, stripped, 0, stripped.length);
        data = stripped;
        blobSize = data.length;
      }
    }

    npk.cdata = data;
    player.sendDataCraftsman(npk);
  }
}
