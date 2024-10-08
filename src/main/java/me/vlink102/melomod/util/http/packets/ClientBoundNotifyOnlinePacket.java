package me.vlink102.melomod.util.http.packets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import me.vlink102.melomod.util.http.Packet;
import me.vlink102.melomod.util.game.SkyblockUtil;

public class ClientBoundNotifyOnlinePacket extends Packet {
    private final Boolean online;
    @Getter
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
            String name = SkyblockUtil.getAsString("online-name",jsonObject);
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

    public Boolean isOnline() {
        return online;
    }
}
