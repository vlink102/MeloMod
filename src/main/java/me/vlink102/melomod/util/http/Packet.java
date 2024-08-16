package me.vlink102.melomod.util.http;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import me.vlink102.melomod.util.http.packets.*;
import me.vlink102.melomod.util.http.packets.database.ClientBoundPlaytimePacket;
import me.vlink102.melomod.util.http.packets.database.ServerBoundPlaytimeRequestPacket;

public class Packet implements PacketParser {
    public static PacketID from(String json) {
        JsonElement element = new JsonParser().parse(json);
        if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();
            int id = jsonObject.get("packet-id").getAsInt();
            return PacketID.from(id);
        }
        return PacketID.ERROR_PACKET;
    }

    public static Packet parseFrom(String json) {
        switch (from(json)) {
            case VERSION_CONTROL:
                return new ServerBoundVersionControlPacket(null).parse(json);
            case REQUEST_CONNECTED:
                return new ServerBoundRequestConnectionsPacket(-1).parse(json);
            case CHAT_MESSAGE:
                return new PacketPlayOutChat(null, null, null, null, null, null).parse(json);
            case OPENED_CONNECTION:
                return new ServerBoundNotifyOnlinePacket(null, null).parse(json);
            case CLOSED_CONNECTION:
                return new ClientBoundDisconnectPacket(null, null).parse(json);
            case CONNECTED_CLIENTS:
                return new ClientBoundConnectedClientsPacket(-1, new JsonArray()).parse(json);
            case SERVER_CLOSED_CONNECTION:
                return new ClientBoundForceDisconnectPacket(null, null, null, null, null).parse(json);
            case VERSION_CONTROL_RESULT:
                return new ClientBoundVersionControlPacket(null, null, null, null, null).parse(json);
            case NOTIFY_ONLINE:
                return new ClientBoundNotifyOnlinePacket(null, null).parse(json);
            case DISCONNECT:
                return new PacketPlayOutDisconnect(null, null).parse(json);
            case LOCRAW_PACKET:
                return new ServerBoundLocrawPacket(null, null, null, null, null, null).parse(json);
            case BAN_PACKET:
                return new ClientBoundBanStatus(null, null).parse(json);
            case PLAYTIME:
                return new ClientBoundPlaytimePacket(null, null, null).parse(json);
            case REQUEST_PLAYTIME:
                return new ServerBoundPlaytimeRequestPacket(null, null).parse(json);
            default:
                return null;
        }
    }

    public Packet parse(String json) {
        return null;
    }

    public PacketID bind() {
        return null;
    }

    public boolean detect(String json) {
        JsonElement element = new JsonParser().parse(json);
        if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();
            int id = jsonObject.get("packet-id").getAsInt();
            return bind().packetID == id;
        }
        return false;
    }

    @Getter
    public enum PacketID {
        /**
         * PacketPlayOutChat.class
         */
        CHAT_MESSAGE(0b0, PacketType.RELAYED),
        /**
         * ClientBoundForceDisconnectPacket.class
         */
        SERVER_CLOSED_CONNECTION(0b1, PacketType.SERVER_CLIENT),
        /**
         * ServerBoundNotifyOnlinePacket.class
         */
        OPENED_CONNECTION(0b10, PacketType.CLIENT_SERVER),
        /**
         * ClientBoundDisconnectPacket.class
         */
        CLOSED_CONNECTION(0b11, PacketType.SERVER_CLIENT),
        /**
         * ServerBoundRequestConnectionsPacket.class
         */
        REQUEST_CONNECTED(0b100, PacketType.CLIENT_SERVER),
        /**
         * ServerBoundVersionControlPacket.class
         */
        VERSION_CONTROL(0b101, PacketType.CLIENT_SERVER),
        /**
         * ClientBoundConnectedClientsPacket.class
         */
        CONNECTED_CLIENTS(0b110, PacketType.SERVER_CLIENT),
        /**
         * ClientBoundVersionControlPacket.class
         */
        VERSION_CONTROL_RESULT(0b111, PacketType.SERVER_CLIENT),
        /**
         * ClientBoundNotifyOnlinePacket.class
         */
        NOTIFY_ONLINE(0b1000, PacketType.SERVER_CLIENT),
        /**
         * PacketPlayOutDisconnect.class
         */
        DISCONNECT(0b1001, PacketType.RELAYED),
        LOCRAW_PACKET(0b1010, PacketType.CLIENT_SERVER),
        BAN_PACKET(0b1011, PacketType.SERVER_CLIENT),
        REQUEST_PLAYTIME(12, PacketType.CLIENT_SERVER),
        PLAYTIME(13, PacketType.SERVER_CLIENT),
        ERROR_PACKET(0xffffffff, null);

        private final int packetID;
        private final PacketType packetType;

        PacketID(int packetID, PacketType type) {
            this.packetID = packetID;
            this.packetType = type;
        }

        public static PacketID from(int packetID) {
            for (PacketID value : values()) {
                if (value.getPacketID() == packetID) {
                    return value;
                }
            }
            return ERROR_PACKET;
        }

        public enum PacketType {
            CLIENT_SERVER,
            SERVER_CLIENT,
            RELAYED
        }
    }
}