package me.vlink102.melomod.util.http.packets.database;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import me.vlink102.melomod.util.http.Packet;

@Getter
public class ServerBoundPlaytimeRequestPacket extends Packet {
    private final String target;
    private final String requesterUUID;

    public ServerBoundPlaytimeRequestPacket(String target, String requesterUUID) {
        this.target = target;
        this.requesterUUID = requesterUUID;
    }

    @Override
    public PacketID bind() {
        return PacketID.REQUEST_PLAYTIME;
    }

    @Override
    public Packet parse(String json) {
        JsonElement element = new JsonParser().parse(json);
        if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();
            String target = jsonObject.get("target").getAsString();
            String requesterUUID = jsonObject.get("requesterUUID").getAsString();
            return new ServerBoundPlaytimeRequestPacket(target, requesterUUID);
        }
        return null;
    }

    @Override
    public String toString() {
        JsonObject o = new JsonObject();
        o.addProperty("target", target);
        o.addProperty("requesterUUID", requesterUUID);
        o.addProperty("packet-id", bind().getPacketID());
        return o.toString();
    }
}
