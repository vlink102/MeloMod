package me.vlink102.melomod.util.http.packets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.vlink102.melomod.util.http.Packet;

public class PacketPlayOutDisconnect extends Packet {
    private final String uuid;
    private final String name;

    public PacketPlayOutDisconnect(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public String toString() {
        JsonObject object = new JsonObject();
        object.addProperty("uuid", uuid);
        object.addProperty("name", name);
        object.addProperty("packet-id", bind().getPacketID());
        return object.toString();
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public Packet parse(String json) {
        JsonElement element = new JsonParser().parse(json);
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            String uuid = object.get("uuid").getAsString();
            String name = object.get("name").getAsString();
            return new PacketPlayOutDisconnect(uuid, name);
        }
        return null;
    }

    @Override
    public PacketID bind() {
        return PacketID.DISCONNECT;
    }


}
