package me.vlink102.melomod.command.client;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import cc.polyfrost.oneconfig.utils.hypixel.LocrawInfo;
import cc.polyfrost.oneconfig.utils.hypixel.LocrawUtil;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.configuration.MainConfiguration;
import me.vlink102.melomod.util.ItemSerializer;
import me.vlink102.melomod.util.http.ApiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Command(
        value = "wipeme",
        description = "Fake wipe, funny haha"
)
public class WipeMe {
    @Main
    public void handle() {
        scheduleWipe(true, false);
    }

    public static ItemStack getWipeStack(String profileName) {
        String wipe = "{id:\"minecraft:written_book\",Count:1b,tag:{pages:[0:\"{\\\"extra\\\":[\\\"\\nYour Skyblock Profile \\\",{\\\"color\\\":\\\"gold\\\",\\\"text\\\":\\\":CUTENAME:\\\"},{\\\"bold\\\":true,\\\"color\\\":\\\"red\\\",\\\"text\\\":\\\" has been wiped\\\"},{\\\"color\\\":\\\"reset\\\",\\\"text\\\":\\\" as a co-op member was determined to be boosting.\\n\\nIf you think it\\u0027s an error contact \\\"},{\\\"underlined\\\":true,\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://support.hypixel.net/\\\"},\\\"text\\\":\\\"support.hypixel.net\\\"},{\\\"color\\\":\\\"reset\\\",\\\"text\\\":\\\"\\n\\n        \\\"},{\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"run_command\\\",\\\"value\\\":\\\"/melomod closebookinternal\\\"},\\\"text\\\":\\\"DISMISS\\\"}],\\\"text\\\":\\\"\\\"}\"],author:\"vlink102\",title:\"Profile Wipe\",resolved:1b},Damage:0s}".replaceAll(":CUTENAME:", profileName);
        try {
            return ItemStack.loadItemStackFromNBT(JsonToNBT.getTagFromJson(wipe));
        } catch (NBTException e) {
            throw new RuntimeException(e);
        }
    }

    public static void scheduleWipe(boolean warpPrototype, boolean showBook) {
        String cuteName = "Banana";
        if (MeloMod.getPlayerProfile() != null)  {
            cuteName = MeloMod.getPlayerProfile().getCuteName();
        }
        MeloMod.addRaw("&eYour SkyBlock Profile &b" + cuteName + "&r &c&lhas been wiped&r &eas a co-op member was determined to be boosting.");
        MeloMod.addRaw("&eIf you think it's an error you can join the support: &b&nsupport.hypixel.net&r");
        if (showBook) {
            ItemStack stack = getWipeStack(cuteName);
            GuiScreenBook guiScreenBook = new GuiScreenBook(Minecraft.getMinecraft().thePlayer, stack, false);
            Minecraft.getMinecraft().displayGuiScreen(guiScreenBook);
        }
        if (warpPrototype) ApiUtil.sendCommandLater("lobby skyblock");
    }
}
