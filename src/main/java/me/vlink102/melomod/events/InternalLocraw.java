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
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;

import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

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

            mod.skyblockUtil.requestUpdate(false);

        }
    }

    public synchronized JsonObject getGuildInformation(UUID uuid) {
        mod.apiUtil
                .newHypixelApiRequest("guild")
                .queryArgument("player", "" + uuid)
                .requestJson()
                .handle((jsonObject, ex) -> {
                    if (jsonObject != null && jsonObject.has("success") && jsonObject.get("success").getAsBoolean()) {
                        if (!jsonObject.has("guild")) return null;

                        return jsonObject.get("guild").getAsJsonObject();
                    }
                    return null;
                });
        return null;
    }

    public synchronized JsonObject getPlayerStatus(UUID uuid) {
        HashMap<String, String> args = new HashMap<>();
        args.put("uuid", "" + uuid);
        mod.apiUtil
                .newHypixelApiRequest("status")
                .queryArgument("uuid", "" + uuid)
                .requestJson()
                .handle((jsonObject, ex) -> {
                    if (jsonObject != null && jsonObject.has("success") && jsonObject.get("success").getAsBoolean()) {
                        return jsonObject.get("session").getAsJsonObject();
                    }
                    return null;
                });
        return null;
    }
}
