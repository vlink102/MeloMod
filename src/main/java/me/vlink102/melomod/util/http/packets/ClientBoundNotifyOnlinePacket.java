package me.vlink102.melomod.util.http.packets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.vlink102.melomod.util.http.Packet;

public class ClientBoundNotifyOnlinePacket extends Packet {
    private final Boolean online;
    private final String name;

    public ClientBoundNotifyOnlinePacket(final Boolean online, final String name) {
        this.online = online;
        this.name = name;
    }

    @Override
    public Packet parse(String json) {
        JsonElement element = new JsonParser().parse(json);
        if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();
            String name = jsonObject.get("online-name").getAsString();
            boolean online = jsonObject.get("online").getAsBoolean();
            return new ClientBoundNotifyOnlinePacket(online, name);
        }
        return null;
    }

    @Override
    public String toString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("online", online);
        jsonObject.addProperty("online-name", name);
        jsonObject.addProperty("packet-id", bind().getPacketID());
        return jsonObject.toString();
    }

    @Override
    public PacketID bind() {
        return PacketID.NOTIFY_ONLINE;
    }

    public String getName() {
        return name;
    }

    public Boolean isOnline() {
        return online;
    }
}
