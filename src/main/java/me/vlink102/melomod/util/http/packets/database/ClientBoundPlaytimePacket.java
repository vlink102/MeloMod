package me.vlink102.melomod.util.http.packets.database;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import me.vlink102.melomod.util.http.Packet;

@Getter
public class ClientBoundPlaytimePacket extends Packet {
    private final Long playtime;
    private final String targetUUID;
    private final String targetName;

    public ClientBoundPlaytimePacket(final Long playtime, final String targetUUID, final String targetName) {
        this.playtime = playtime;
        this.targetUUID = targetUUID;
        this.targetName = targetName;
    }

    @Override
    public PacketID bind() {
        return PacketID.PLAYTIME;
    }

    @Override
    public Packet parse(String json) {
        JsonElement element = new JsonParser().parse(json);
        if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();
            Long playtime = jsonObject.get("playtime").getAsLong();
            String targetUUID = jsonObject.get("targetUUID").getAsString();
            String targetName = jsonObject.get("targetName").getAsString();
            return new ClientBoundPlaytimePacket(playtime, targetUUID, targetName);        }
        return null;
    }

    @Override
    public String toString() {
        JsonObject o = new JsonObject();
        o.addProperty("playtime", playtime);
        o.addProperty("targetUUID", targetUUID);
        o.addProperty("targetName", targetName);
        o.addProperty("packet-id", bind().getPacketID());
        return o.toString();
    }
}
