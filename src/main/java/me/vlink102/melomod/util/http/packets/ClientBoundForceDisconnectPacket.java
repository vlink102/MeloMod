package me.vlink102.melomod.util.http.packets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.vlink102.melomod.util.http.Ban;
import me.vlink102.melomod.util.http.Packet;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ClientBoundForceDisconnectPacket extends Packet {
    private final String closedID;
    private final String reason;
    private final String bannedBy;
    private final Boolean isBan;
    private final Ban ban;

    public ClientBoundForceDisconnectPacket(String closedID, String reason, String bannedBy, Boolean isBan, Ban ban) {
        this.closedID = closedID;
        this.reason = reason;
        this.bannedBy = bannedBy;
        this.isBan = isBan;
        this.ban = ban;
    }

    public Ban getBan() {
        return ban;
    }

    public boolean isBan() {
        return isBan;
    }

    public String getBannedBy() {
        return bannedBy;
    }

    public String getClosedID() {
        return closedID;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        JsonObject o = new JsonObject();
        o.addProperty("closed-id", closedID);
        o.addProperty("admin", bannedBy);
        o.addProperty("reason", reason);
        o.addProperty("is-ban", bannedBy);
        if (isBan) {
            JsonObject object = ban.toJson();
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
            String closedID = o.get("closed-id").getAsString();
            String reason = o.get("reason").getAsString();
            String bannedBy = o.get("admin").getAsString();
            boolean isBan = o.get("is-ban").getAsBoolean();
            Ban banParsed = null;
            if (isBan) {
                banParsed = Ban.parse(o.getAsJsonObject("ban"));
            }
            return new ClientBoundForceDisconnectPacket(closedID, reason, bannedBy, isBan, banParsed);
        }
        return null;
    }
}
