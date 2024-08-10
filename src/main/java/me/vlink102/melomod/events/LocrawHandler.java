package me.vlink102.melomod.events;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.LocrawEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.util.enums.skyblock.Location;
import me.vlink102.melomod.util.http.ApiUtil;
import me.vlink102.melomod.util.http.CommunicationHandler;
import me.vlink102.melomod.util.http.packets.ServerBoundLocrawPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

public class LocrawHandler {
    private final MeloMod mod;

    public class Coords {
        private final float x;
        private final float y;
        private final float z;
        public Coords(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float getX() {
            return x;
        }
        public float getY() {
            return y;
        }
        public float getZ() {
            return z;
        }
    }

    public BlockPos getCoords() {
        if (Minecraft.getMinecraft().thePlayer != null) {
            return Minecraft.getMinecraft().thePlayer.getPosition();
        }
        return new BlockPos(0, 0, 0);
    }

    public LocrawHandler(MeloMod mod) {
        this.mod = mod;
        EventManager.INSTANCE.register(this);
    }

    private static Location gameMode;

    public static Location getLocation() {
        return gameMode;
    }

    private static String serverID;

    public static String getServerID() {
        return serverID;
    }

    public static class LocrawInfo {
        private final String serverID;
        private final String gamemode;
        private final String gametype;
        private final String map;
        private final String serverIP;
        private final ServerBoundLocrawPacket.ServerType isHypixel;

        public LocrawInfo(String serverID, String gamemode, String gametype, String map, String serverIP, ServerBoundLocrawPacket.ServerType isHypixel) {
            this.serverID = serverID;
            this.gamemode = gamemode;
            this.gametype = gametype;
            this.map = map;
            this.serverIP = serverIP;
            this.isHypixel = isHypixel;
        }

        public ServerBoundLocrawPacket.ServerType isHypixel() {
            return isHypixel;
        }

        public String getServerIP() {
            return serverIP;
        }

        public String getServerID() {
            return serverID;
        }

        public String getGametype() {
            return gametype;
        }

        public String getGamemode() {
            return gamemode;
        }

        public String getMap() {
            return map;
        }
    }

    public static ServerBoundLocrawPacket.ServerType getType() {
        if (Minecraft.getMinecraft().isSingleplayer()) {
            return ServerBoundLocrawPacket.ServerType.SINGLEPLAYER;
        }
        if (HypixelUtils.INSTANCE.isHypixel()) {
            return ServerBoundLocrawPacket.ServerType.HYPIXEL;
        }
        return ServerBoundLocrawPacket.ServerType.SERVER;
    }

    public static String serverIP() {
        if (Minecraft.getMinecraft().isSingleplayer()) {
            return Minecraft.getMinecraft().getIntegratedServer().getWorldName();
        }
        return Minecraft.getMinecraft().getCurrentServerData().serverIP;
    }

    @Subscribe
    private void onServerTransfer(LocrawEvent event) {
        cc.polyfrost.oneconfig.utils.hypixel.LocrawInfo info = event.info;
        gameMode = Location.parseFromLocraw(info.getGameMode());
        serverID = info.getServerId();

        ServerBoundLocrawPacket packet = new ServerBoundLocrawPacket(info.getMapName(), info.getGameMode(), info.getRawGameType(), info.getServerId(), getType(), serverIP());
        CommunicationHandler.thread.sendPacket(packet);

        if (mod.getPlayerProfile() == null) {
            mod.apiUtil.requestAPI(
                    ApiUtil.HypixelEndpoint.SKYBLOCK_PROFILES,
                    object -> mod.skyblockUtil.updateInformation(object),
                    ApiUtil.HypixelEndpoint.FilledEndpointArgument.uuid()
            );
        }
    }

}
