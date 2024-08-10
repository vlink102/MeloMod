package me.vlink102.melomod.util.http.packets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import me.vlink102.melomod.util.http.Packet;
import me.vlink102.melomod.util.game.SkyblockUtil;

@Getter
public class ServerBoundRequestConnectionsPacket extends Packet {
    private final int page;

    public ServerBoundRequestConnectionsPacket(final int page) {
        this.page = page;
    }

    @Override
    public String toString() {
        JsonObject o = new JsonObject();
        o.addProperty("online-request-page", page);
        o.addProperty("packet-id", bind().getPacketID());
        return o.toString();
    }

    @Override
    public PacketID bind() {
        return PacketID.REQUEST_CONNECTED;
    }

    @Override
    public Packet parse(String json) {
        JsonElement element = new JsonParser().parse(json);
        if (element.isJsonObject()) {
            JsonObject o = element.getAsJsonObject();
            int onlineRequestPage = o.getAsJsonPrimitive("online-request-page").getAsInt();
            return new ServerBoundRequestConnectionsPacket(onlineRequestPage);
        }
        return null;
    }

}
