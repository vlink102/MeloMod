package me.vlink102.melomod.util.http.packets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.vlink102.melomod.util.http.Packet;
import me.vlink102.melomod.util.game.SkyblockUtil;

public class ServerBoundNotifyOnlinePacket extends Packet {
    private final String uuid;
    private final String name;

    public ServerBoundNotifyOnlinePacket(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public String toString() {
        JsonObject o = new JsonObject();
        o.addProperty("online_uuid", uuid);
        o.addProperty("pretty_name", name);
        o.addProperty("packet-id", bind().getPacketID());
        return o.toString();
    }

    @Override
    public PacketID bind() {
        return PacketID.OPENED_CONNECTION;
    }

    @Override
    public Packet parse(String json) {
        JsonElement element = new JsonParser().parse(json);
        if (element.isJsonObject()) {
            JsonObject o = element.getAsJsonObject();
            String onlineUuid = SkyblockUtil.getAsString("online_uuid",o);
            String prettyName = SkyblockUtil.getAsString("pretty_name",o);
            return new ServerBoundNotifyOnlinePacket(onlineUuid, prettyName);
        }
        return null;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}