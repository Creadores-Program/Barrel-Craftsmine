package com.nukkitx.protocol.bedrock;

import com.nukkitx.network.raknet.RakNetClient;
import com.nukkitx.network.raknet.RakNetClientSession;
import com.nukkitx.network.util.EventLoops;
import com.nukkitx.protocol.bedrock.wrapper.BedrockWrapperSerializer;
import com.nukkitx.protocol.bedrock.wrapper.BedrockWrapperSerializers;
import io.netty.channel.EventLoopGroup;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

public class BedrockClient extends Bedrock {
    private final RakNetClient rakNetClient;
    BedrockClientSession session;

    public BedrockClient(InetSocketAddress bindAddress) {
        this(bindAddress, EventLoops.commonGroup(), EventLoops.commonGroup());
    }

    public BedrockClient(InetSocketAddress bindAddress, EventLoopGroup eventLoopGroup) {
        this(bindAddress, eventLoopGroup, eventLoopGroup);
    }

    public BedrockClient(InetSocketAddress bindAddress, EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
        this(bindAddress, bossGroup, workerGroup, 8);
    }

    /**
     * Convenience constructor: same EventLoopGroup for boss+worker, but lets
     * the caller pin the RakNet protocol version. This is the form used by
     * Player.login() — it passes the codec's raknetProtocolVersion (8 for
     * MCPE 1.1 v113) so the UnconnectedPing handshake uses the right magic
     * byte instead of the default 10.
     */
    public BedrockClient(InetSocketAddress bindAddress, EventLoopGroup eventLoopGroup, int raknetProtocolVersion) {
        this(bindAddress, eventLoopGroup, eventLoopGroup, raknetProtocolVersion);
    }

    /**
     * Full constructor that also lets the caller pin the RakNet protocol
     * version. This is critical: the RakNetClient defaults to protocol
     * version 10, but MCPE 1.1 (v113) servers only accept version 8 and
     * reply with "unsupported Raknet version 10" otherwise. Bedrock 1.7+
     * servers use 9 or 10, so the caller must pass the version that
     * matches the downstream codec.
     *
     * <p>The {@code raknetProtocolVersion} is applied to the underlying
     * RakNetClient immediately (before bind) so the UnconnectedPing
     * handshake uses the right magic byte.</p>
     */
    public BedrockClient(InetSocketAddress bindAddress, EventLoopGroup bossGroup, EventLoopGroup workerGroup, int raknetProtocolVersion) {
        super(bossGroup, workerGroup);
        this.rakNetClient = new RakNetClient(bindAddress, bossGroup);
        this.rakNetClient.setProtocolVersion(raknetProtocolVersion);
        // Bind is done lazily in ensureBound() on first use (ping/directConnect).
    }

    /**
     * Ensures the underlying RakNetClient is bound before any connect/ping
     * operation. RakNetClient.connect() throws IllegalStateException
     * ("RakNet has not been started") if isRunning() is false — bind() is
     * what flips it to true. This method binds synchronously (join) on the
     * first call and is a no-op on subsequent calls.
     *
     * <p>If bind fails, the full cause chain is wrapped in a RuntimeException
     * so the caller (Player.login → LoginHPacket) can log the real reason
     * (e.g. "Address already in use", "Permission denied", or a Netty
     * channel registration issue).</p>
     */
    private void ensureBound() {
        if (this.rakNetClient.isRunning()) {
            return;
        }
        try {
            this.rakNetClient.bind().join();
        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            if (cause == null) cause = e;
            throw new RuntimeException("Failed to bind RakNetClient to "
                + this.rakNetClient.getBindAddress()
                + ": " + cause.getClass().getSimpleName() + ": " + cause.getMessage(), cause);
        }
    }

    @Override
    protected void onTick() {
        if (this.session != null) {
            this.session.tick();
        }
    }

    @Override
    public RakNetClient getRakNet() {
        return this.rakNetClient;
    }

    public void setRakNetVersion(int version) {
        this.rakNetClient.setProtocolVersion(version);
    }

    @Override
    public void close(boolean force) {
        if (this.session != null && !this.session.isClosed()) {
            this.session.disconnect();
        }
        this.rakNetClient.close(force);
        this.tickFuture.cancel(false);
    }

    public CompletableFuture<BedrockClientSession> connect(InetSocketAddress address) {
        return this.connect(address, 10, TimeUnit.SECONDS);
    }

    public CompletableFuture<BedrockClientSession> connect(InetSocketAddress address, long timeout, TimeUnit unit) {
        // Bind lazily before pinging — RakNetClient.ping() also requires
        // isRunning() to be true (it sends an unconnected ping through the
        // bound DatagramChannel).
        try {
            ensureBound();
        } catch (RuntimeException e) {
            return CompletableFuture.failedFuture(e);
        }
        return this.ping(address, timeout, unit).thenApply(pong -> {
            int port;
            if (address.getAddress() instanceof Inet4Address && pong.getIpv4Port() != -1) {
                port = pong.getIpv4Port();
            } else if (address.getAddress() instanceof Inet6Address && pong.getIpv6Port() != -1) {
                port = pong.getIpv6Port();
            } else {
                port = address.getPort();
            }

            return new InetSocketAddress(address.getAddress(), port);
        }).thenCompose(this::directConnect);
    }

    public CompletableFuture<BedrockClientSession> directConnect(InetSocketAddress address) {
        ensureBound();
        CompletableFuture<BedrockClientSession> future = new CompletableFuture<>();

        RakNetClientSession connection = this.rakNetClient.connect(address);
        BedrockWrapperSerializer serializer = BedrockWrapperSerializers.getSerializer(connection.getProtocolVersion());
        this.session = new BedrockClientSession(connection, connection.getEventLoop(), serializer);
        BedrockRakNetSessionListener.Client listener = new BedrockRakNetSessionListener.Client(this.session,
                connection, this, future);
        connection.setListener(listener);
        return future;
    }

    public CompletableFuture<BedrockPong> ping(InetSocketAddress address) {
        return this.ping(address, 10, TimeUnit.SECONDS);
    }

    public CompletableFuture<BedrockPong> ping(InetSocketAddress address, long timeout, TimeUnit unit) {
        ensureBound();
        return this.rakNetClient.ping(address, timeout, unit).thenApply(BedrockPong::fromRakNet);
    }

    public BedrockClientSession getSession() {
        return session;
    }
}
