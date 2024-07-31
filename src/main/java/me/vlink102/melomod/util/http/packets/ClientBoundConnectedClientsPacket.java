package me.vlink102.melomod.util.http.packets;

import com.google.gson.*;
import me.vlink102.melomod.events.InternalLocraw;
import me.vlink102.melomod.util.game.SkyblockUtil;
import me.vlink102.melomod.util.http.Packet;
import me.vlink102.melomod.util.game.SkyblockUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientBoundConnectedClientsPacket extends Packet {
    private final int page;
    private final JsonArray players;

    public ClientBoundConnectedClientsPacket(final int page, final JsonArray players) {
        this.page = page;
        this.players = players;
    }

    @Deprecated
    public ClientBoundConnectedClientsPacket(final int page, List<String> playerList) {
        this.page = page;
        this.players = new JsonArray();

    }

    public HashMap<String, InternalLocraw.LocrawInfo> getPlayerList() {
        HashMap<String, InternalLocraw.LocrawInfo> list = new HashMap<>();
        for (JsonElement player : players) {
            JsonObject playerObj = player.getAsJsonObject();
            String name = SkyblockUtil.getAsString("name",playerObj);
            String map = SkyblockUtil.getAsString("map",playerObj);
            String gametype = SkyblockUtil.getAsString("gametype",playerObj);
            String gamemode = SkyblockUtil.getAsString("gamemode",playerObj);
            String serverID = SkyblockUtil.getAsString("serverID",playerObj);
            String serverIP = SkyblockUtil.getAsString("serverIP",playerObj);
            ServerBoundLocrawPacket.ServerType isHypixel = ServerBoundLocrawPacket.ServerType.valueOf(playerObj.get("isHypixel").getAsString());
            list.put(name, new InternalLocraw.LocrawInfo(serverID, gamemode, gametype, map, serverIP, isHypixel));
        }
        return list;
    }

    public int getPage() {
        return page;
    }

    public JsonArray getPlayers() {
        return players;
    }

    @Override
    public String toString() {
        JsonObject o = new JsonObject();
        o.addProperty("page", page);
        o.add("online_players", players);
        o.addProperty("packet-id", bind().getPacketID());
        return o.toString();
    }

    @Override
    public PacketID bind() {
        return PacketID.CONNECTED_CLIENTS;
    }

    @Override
    public Packet parse(String json) {
        JsonElement element = new JsonParser().parse(json);
        if (element.isJsonObject()) {
            JsonObject o = element.getAsJsonObject();
            int page = SkyblockUtil.getAsInteger("page", o);
            JsonArray players = SkyblockUtil.getAsJsonArray("online_players", o);

            return new ClientBoundConnectedClientsPacket(page, players);
        }
        return null;
    }
}
