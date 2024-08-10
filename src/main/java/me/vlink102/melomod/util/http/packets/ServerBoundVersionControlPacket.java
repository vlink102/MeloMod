package me.vlink102.melomod.util.http.packets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.util.http.Packet;
import me.vlink102.melomod.util.game.SkyblockUtil;

@Getter
public class ServerBoundVersionControlPacket extends Packet {
    private final String uuid;
    private final String version;

    public ServerBoundVersionControlPacket(final String uuid) {
        this.uuid = uuid;
        this.version = MeloMod.VERSION_NEW.toString();
    }

    public ServerBoundVersionControlPacket(final String uuid, final String version) {
        this.uuid = uuid;
        this.version = version;
    }

    @Override
    public String toString() {
        JsonObject o = new JsonObject();
        o.addProperty("version", version);
        o.addProperty("uuid", uuid);
        o.addProperty("packet-id", bind().getPacketID());
        return o.toString();
    }

    @Override
    public PacketID bind() {
        return PacketID.VERSION_CONTROL;
    }

    @Override
    public Packet parse(String json) {
        JsonElement element = new JsonParser().parse(json);
        if (element.isJsonObject()) {
            JsonObject o = element.getAsJsonObject();
            String uuid = SkyblockUtil.getAsString("uuid",o);
            String version = SkyblockUtil.getAsString("version",o);
            return new ServerBoundVersionControlPacket(uuid, version);
        }
        return null;
    }

}
