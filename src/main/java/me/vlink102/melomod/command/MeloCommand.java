package me.vlink102.melomod.command;

import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand;
import me.vlink102.melomod.MeloMod;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import me.vlink102.melomod.util.ApiUtil;
import net.minecraft.client.Minecraft;

import java.util.concurrent.ExecutionException;

/**
 * An example command implementing the Command api of OneConfig.
 * Registered in ExampleMod.java with `CommandManager.INSTANCE.registerCommand(new ExampleCommand());`
 *
 * @see Command
 * @see Main
 * @see MeloMod
 */
@Command(value = MeloMod.MODID, description = "Access the " + MeloMod.NAME + " GUI.")
public class MeloCommand {
    private final MeloMod mod;

    public MeloCommand(MeloMod mod) {
        this.mod = mod;
    }

    @SubCommand(description = "Reload the mod and refresh Hypixel API")
    private void reloadAPI() {
        mod.apiUtil.requestAPI(
                ApiUtil.HypixelEndpoint.SKYBLOCK_PROFILES,
                object -> mod.skyblockUtil.updateInformation(object),
                ApiUtil.HypixelEndpoint.FilledEndpointArgument.uuid()
        );
    }

    @Main
    private void handle() {
        MeloMod.config.openGui();
    }
}