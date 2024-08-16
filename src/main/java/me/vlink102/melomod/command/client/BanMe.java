package me.vlink102.melomod.command.client;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Description;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.configuration.MainConfiguration;
import me.vlink102.melomod.util.StringUtils;
import me.vlink102.melomod.util.VChatComponent;
import me.vlink102.melomod.util.game.neu.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.relauncher.IFMLCallHook;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Command(
        value = "banme",
        description = "Fake ban, funny haha"
)
public class BanMe {
    @Main
    public void handle(@Description(autoCompletesTo = {"7d", "30d", "90d", "360d", "permanent", "blocked"}) String s, @Description(autoCompletesTo = {"boosting_skyblock", "security", "recovery_stage", "advertising", "watchdog", "inappropriate_website", "cheating", "inappropriate_items", "suspicious_activity", "exploiting", "boosting_account", "inappropriate_build", "extreme_behavior", "username", "chargeback"}) String reason) {
        IChatComponent banString = getBanString(s, reason);

        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
        service.schedule(() -> {
            if (!Minecraft.getMinecraft().isSingleplayer()) {
                //Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(new S00PacketDisconnect(new ChatComponentText("Runtime error")));
                Minecraft.getMinecraft().getNetHandler().getNetworkManager().closeChannel(banString);
            }
        }, MainConfiguration.disconnectDelay, TimeUnit.MILLISECONDS);
    }

    public static IChatComponent getBanString(String durationString, String reason) {
        VChatComponent banString = createDurationString(durationString);
        banString.add("\n");
        banString.add("\n" + getReason(reason, MeloMod.playerName));
        banString.setIgnoreLength(true);
        return banString.build();
    }

    private static String getRandomHexString(int numchars){
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        while(sb.length() < numchars){
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.substring(0, numchars);
    }

    private static String getRandomBanID() {
        return "#" + getRandomHexString(8).toUpperCase();
    }

    public static String getReason(String key, String username) {
        String reason = REASONS.get(key);
        reason = reason.replaceAll(":BAN_ID:", getRandomBanID());
        reason = reason.replaceAll(":USERNAME:", username);
        reason = reason.replaceAll(":WATCHDOG:", watchdogID());
        return reason;
    }

    public static String watchdogID() {
        Random r = new Random();
        int randomID = r.nextInt(99999999);
        return "[GG-" + randomID + "]";
    }

    public static final HashMap<String, String> REASONS = new HashMap<String, String>() {{
        this.put("boosting_skyblock",
                "&7Reason: &fBoosting detected on one or multiple SkyBlock profiles.&r\n"
                + "&7Find out more: &b&nhttps://www.hypixel.net/appeal&r\n\n"
                + "&7Ban ID: &f:BAN_ID:&r\n"
                + "&7Sharing your Ban ID may affect the processing of your appeal!&r"
        );
        this.put("security",
                "&7Reason: &fYour account has a security alert, please secure it and contact appeals.&r\n"
                + "&7Find out more: &b&nhttps://www.hypixel.net/appeal&r\n\n"
                + "&7Ban ID: &f:BAN_ID:&r\n"
                + "&7Sharing your Ban ID may affect the processing of your appeal!&r"
        );
        this.put("recovery_stage",
                "&7Reason: &fYour account's security appeal was processed and the account&r\n"
                + "&fhas entered a recovery phase and will be able to access the server again&r\n"
                + "&fafter 30 days. Use the time to change passwords, emails and security&r\n"
                + "&fquestions.&r\n"
                + "&7Find out more: &b&nhttps://www.hypixel.net/appeal&r"
        );
        this.put("advertising",
                "&7Reason: &fAdvertising Server&r\n"
                + "&7Find out more: &b&nhttps://www.hypixel.net/appeal&r\n\n"
                + "&7Ban ID: &f:BAN_ID:&r\n"
                + "&7Sharing your Ban ID may affect the processing of your appeal!&r"
        );
        this.put("watchdog",
                "&7Reason: &fWATCHDOG CHEAT DETECTION &o:WATCHDOG:&r\n"
                + "&7Find out more: &b&nhttps://www.hypixel.net/appeal&r\n\n"
                + "&7Ban ID: &f:BAN_ID:&r\n"
                + "&7Sharing your Ban ID may affect the processing of your appeal!&r"
        );
        this.put("inappropriate_website",
                "&7Reason: &fAdvertising inappropriate website&r\n"
                + "&7Find out more: &b&nhttps://www.hypixel.net/appeal&r\n\n"
                + "&7Ban ID: &f:BAN_ID:&r\n"
                + "&7Sharing your Ban ID may affect the processing of your appeal!&r"
        );
        this.put("cheating",
                "&7Reason: &fCheating through the use of unfair game advantages.&r\n"
                + "&7Find out more: &b&nhttps://www.hypixel.net/appeal&r\n\n"
                + "&7Ban ID: &f:BAN_ID:&r\n"
                + "&7Sharing your Ban ID may affect the processing of your appeal!&r"
        );
        this.put("inappropriate_items",
                "&7Reason: &fUsing Pets, Cosmetics, or Items in an inappropriate way.&r\n"
                + "&7Find out more: &b&nhttps://www.hypixel.net/appeal&r\n\n"
                + "&7Ban ID: &f:BAN_ID:&r\n"
                + "&7Sharing your Ban ID may affect the processing of your appeal!&r"
        );
        this.put("suspicious_activity",
                "&7Reason: &fSuspicious activity has been detected on your account.&r\n"
                + "&7Find out more: &b&nhttps://www.hypixel.net/appeal&r\n\n"
                + "&7Ban ID: &f:BAN_ID:&r\n"
                + "&7Sharing your Ban ID may affect the processing of your appeal!&r"
        );
        this.put("exploiting",
                "&7Reason: &fExploiting a bug or issue within the server and using it to your advantage.&r\n"
                + "&7Find out more: &b&nhttps://www.hypixel.net/appeal&r\n\n"
                + "&7Ban ID: &f:BAN_ID:&r\n"
                + "&7Sharing your Ban ID may affect the processing of your appeal!&r"
        );
        this.put("boosting_account",
                "&7Reason: &fBoosting your account to improve your stats.&r\n"
                + "&7Find out more: &b&nhttps://www.hypixel.net/appeal&r\n\n"
                + "&7Ban ID: &f:BAN_ID:&r\n"
                + "&7Sharing your Ban ID may affect the processing of your appeal!&r"
        );
        this.put("inappropriate_build",
                "&7Reason: &fCreating a build or drawing which is not appropriate on the server.&r\n"
                + "&7Find out more: &b&nhttps://www.hypixel.net/appeal&r\n\n"
                + "&7Ban ID: &f:BAN_ID:&r\n"
                + "&7Sharing your Ban ID may affect the processing of your appeal!&r"
        );
        this.put("extreme_behavior",
                "&7Reason: &fExtreme Negative behaviour\n"
                + "&7Find out more: &b&nhttps://www.hypixel.net/appeal&r\n\n"
                + "&7Ban ID: &f:BAN_ID:&r"
        );
        this.put("username",
                "&7Reason: &fYour username, :USERNAME:, is not allowed on the server and is breaking our rules.&r\n"
                + "&cFind out more: &bhttps://hypixel.net/rules&r\n\n"
                + "&cPlease change your Minecraft username before trying to join again.&r\n"
                + "&cIf you believe your name has been falsely blocked, contact &bhttps://www.hypixel.net/appeal&r"
        );
        this.put("chargeback",
                "&7Reason: &fChargeback: for more info and appeal, go to support.hypixel.net.\n"
                + "&7Find out more: &b&nhttps://www.hypixel.net/appeal&r\n\n"
                + "&7Ban ID: &f:BAN_ID:&r\n"
                + "&7Sharing your Ban ID may affect the processing of your appeal!&r"
        );
    }};

    public static Duration parse(String duration) {
        if (duration.equalsIgnoreCase("permanent") || duration.equalsIgnoreCase("blocked") || duration.equalsIgnoreCase("forever")) return null;
        String first = duration.substring(0, duration.length() - 1);
        String last = duration.substring(duration.length() - 1);
        if (!first.matches("\\d+?") || !last.matches("[dmyMsh]")) {
            return Duration.ZERO;
        }
        int num = Integer.parseInt(first);
        switch (last.charAt(0)) {
            case 'h':
                return Duration.ofHours(num);
            case 'd':
                return Duration.ofDays(num);
            case 'm':
                return Duration.ofMinutes(num);
            case 's':
                return Duration.ofSeconds(num);
            case 'M':
                return Duration.ofDays(num * 30L);
            case 'y':
                return Duration.ofDays(num * 365L);
            default:
                return Duration.ZERO;
        }
    }

    public static VChatComponent createDurationString(String duration) {
        Duration parsedDuration = parse(duration);
        String pretty = parsedDuration == null ? null : Utils.prettyTime(parsedDuration.toMillis());
        VChatComponent chatComponent = new VChatComponent(MeloMod.MessageScheme.RAW);
        if (pretty == null) {
            if (duration.equalsIgnoreCase("blocked")) {
                chatComponent.add("&cYou are currently blocked from joining this server!");
            } else {
                chatComponent.add("&cYou are permanently banned from this server!");
            }
        } else {
            chatComponent.add("&cYou are temporarily banned for &f" + pretty + " &cfrom this server!");
        }
        return chatComponent;
    }
}
