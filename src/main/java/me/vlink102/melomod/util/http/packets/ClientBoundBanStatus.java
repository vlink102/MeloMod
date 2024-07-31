package me.vlink102.melomod.util.http.packets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.vlink102.melomod.util.game.SkyblockUtil;
import me.vlink102.melomod.util.http.Ban;
import me.vlink102.melomod.util.http.Packet;

public class ClientBoundBanStatus extends Packet {
    private final Ban banReason;
    private final Boolean banned;

    public ClientBoundBanStatus(Ban banReason, Boolean banned) {
        this.banReason = banReason;
        this.banned = banned;
    }

    @Override
    public Packet parse(String json) {
        JsonElement element = new JsonParser().parse(json);
        if (element.isJsonObject()) {
            JsonObject o = element.getAsJsonObject();
            boolean banned = SkyblockUtil.getAsBoolean("banned", o);
            Ban banReason = null;
            if (banned) {
                banReason = Ban.parse(SkyblockUtil.getAsJsonObject("ban", o));
            }
            return new ClientBoundBanStatus(banReason, banned);
        }
        return null;
    }

    @Override
    public String toString() {
        JsonObject o = new JsonObject();
        o.addProperty("banned", banned);
        if (banned) {
            o.add("ban", banReason.toJson());
        }
        o.addProperty("packet-id", bind().getPacketID());
        return o.toString();
    }

    @Override
    public Packet.PacketID bind() {
        return PacketID.BAN_PACKET;
    }

    public Ban getBanReason() {
        return banReason;
    }

    public Boolean getBanned() {
        return banned;
    }
}
