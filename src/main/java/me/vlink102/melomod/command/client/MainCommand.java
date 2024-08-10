package me.vlink102.melomod.command.client;

import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand;
import me.vlink102.melomod.MeloMod;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import me.vlink102.melomod.configuration.MainConfiguration;
import me.vlink102.melomod.util.http.ApiUtil;
import me.vlink102.melomod.util.http.CommunicationHandler;
import me.vlink102.melomod.util.http.packets.ServerBoundRequestConnectionsPacket;
import me.vlink102.melomod.util.translation.Feature;

/**
 * An example command implementing the Command api of OneConfig.
 * Registered in ExampleMod.java with `CommandManager.INSTANCE.registerCommand(new ExampleCommand());`
 *
 * @see Command
 * @see Main
 * @see MeloMod
 */
@Command(value = MeloMod.MODID, description = "Access the " + MeloMod.NAME + " GUI.")
public class MainCommand {
    private final MeloMod mod;

    public MainCommand(MeloMod mod) {
        this.mod = mod;
    }

    @SubCommand(description = "Reload the mod and refresh Hypixel API")
    private void reload() {
        mod.apiUtil.requestAPI(
                ApiUtil.HypixelEndpoint.SKYBLOCK_PROFILES,
                object -> mod.skyblockUtil.updateInformation(object),
                ApiUtil.HypixelEndpoint.FilledEndpointArgument.uuid()
        );
    }

    @SubCommand(
            description = "Toggle debug"
    )
    private void debug() {
        MainConfiguration.debugMessages = !MainConfiguration.debugMessages;
        if (MainConfiguration.debugMessages) {
            MeloMod.addSystemNotification("&9" + Feature.GENERIC_COMMANDS_ENABLED_DEBUG + "&r");
        } else {
            MeloMod.addSystemNotification("&9" + Feature.GENERIC_COMMANDS_DISABLED_DEBUG + "&r");
        }
    }

    @SubCommand(
            description = "View online users"
    )
    private void online(int page) {
        CommunicationHandler.thread.sendPacket(new ServerBoundRequestConnectionsPacket(page));
    }

    @Main
    private void handle() {
        MeloMod.config.openGui();
    }
}