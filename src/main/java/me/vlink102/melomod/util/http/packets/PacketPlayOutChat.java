package me.vlink102.melomod.util.http.packets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.vlink102.melomod.util.http.Packet;

public class PacketPlayOutChat extends Packet {
    private final String contents;
    private final String uuid;
    private final String name;

    public PacketPlayOutChat(final String contents, final String uuid, final String name) {
        this.contents = contents;
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public String toString() {
        JsonObject o = new JsonObject();
        o.addProperty("chat-message", contents);
        o.addProperty("chat-uuid", uuid);
        o.addProperty("chat-name", name);
        o.addProperty("packet-id", bind().getPacketID());
        return o.toString();
    }

    @Override
    public PacketID bind() {
        return PacketID.CHAT_MESSAGE;
    }

    @Override
    public Packet parse(String json) {
        JsonElement element = new JsonParser().parse(json);
        if (element.isJsonObject()) {
            JsonObject o = element.getAsJsonObject();
            String contents = o.get("chat-message").getAsString();
            String uuid = o.get("chat-uuid").getAsString();
            String name = o.get("chat-name").getAsString();
            return new PacketPlayOutChat(contents, uuid, name);
        }
        return null;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getContents() {
        return contents;
    }
}
