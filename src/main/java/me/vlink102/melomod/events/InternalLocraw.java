package me.vlink102.melomod.events;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.LocrawEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import cc.polyfrost.oneconfig.utils.hypixel.LocrawInfo;
import cc.polyfrost.oneconfig.utils.hypixel.LocrawUtil;
import me.vlink102.melomod.MeloMod;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;

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

    @Subscribe
    private void onServerTransfer(LocrawEvent event) {
        if (!HypixelUtils.INSTANCE.isHypixel()) return;
        if (Minecraft.getMinecraft().thePlayer == null) return;
        LocrawInfo info = event.info;
        System.out.println("GameMode: " + info.getGameMode());
    }
}
