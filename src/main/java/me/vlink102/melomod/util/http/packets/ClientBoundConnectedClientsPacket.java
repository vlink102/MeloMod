package me.vlink102.melomod.util.http.packets;

import com.google.gson.*;
import me.vlink102.melomod.util.game.SkyblockUtil;
import me.vlink102.melomod.util.http.Packet;

import java.util.ArrayList;
import java.util.List;

public class ClientBoundConnectedClientsPacket extends Packet {
    private final int page;
    private final JsonArray players;

    public ClientBoundConnectedClientsPacket(final int page, final JsonArray players) {
        this.page = page;
        this.players = players;
    }

    public ClientBoundConnectedClientsPacket(final int page, List<String> playerList) {
        this.page = page;
        this.players = new JsonArray();
        for (String s : playerList) {
            players.add(new JsonPrimitive(s));
        }
    }

    public List<String> getPlayerList() {
        List<String> list = new ArrayList<>();
        for (JsonElement player : players) {
            list.add(player.getAsString());
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
