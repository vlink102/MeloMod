package me.vlink102.melomod.util.http.packets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.vlink102.melomod.util.http.Packet;
import me.vlink102.melomod.util.game.SkyblockUtil;

public class ServerBoundLocrawPacket extends Packet {
    private final String map;
    private final String gamemode;
    private final String gametype;
    private final String serverID;
    private final ServerType isHypixel;
    private final String serverIP;

    public enum ServerType {
        SINGLEPLAYER,
        HYPIXEL,
        SERVER,
        OFFLINE
    }

    public ServerBoundLocrawPacket(final String map, final String gamemode, final String gametype, final String serverID, ServerType isHypixel, final String serverIP) {
        this.map = map;
        this.gamemode = gamemode;
        this.gametype = gametype;
        this.serverID = serverID;
        this.isHypixel = isHypixel;
        this.serverIP = serverIP;
    }

    public String getServerIP() {
        return serverIP;
    }

    public ServerType getIsHypixel() {
        return isHypixel;
    }

    public String getGamemode() {
        return gamemode;
    }

    public String getGametype() {
        return gametype;
    }

    public String getServerID() {
        return serverID;
    }

    public String getMap() {
        return map;
    }

    @Override
    public Packet parse(String json) {
        JsonElement element = new JsonParser().parse(json);
        if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();
            String serverIP = SkyblockUtil.getAsString("serverIP",jsonObject);
            ServerType isHypixel = ServerType.valueOf(jsonObject.get("isHypixel").getAsString());
            if (isHypixel == ServerType.HYPIXEL) {
                String serverId = SkyblockUtil.getAsString("serverID",jsonObject);
                String map = SkyblockUtil.getAsString("map",jsonObject);
                String gamemode = SkyblockUtil.getAsString("gamemode",jsonObject);
                String gametype = SkyblockUtil.getAsString("gametype",jsonObject);
                return new ServerBoundLocrawPacket(map, gamemode, gametype, serverId, isHypixel, serverIP);
            }
            return new ServerBoundLocrawPacket(null,null,null, null, isHypixel, serverIP);
        }
        return null;
    }

    @Override
    public String toString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("isHypixel", isHypixel.toString());
        jsonObject.addProperty("serverIP", serverIP);
        jsonObject.addProperty("serverID", serverID);
        jsonObject.addProperty("map", map);
        jsonObject.addProperty("gamemode", gamemode);
        jsonObject.addProperty("gametype", gametype);
        jsonObject.addProperty("packet-id", bind().getPacketID());
        return jsonObject.toString();
    }

    @Override
    public PacketID bind() {
        return PacketID.LOCRAW_PACKET;
    }
}
