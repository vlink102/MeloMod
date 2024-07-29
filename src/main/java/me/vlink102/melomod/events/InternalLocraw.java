package me.vlink102.melomod.events;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.LocrawEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import cc.polyfrost.oneconfig.utils.hypixel.LocrawInfo;
import cc.polyfrost.oneconfig.utils.hypixel.LocrawUtil;
import com.google.gson.JsonObject;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.mixin.SkyblockUtil;
import me.vlink102.melomod.util.ApiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;

import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class InternalLocraw {
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

    public InternalLocraw(MeloMod mod) {
        this.mod = mod;
        EventManager.INSTANCE.register(this);
    }

    private static SkyblockUtil.Location gameMode;

    public static SkyblockUtil.Location getLocation() {
        return gameMode;
    }

    private static String serverID;

    public static String getServerID() {
        return serverID;
    }


    @Subscribe
    private void onServerTransfer(LocrawEvent event) {
        gameMode = SkyblockUtil.Location.parseFromLocraw(event.info.getGameMode());
        serverID = event.info.getServerId();

        if (mod.getPlayerProfile() == null) {
            mod.apiUtil.requestAPI(
                    ApiUtil.HypixelEndpoint.SKYBLOCK_PROFILES,
                    object -> mod.skyblockUtil.updateInformation(object),
                    ApiUtil.HypixelEndpoint.FilledEndpointArgument.uuid()
            );
        }
    }

}
