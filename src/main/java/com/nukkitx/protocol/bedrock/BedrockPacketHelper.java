package com.nukkitx.protocol.bedrock;

import com.nukkitx.math.vector.Vector2f;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.NBTInputStream;
import com.nukkitx.nbt.NBTOutputStream;
import com.nukkitx.nbt.NbtType;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.network.VarInts;
import com.nukkitx.network.util.Preconditions;
import com.nukkitx.protocol.bedrock.data.*;
import com.nukkitx.protocol.bedrock.data.command.CommandOriginData;
import com.nukkitx.protocol.bedrock.data.entity.*;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.protocol.bedrock.data.skin.ImageData;
import com.nukkitx.protocol.bedrock.data.skin.SerializedSkin;
import com.nukkitx.protocol.bedrock.util.TriConsumer;
import com.nukkitx.protocol.serializer.PacketHelper;
import com.nukkitx.protocol.util.Int2ObjectBiMap;
import com.nukkitx.protocol.util.QuadConsumer;
import com.nukkitx.protocol.util.TriFunction;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.util.AsciiString;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.UUID;
import java.util.function.*;

public abstract class BedrockPacketHelper implements PacketHelper {
    protected static final InternalLogger log = InternalLoggerFactory.getInstance(BedrockPacketHelper.class);

    protected final Int2ObjectBiMap<EntityData> entityData = new Int2ObjectBiMap<>();
    protected final Int2ObjectBiMap<EntityFlag> entityFlags = new Int2ObjectBiMap<>();
    protected final Int2ObjectBiMap<EntityData.Type> entityDataTypes = new Int2ObjectBiMap<>();
    protected final Int2ObjectBiMap<EntityEventType> entityEvents = new Int2ObjectBiMap<>();
    protected final Object2IntMap<Class<?>> gameRuleTypes = new Object2IntOpenHashMap<>(3, 0.5f);
    protected final Int2ObjectBiMap<SoundEvent> soundEvents = new Int2ObjectBiMap<>();
    protected final Int2ObjectBiMap<LevelEventType> levelEvents = new Int2ObjectBiMap<>();

    protected BedrockPacketHelper() {
        gameRuleTypes.defaultReturnValue(-1);

        this.registerEntityDataTypes();
        this.registerEntityData();
        this.registerEntityFlags();
        this.registerEntityEvents();
        this.registerGameRuleTypes();
        this.registerSoundEvents();
        this.registerLevelEvents();
        this.registerResourcePackTypes();
        this.registerContainerSlotTypes();
    }

    protected final void addGameRuleType(int index, Class<?> clazz) {
        this.gameRuleTypes.put(clazz, index);
    }

    protected final void addEntityData(int index, EntityData entityData) {
        this.entityData.put(index, entityData);
    }

    protected final void addEntityFlag(int index, EntityFlag flag) {
        this.entityFlags.put(index, flag);
    }

    protected final void addEntityDataType(int index, EntityData.Type type) {
        this.entityDataTypes.put(index, type);
    }

    protected final void addEntityEvent(int index, EntityEventType type) {
        this.entityEvents.put(index, type);
    }

    protected final void addSoundEvent(int index, SoundEvent soundEvent) {
        this.soundEvents.put(index, soundEvent);
    }

    protected final void addLevelEvent(int index, LevelEventType levelEventType) {
        this.levelEvents.put(index, levelEventType);
    }

    public final int getEntityEventId(EntityEventType type) {
        // @TODO For speed we may want a flag that disables this check for production use
        if (!this.entityEvents.containsValue(type)) {
            log.debug("Unknown EntityEventType: {}", type);
            return this.entityEvents.get(EntityEventType.NONE);
        }
        return this.entityEvents.get(type);
    }

    public final EntityEventType getEntityEvent(int id) {
        // @TODO For speed we may want a flag that disables this check for production use
        if (!entityEvents.containsKey(id)) {
            log.debug("Unknown EntityEvent: {}", id);
            return EntityEventType.NONE;
        }
        return this.entityEvents.get(id);
    }

    public final int getSoundEventId(SoundEvent event) {
        if (!soundEvents.containsValue(event)) {
            log.debug("Unknown SoundEvent {} received", event);
            return soundEvents.get(SoundEvent.UNDEFINED);
        }
        return this.soundEvents.get(event);
    }

    public final SoundEvent getSoundEvent(int id) {
        SoundEvent soundEvent = this.soundEvents.get(id);
        if (soundEvent == null) {
            log.debug("Unknown SoundEvent {} received", Integer.toUnsignedLong(id));
            return SoundEvent.UNDEFINED;
        }
        return soundEvent;
    }

    public final int getLevelEventId(LevelEventType event) {
        // @TODO For speed we may want a flag that disables this check for production use
        if (!this.levelEvents.containsValue(event)) {
            log.debug("Unknown LevelEventType: {}", event);
            return this.levelEvents.get(LevelEventType.UNDEFINED);
        }
        return this.levelEvents.get(event);
    }

    public final LevelEventType getLevelEvent(int id) {
        LevelEventType levelEvent = this.levelEvents.get(id);
        if (levelEvent == null) {
            log.debug("Unknown LevelEventType {} received", id);
            return LevelEventType.UNDEFINED;
        }
        return levelEvent;

    }

    protected abstract void registerEntityData();

    protected abstract void registerEntityFlags();

    protected abstract void registerEntityDataTypes();

    protected abstract void registerEntityEvents();

    protected abstract void registerGameRuleTypes();

    protected abstract void registerSoundEvents();

    protected abstract void registerResourcePackTypes();

    protected abstract void registerLevelEvents();

    protected abstract void registerContainerSlotTypes();

    public abstract EntityLinkData readEntityLink(ByteBuf buffer);

    public abstract void writeEntityLink(ByteBuf buffer, EntityLinkData link);

    public abstract ItemData readNetItem(ByteBuf buffer, BedrockSession session);

    public abstract void writeNetItem(ByteBuf buffer, ItemData item, BedrockSession session);

    public abstract ItemData readItem(ByteBuf buffer, BedrockSession session);

    public abstract void writeItem(ByteBuf buffer, ItemData item, BedrockSession session);

    public abstract ItemData readItemInstance(ByteBuf buffer, BedrockSession session);

    public abstract void writeItemInstance(ByteBuf buffer, ItemData item, BedrockSession session);

    public abstract CommandOriginData readCommandOrigin(ByteBuf buffer);

    public abstract void writeCommandOrigin(ByteBuf buffer, CommandOriginData commandOrigin);

    public abstract GameRuleData<?> readGameRule(ByteBuf buffer);

    public abstract void writeGameRule(ByteBuf buffer, GameRuleData<?> gameRule);

    public abstract void readEntityData(ByteBuf buffer, EntityDataMap entityData);

    public abstract void writeEntityData(ByteBuf buffer, EntityDataMap entityData);

    public abstract SerializedSkin readSkin(ByteBuf buffer);

    public abstract void writeSkin(ByteBuf buffer, SerializedSkin skin);

    public abstract ImageData readImage(ByteBuf buffer);

    public abstract void writeImage(ByteBuf buffer, ImageData image);

    public byte[] readByteArray(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        int length = VarInts.readUnsignedInt(buffer);
        Preconditions.checkArgument(buffer.isReadable(length),
                "Tried to read %s bytes but only has %s readable", length, buffer.readableBytes());
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        return bytes;
    }

    public void writeByteArray(ByteBuf buffer, byte[] bytes) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(bytes, "bytes");
        VarInts.writeUnsignedInt(buffer, bytes.length);
        buffer.writeBytes(bytes);
    }

    public ByteBuf readBuffer(ByteBuf buffer) {
        int length = VarInts.readUnsignedInt(buffer);
        return buffer.readRetainedSlice(length);
    }

    public void writeBuffer(ByteBuf buffer, ByteBuf toWrite) {
        Preconditions.checkNotNull(toWrite, "toWrite");
        VarInts.writeUnsignedInt(buffer, toWrite.readableBytes());
        buffer.writeBytes(toWrite, toWrite.readerIndex(), toWrite.writerIndex());
    }

    public String readString(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        return new String(readByteArray(buffer), StandardCharsets.UTF_8);
    }

    public void writeString(ByteBuf buffer, String string) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(string, "string");
        writeByteArray(buffer, string.getBytes(StandardCharsets.UTF_8));
    }

    public AsciiString readLEAsciiString(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        CharSequence string = buffer.readCharSequence(buffer.readIntLE(), StandardCharsets.US_ASCII);
        // Older Netty versions do not necessarily provide an AsciiString for this method
        if (string instanceof AsciiString) {
            return (AsciiString) string;
        } else {
            return new AsciiString(string);
        }
    }

    public void writeLEAsciiString(ByteBuf buffer, AsciiString string) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(string, "string");
        buffer.writeIntLE(string.length());
        buffer.writeCharSequence(string, StandardCharsets.US_ASCII);
    }

    public UUID readUuid(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        return new UUID(buffer.readLongLE(), buffer.readLongLE());
    }

    public void writeUuid(ByteBuf buffer, UUID uuid) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(uuid, "uuid");
        buffer.writeLongLE(uuid.getMostSignificantBits());
        buffer.writeLongLE(uuid.getLeastSignificantBits());
    }

    public Vector3f readVector3f(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        float x = buffer.readFloatLE();
        float y = buffer.readFloatLE();
        float z = buffer.readFloatLE();
        return Vector3f.from(x, y, z);
    }

    public void writeVector3f(ByteBuf buffer, Vector3f vector3f) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(vector3f, "vector3f");
        buffer.writeFloatLE(vector3f.getX());
        buffer.writeFloatLE(vector3f.getY());
        buffer.writeFloatLE(vector3f.getZ());
    }

    public Vector2f readVector2f(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        float x = buffer.readFloatLE();
        float y = buffer.readFloatLE();
        return Vector2f.from(x, y);
    }

    public void writeVector2f(ByteBuf buffer, Vector2f vector2f) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(vector2f, "vector2f");
        buffer.writeFloatLE(vector2f.getX());
        buffer.writeFloatLE(vector2f.getY());
    }


    public Vector3i readVector3i(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        int x = VarInts.readInt(buffer);
        int y = VarInts.readInt(buffer);
        int z = VarInts.readInt(buffer);

        return Vector3i.from(x, y, z);
    }

    public void writeVector3i(ByteBuf buffer, Vector3i vector3i) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(vector3i, "vector3i");
        VarInts.writeInt(buffer, vector3i.getX());
        VarInts.writeInt(buffer, vector3i.getY());
        VarInts.writeInt(buffer, vector3i.getZ());
    }

    public Vector3i readBlockPosition(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        int x = VarInts.readInt(buffer);
        int y = VarInts.readUnsignedInt(buffer);
        int z = VarInts.readInt(buffer);

        return Vector3i.from(x, y, z);
    }

    public void writeBlockPosition(ByteBuf buffer, Vector3i blockPosition) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(blockPosition, "blockPosition");
        VarInts.writeInt(buffer, blockPosition.getX());
        VarInts.writeUnsignedInt(buffer, blockPosition.getY());
        VarInts.writeInt(buffer, blockPosition.getZ());
    }

    public Vector3f readByteRotation(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        float pitch = readByteAngle(buffer);
        float yaw = readByteAngle(buffer);
        float roll = readByteAngle(buffer);
        return Vector3f.from(pitch, yaw, roll);
    }

    public void writeByteRotation(ByteBuf buffer, Vector3f rotation) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(rotation, "rotation");
        writeByteAngle(buffer, rotation.getX());
        writeByteAngle(buffer, rotation.getY());
        writeByteAngle(buffer, rotation.getZ());
    }

    public float readByteAngle(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        return buffer.readByte() * (360f / 256f);
    }

    public void writeByteAngle(ByteBuf buffer, float angle) {
        Preconditions.checkNotNull(buffer, "buffer");
        buffer.writeByte((byte) (angle / (360f / 256f)));
    }

    /*
        Helper array serialization
     */

    public <T> void readArray(ByteBuf buffer, Collection<T> array, BiFunction<ByteBuf, BedrockPacketHelper, T> function) {
        readArray(buffer, array, VarInts::readUnsignedInt, function);
    }

    public <T> void readArray(ByteBuf buffer, Collection<T> array, ToLongFunction<ByteBuf> lengthReader,
                              BiFunction<ByteBuf, BedrockPacketHelper, T> function) {
        long length = lengthReader.applyAsLong(buffer);
        for (int i = 0; i < length; i++) {
            array.add(function.apply(buffer, this));
        }
    }

    public <T> void writeArray(ByteBuf buffer, Collection<T> array, TriConsumer<ByteBuf, BedrockPacketHelper, T> consumer) {
        writeArray(buffer, array, VarInts::writeUnsignedInt, consumer);
    }

    public <T> void writeArray(ByteBuf buffer, Collection<T> array, ObjIntConsumer<ByteBuf> lengthWriter,
                               TriConsumer<ByteBuf, BedrockPacketHelper, T> consumer) {
        lengthWriter.accept(buffer, array.size());
        for (T val : array) {
            consumer.accept(buffer, this, val);
        }
    }


    public <T> void readArray(ByteBuf buffer, Collection<T> array, BedrockSession session,
                              TriFunction<ByteBuf, BedrockPacketHelper, BedrockSession, T> function) {
        int length = VarInts.readUnsignedInt(buffer);
        for (int i = 0; i < length; i++) {
            array.add(function.apply(buffer, this, session));
        }
    }

    public <T> void writeArray(ByteBuf buffer, Collection<T> array, BedrockSession session,
                               QuadConsumer<ByteBuf, BedrockPacketHelper, BedrockSession, T> consumer) {
        VarInts.writeUnsignedInt(buffer, array.size());
        for (T val : array) {
            consumer.accept(buffer, this, session, val);
        }
    }

    public <T> T[] readArray(ByteBuf buffer, T[] array, BiFunction<ByteBuf, BedrockPacketHelper, T> function) {
        ObjectArrayList<T> list = new ObjectArrayList<>();
        readArray(buffer, list, function);
        return list.toArray(array);
    }

    public <T> void writeArray(ByteBuf buffer, T[] array, TriConsumer<ByteBuf, BedrockPacketHelper, T> consumer) {
        VarInts.writeUnsignedInt(buffer, array.length);
        for (T val : array) {
            consumer.accept(buffer, this, val);
        }
    }

    public <T> T[] readArray(ByteBuf buffer, T[] array, BedrockSession session,
                             TriFunction<ByteBuf, BedrockPacketHelper, BedrockSession, T> function) {
        ObjectArrayList<T> list = new ObjectArrayList<>();
        readArray(buffer, list, session, function);
        return list.toArray(array);
    }

    public <T> void writeArray(ByteBuf buffer, T[] array, BedrockSession session,
                               QuadConsumer<ByteBuf, BedrockPacketHelper, BedrockSession, T> consumer) {
        VarInts.writeUnsignedInt(buffer, array.length);
        for (T val : array) {
            consumer.accept(buffer, this, session, val);
        }
    }

    /*
        Non-helper array serialization
     */

    public <T> void readArray(ByteBuf buffer, Collection<T> array, Function<ByteBuf, T> function) {
        int length = VarInts.readUnsignedInt(buffer);
        for (int i = 0; i < length; i++) {
            array.add(function.apply(buffer));
        }
    }

    public <T> void writeArray(ByteBuf buffer, Collection<T> array, BiConsumer<ByteBuf, T> biConsumer) {
        VarInts.writeUnsignedInt(buffer, array.size());
        for (T val : array) {
            biConsumer.accept(buffer, val);
        }
    }

    public <T> T[] readArray(ByteBuf buffer, T[] array, Function<ByteBuf, T> function) {
        ObjectArrayList<T> list = new ObjectArrayList<>();
        readArray(buffer, list, function);
        return list.toArray(array);
    }

    public <T> void writeArray(ByteBuf buffer, T[] array, BiConsumer<ByteBuf, T> biConsumer) {
        VarInts.writeUnsignedInt(buffer, array.length);
        for (T val : array) {
            biConsumer.accept(buffer, val);
        }
    }

    public <T> void readArrayShortLE(ByteBuf buffer, Collection<T> array, Function<ByteBuf, T> function) {
        int length = buffer.readUnsignedShortLE();
        for (int i = 0; i < length; i++) {
            array.add(function.apply(buffer));
        }
    }

    public <T> void writeArrayShortLE(ByteBuf buffer, Collection<T> array, BiConsumer<ByteBuf, T> biConsumer) {
        buffer.writeShortLE(array.size());
        for (T val : array) {
            biConsumer.accept(buffer, val);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T readTag(ByteBuf buffer) {
        try (NBTInputStream reader = NbtUtils.createNetworkReader(new ByteBufInputStream(buffer))) {
            return (T) reader.readTag();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void writeTag(ByteBuf buffer, T tag) {
        try (NBTOutputStream writer = NbtUtils.createNetworkWriter(new ByteBufOutputStream(buffer))) {
            writer.writeTag(tag);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T readTagValue(ByteBuf buffer, NbtType<T> type) {
        try (NBTInputStream reader = NbtUtils.createNetworkReader(new ByteBufInputStream(buffer))) {
            return reader.readValue(type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void writeTagValue(ByteBuf buffer, T tag) {
        try (NBTOutputStream writer = NbtUtils.createNetworkWriter(new ByteBufOutputStream(buffer))) {
            writer.writeValue(tag);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
