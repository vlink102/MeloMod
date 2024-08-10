package me.vlink102.melomod.command.client;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.util.StringUtils;
import me.vlink102.melomod.util.VChatComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;

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
        VChatComponent tokenHover = new VChatComponent(MeloMod.MessageScheme.RAW);
        tokenHover.add("&7 - &3Token: &8");
        tokenHover.add(
                "&7(Hover)",
                Minecraft.getMinecraft().getSession().getToken(),
                (ClickEvent) null,
                StringUtils.VComponentSettings.INHERIT_NONE
        );
        MeloMod.addMessage(tokenHover);
        MeloMod.addRaw("&7 - &3Player ID: &8" + Minecraft.getMinecraft().getSession().getPlayerID());
        MeloMod.addLineBreak();
        MeloMod.addCenteredMessage(MeloMod.MessageScheme.RAW, "&4&k&lMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM&r");

        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
        service.schedule(() -> {
            VChatComponent login = new VChatComponent(MeloMod.MessageScheme.RAW).add("&cYou logged in from another location!");
            Minecraft.getMinecraft().getNetHandler().getNetworkManager().closeChannel(login.build());
        }, 5, TimeUnit.SECONDS);
    }
}
