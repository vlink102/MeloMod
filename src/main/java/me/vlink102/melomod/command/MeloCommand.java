package me.vlink102.melomod.command;

import me.vlink102.melomod.MeloMod;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;

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
    @Main
    private void handle() {
        MeloMod.config.openGui();
    }
}