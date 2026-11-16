package com.craftsmine.nkm.network;

import cn.nukkit.Player;
import com.craftsmine.nkm.protocol.LegacyProtocol;
import io.netty.channel.ChannelId;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registro concurrente de sesiones legadas activas (Protocolo 80 - 112).
 */
public final class SessionProtocolRegistry {

    public static final class LegacySessionState {
        private final ChannelId channelId;
        private final String remoteAddress;
        private volatile int rawProtocol;
        private volatile LegacyProtocol protocol;
        private volatile String username = "Desconocido";
        private volatile boolean bypassedResourcePacks;
        private volatile boolean modernDetached;
        private final long connectedAtMillis;

        public LegacySessionState(ChannelId channelId, SocketAddress remoteAddress, int rawProtocol) {
            this.channelId = channelId;
            this.remoteAddress = remoteAddress == null ? "unknown" : remoteAddress.toString();
            this.rawProtocol = rawProtocol;
            this.protocol = LegacyProtocol.resolve(rawProtocol);
            this.connectedAtMillis = System.currentTimeMillis();
        }

        public ChannelId getChannelId() {
            return channelId;
        }

        public String getRemoteAddress() {
            return remoteAddress;
        }

        public int getRawProtocol() {
            return rawProtocol;
        }

        public LegacyProtocol getProtocol() {
            return protocol;
        }

        public void updateProtocol(int rawProtocol) {
            this.rawProtocol = rawProtocol;
            this.protocol = LegacyProtocol.resolve(rawProtocol);
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            if (username != null && !username.isBlank()) {
                this.username = username;
            }
        }

        public boolean isBypassedResourcePacks() {
            return bypassedResourcePacks;
        }

        public void setBypassedResourcePacks(boolean bypassedResourcePacks) {
            this.bypassedResourcePacks = bypassedResourcePacks;
        }

        public boolean isModernDetached() {
            return modernDetached;
        }

        public void setModernDetached(boolean modernDetached) {
            this.modernDetached = modernDetached;
        }

        public long getConnectedAtMillis() {
            return connectedAtMillis;
        }
    }

    private final Map<ChannelId, LegacySessionState> byChannel = new ConcurrentHashMap<>();
    private final Map<String, LegacySessionState> byUsername = new ConcurrentHashMap<>();

    public LegacySessionState registerOrUpdate(ChannelId channelId, SocketAddress remote, int rawProtocol, String username) {
        LegacySessionState state = byChannel.compute(channelId, (id, existing) -> {
            if (existing == null) {
                return new LegacySessionState(id, remote, rawProtocol);
            }
            existing.updateProtocol(rawProtocol);
            return existing;
        });
        if (username != null && !username.isBlank()) {
            state.setUsername(username);
            byUsername.put(username.toLowerCase(), state);
        }
        return state;
    }

    public LegacySessionState getByChannel(ChannelId channelId) {
        return byChannel.get(channelId);
    }

    public LegacySessionState getByPlayer(Player player) {
        if (player == null || player.getName() == null) {
            return null;
        }
        return byUsername.get(player.getName().toLowerCase());
    }

    public LegacySessionState removeByChannel(ChannelId channelId) {
        LegacySessionState removed = byChannel.remove(channelId);
        if (removed != null && removed.getUsername() != null) {
            byUsername.remove(removed.getUsername().toLowerCase(), removed);
        }
        return removed;
    }

    public Collection<LegacySessionState> getActiveSessions() {
        return Collections.unmodifiableCollection(byChannel.values());
    }

    public void clear() {
        byChannel.clear();
        byUsername.clear();
    }
}
