package me.vlink102.melomod.util.http.packets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.vlink102.melomod.util.http.Packet;
import me.vlink102.melomod.util.game.SkyblockUtil;

public class PacketPlayOutChat extends Packet {
    private final String contents;
    private final String uuid;
    private final String name;
    private final String targetName;

    public PacketPlayOutChat(final String contents, final String uuid, final String name, final String targetUUID) {
        this.contents = contents;
        this.uuid = uuid;
        this.name = name;
        this.targetName = targetUUID;
    }

    public String getTargetName() {
        return targetName;
    }

    @Override
    public String toString() {
        JsonObject o = new JsonObject();
        o.addProperty("chat-message", contents);
        o.addProperty("chat-uuid", uuid);
        o.addProperty("chat-name", name);
        o.addProperty("target", targetName);
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
            String contents = SkyblockUtil.getAsString("chat-message",o);
            String uuid = SkyblockUtil.getAsString("chat-uuid",o);
            String name = SkyblockUtil.getAsString("chat-name",o);
            String targetName = SkyblockUtil.getAsString("target",o);
            return new PacketPlayOutChat(contents, uuid, name, targetName);
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
