package me.vlink102.melomod.util.http.packets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import me.vlink102.melomod.util.http.BanPacket;
import me.vlink102.melomod.util.http.Packet;
import me.vlink102.melomod.util.game.SkyblockUtil;

public class ClientBoundForceDisconnectPacket extends Packet {
    @Getter
    private final String closedID;
    @Getter
    private final String reason;
    @Getter
    private final String bannedBy;
    private final Boolean isBan;
    private final BanPacket banPacket;

    public ClientBoundForceDisconnectPacket(String closedID, String reason, String bannedBy, Boolean isBan, BanPacket banPacket) {
        this.closedID = closedID;
        this.reason = reason;
        this.bannedBy = bannedBy;
        this.isBan = isBan;
        this.banPacket = banPacket;
    }

    public BanPacket getBan() {
        return banPacket;
    }

    public boolean isBan() {
        return isBan;
    }

    @Override
    public String toString() {
        JsonObject o = new JsonObject();
        o.addProperty("closed-id", closedID);
        o.addProperty("admin", bannedBy);
        o.addProperty("reason", reason);
        o.addProperty("is-ban", bannedBy);
        if (isBan) {
            JsonObject object = banPacket.toJson();
            object.add("ban", object);
        }
        o.addProperty("packet-id", bind().getPacketID());
        return o.toString();
    }

    @Override
    public PacketID bind() {
        return PacketID.SERVER_CLOSED_CONNECTION;
    }


    
    @Override
    public Packet parse(String json) {
        JsonElement element = new JsonParser().parse(json);
        if (element.isJsonObject()) {
            JsonObject o = element.getAsJsonObject();
            String closedID = SkyblockUtil.getAsString("closed-id",o);
            String reason = SkyblockUtil.getAsString("reason",o);
            String bannedBy = SkyblockUtil.getAsString("admin",o);
            boolean isBan = o.get("is-ban").getAsBoolean();
            BanPacket banPacketParsed = null;
            if (isBan) {
                banPacketParsed = BanPacket.parse(o.getAsJsonObject("ban"));
            }
            return new ClientBoundForceDisconnectPacket(closedID, reason, bannedBy, isBan, banPacketParsed);
        }
        return null;
    }
}
