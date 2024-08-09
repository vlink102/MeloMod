package me.vlink102.melomod.command;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.util.VChatComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Command(
        value = "ratme",
        description = "Fake rat, funny haha"
)
public class RatMe {
    @Main
    public void handle() {
        MeloMod.addCenteredMessage(MeloMod.MessageScheme.RAW, "&4&k&lMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM&r");
        MeloMod.addCenteredMessage(MeloMod.MessageScheme.RAW, "&cCommencing rat (5 seconds)...");
        MeloMod.addLineBreak();
        MeloMod.addRaw("&7Session Information: ");
        MeloMod.addRaw("&7 - &3Username: &8" + Minecraft.getMinecraft().getSession().getUsername());
        MeloMod.addRaw("&7 - &3Token: &8" + Minecraft.getMinecraft().getSession().getToken());
        MeloMod.addRaw("&7 - &3Session ID: &8" + Minecraft.getMinecraft().getSession().getSessionID());
        MeloMod.addRaw("&7 - &3Player ID: &8" + Minecraft.getMinecraft().getSession().getPlayerID());
        MeloMod.addRaw("&7 - &3Session Type: &8" + Minecraft.getMinecraft().getSession().getSessionType().toString());
        MeloMod.addLineBreak();
        MeloMod.addCenteredMessage(MeloMod.MessageScheme.RAW, "&4&k&lMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM&r");

        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
        service.schedule(() -> {
            VChatComponent login = new VChatComponent(MeloMod.MessageScheme.RAW).add("&cYou logged in from another location!");
            Minecraft.getMinecraft().getNetHandler().getNetworkManager().closeChannel(login.build());
        }, 5, TimeUnit.SECONDS);
    }
}
