package org.CreadoresProgram.CraftsMine.meJustinLib.bedrockserverquery.data;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;
public class BedrockQuery{
    public boolean online;
    public String motd;
    public int protocolVersion;
    public String minecraftVersion;
    public int playerCount;
    public int maxPlayers;
    public String software;
    public String gamemode;
    private BedrockQuery(boolean online, String motd, int protocolVersion, String minecraftVersion, int playerCount, int maxPlayers, String software, String gamemode) {
        this.online = online;
        this.motd = motd;
        this.protocolVersion = protocolVersion;
        this.minecraftVersion = minecraftVersion;
        this.playerCount = playerCount;
        this.maxPlayers = maxPlayers;
        this.software = software;
        this.gamemode = gamemode;
    }

    private static final byte IDUnconnectedPing = 0x01;
    private static final byte[] unconnectedMessageSequence = {0x00, (byte) 0xff, (byte) 0xff, 0x00, (byte) 0xfe, (byte) 0xfe, (byte) 0xfe, (byte) 0xfe, (byte) 0xfd, (byte) 0xfd, (byte) 0xfd, (byte) 0xfd, 0x12, 0x34, 0x56, 0x78};
    private static long dialerID = new Random().nextLong();

    public static BedrockQuery create(String serverAddress, int port) {
        try {
            InetAddress address = InetAddress.getByName(serverAddress);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            dataOutputStream.writeByte(IDUnconnectedPing);
            dataOutputStream.writeLong(System.currentTimeMillis() / 1000);
            dataOutputStream.write(unconnectedMessageSequence);
            dataOutputStream.writeLong(dialerID++);

            byte[] requestData = outputStream.toByteArray();
            byte[] responseData = new byte[1024 * 1024 * 4];

            DatagramSocket socket = new DatagramSocket();
            DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, address, port);
            socket.send(requestPacket);

            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length);
            socket.setSoTimeout(2000);
            socket.receive(responsePacket);

            String[] splittedData = new String(responsePacket.getData(), 0, responsePacket.getLength()).split(";", 2)[1].split(";");

            int protocol = Integer.parseInt(splittedData[1]);
            int playerCount = Integer.parseInt(splittedData[3]);
            int maxPlayers = Integer.parseInt(splittedData[4]);

            return new BedrockQuery(true, splittedData[0], protocol, splittedData[2], playerCount, maxPlayers, splittedData[6], splittedData[7]);
        } catch (Exception e) {
            return new BedrockQuery(false, "", -1, "", 0, 0, "", "");
        }
    }

}
