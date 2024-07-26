package me.vlink102.melomod.events;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.ChatReceiveEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import cc.polyfrost.oneconfig.utils.hypixel.LocrawInfo;
import cc.polyfrost.oneconfig.utils.hypixel.LocrawUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.util.internal.StringUtil;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.config.ChatConfig;
import me.vlink102.melomod.mixin.SkyblockUtil;
import me.vlink102.melomod.util.StringUtils;
import me.vlink102.melomod.util.Utils;
import me.vlink102.melomod.util.math.DoubleEvaluator;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.Sys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatEvent {


    public static String getChessMove(String fen) {
        try {
            URL url = new URL("https://chess-api.com/v1");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            String jsonInputString = "{\"fen\": \"" + fen + "\", \"depth\": 10}";
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                JsonObject object = new JsonParser().parse(response.toString()).getAsJsonObject();
                return object.get("text").getAsString();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private final MeloMod mod;

    public ChatEvent(MeloMod mod) {
        this.mod = mod;
        EventManager.INSTANCE.register(this);
    }

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    public enum SeaCreature {
        SQUID("pond_squid", "pond_squid_1"),
        AGARIMOO("agarimoo_35"),
        SEA_WALKER("sea_walker_4"),
        NIGHT_SQUID("night_squid_6"),
        SEA_GUARDIAN("sea_guardian_10"),
        SEA_WITCH("sea_witch_15"),
        SEA_ARCHER("sea_archer_15"),
        RIDER_OF_THE_DEEP("zombie_deep_20", "chicken_deep_20"),
        CATFISH("catfish_23"),
        CARROT_KING("carrot_king_25"),
        SEA_LEECH("sea_leech_30"),
        GUARDIAN_DEFENDER("guardian_defender_45"),
        DEEP_SEA_PROTECTOR("deep_sea_protector_60"),
        WATER_HYDRA("water_hydra_100"),
        THE_SEA_EMPEROR("skeleton_emperor_150", "guardian_emperor_150"),
        OASIS_RABBIT("oasis_rabbit_10"),
        OASIS_SHEEP("oasis_sheep_10"),
        WATER_WORM("water_worm_20"),
        POISONED_WATER_WORM("poisoned_water_worm_25"),
        ABYSSAL_MINER("zombie_miner_150"),
        SCARECROW("scarecrow_9"),
        NIGHTMARE("nightmare_24"),
        WEREWOLF("werewolf_50"),
        PHANTOM_FISHER("phantom_fisherman_160"),
        GRIM_REAPER("grim_reaper_190"),
        FROZEN_STEVE("frozen_steve_7"),
        FROSTY("frosty_the_snowman_13"),
        GRINCH("grinch_21"),
        YETI("yeti_175"),
        NUTCRACKER("nutcracker_50"),
        REINDRAKE("reindrake_100"),
        NURSE_SHARK("nurse_shark_6"),
        BLUE_SHARK("blue_shark_20"),
        TIGER_SHARK("tiger_shark_50"),
        GREAT_WHITE_SHARK("great_white_shark_180"),
        MAGMA_SLUG("magma_slug_200"),
        MOOGMA("moogma_210"),
        LAVA_LEECH("lava_leech_220"),
        PYROCLASTIC_WORM("pyroclastic_worm_240"),
        LAVA_FLAME("lava_flame_230"),
        FIRE_EEL("fire_eel_240"),
        TAURUS("pig_rider_250"),
        PLHLEGBLAST("pond_squid_300"),
        THUNDER("thunder_400"),
        LORD_JAWBUS("lord_jawbus_600"),
        FLAMING_WORM("flaming_worm_50"),
        LAVA_BLAZE("lava_blaze_100"),
        LAVA_PIGMAN("lava_pigman_100"),
        NOTHING();

        private final String[] internalID;

        SeaCreature(String... internalID) {
            this.internalID = internalID;
        }

        public String[] getInternalID() {
            return internalID;
        }

        public static SeaCreature convertBestiaryMob(String bestiaryString) {
            for (SeaCreature value : SeaCreature.values()) {
                if (Arrays.asList(value.internalID).contains(bestiaryString)) {
                    return value;
                }
            }
            return NOTHING;
        }
    }

    public static final HashMap<SeaCreature, Integer> seaCreatureSession = new HashMap<>();
    public static final HashMap<SeaCreature, Integer> lastCaught = new HashMap<>();
    public static final HashMap<SeaCreature, Long> lastCaughtTimeStamp = new HashMap<>();


    private static final HashMap<String, SeaCreature> mapping = new HashMap<String, SeaCreature>() {{
        this.put("A Squid appeared.", SeaCreature.SQUID);
        this.put("Your Chumcap Bucket trembles, it's an Agarimoo.", SeaCreature.AGARIMOO);
        this.put("You caught a Sea Walker.", SeaCreature.SEA_WALKER);
        this.put("Pitch darkness reveals a Night Squid.", SeaCreature.NIGHT_SQUID);
        this.put("You stumbled upon a Sea Guardian.", SeaCreature.SEA_GUARDIAN);
        this.put("It looks like you've disrupted the Sea Witch's brewing session. Watch out, she's furious!", SeaCreature.SEA_WITCH);
        this.put("You reeled in a Sea Archer.", SeaCreature.SEA_ARCHER);
        this.put("The Rider of the Deep has emerged.", SeaCreature.RIDER_OF_THE_DEEP);
        this.put("Huh? A Catfish!", SeaCreature.CATFISH);
        this.put("Is this even a fish? It's the Carrot King!", SeaCreature.CARROT_KING);
        this.put("Gross! A Sea Leech!", SeaCreature.SEA_LEECH);
        this.put("You've discovered a Guardian Defender of the sea.", SeaCreature.GUARDIAN_DEFENDER);
        this.put("You have awoken the Deep Sea Protector, prepare for a battle!", SeaCreature.DEEP_SEA_PROTECTOR);
        this.put("The Water Hydra has come to test your strength.", SeaCreature.WATER_HYDRA);
        this.put("The Sea Emperor arises from the depths.", SeaCreature.THE_SEA_EMPEROR);
        this.put("An Oasis Rabbit appears from the water.", SeaCreature.OASIS_RABBIT);
        this.put("An Oasis Sheep appears from the water.", SeaCreature.OASIS_SHEEP);
        this.put("A Water Worm surfaces!", SeaCreature.WATER_WORM);
        this.put("A Poisoned Water Worm surfaces!", SeaCreature.POISONED_WATER_WORM);
        this.put("An Abyssal Miner breaks out of the water!", SeaCreature.ABYSSAL_MINER);
        this.put("Phew! It's only a Scarecrow.", SeaCreature.SCARECROW);
        this.put("You hear trotting from beneath the waves, you caught a Nightmare.", SeaCreature.NIGHTMARE);
        this.put("It must be a full moon, a Werewolf appears.", SeaCreature.WEREWOLF);
        this.put("The spirit of a long lost Phantom Fisher has come to haunt you.", SeaCreature.PHANTOM_FISHER);
        this.put("This can't be! The manifestation of death himself!", SeaCreature.GRIM_REAPER);
        this.put("Frozen Steve fell into the pond long ago, never to resurface...until now!", SeaCreature.FROZEN_STEVE);
        this.put("It's a snowman! He looks harmless.", SeaCreature.FROSTY);
        this.put("The Grinch stole Jerry's Gifts...get them back!", SeaCreature.GRINCH);
        this.put("What is this creature!?", SeaCreature.YETI);
        this.put("You found a forgotten Nutcracker laying beneath the ice.", SeaCreature.NUTCRACKER);
        this.put("A Reindrake forms from the depths.", SeaCreature.REINDRAKE);
        this.put("A tiny fin emerges from the water, you've caught a Nurse Shark.", SeaCreature.NURSE_SHARK);
        this.put("You spot a fin as blue as the water it came from, it's a Blue Shark.", SeaCreature.BLUE_SHARK);
        this.put("A striped beast bounds from the depths, the wild Tiger Shark!", SeaCreature.TIGER_SHARK);
        this.put("Hide no longer, a Great White Shark has tracked your scent and thirsts for your blood!", SeaCreature.GREAT_WHITE_SHARK);
        this.put("From beneath the lava appears a Magma Slug.", SeaCreature.MAGMA_SLUG);
        this.put("You hear a faint Moo from the lava... A Moogma appears.", SeaCreature.MOOGMA);
        this.put("A small but fearsome Lava Leech emerges.", SeaCreature.LAVA_LEECH);
        this.put("You feel the heat radiating as a Pyroclastic Worm surfaces.", SeaCreature.PYROCLASTIC_WORM);
        this.put("A Lava Flame flies out from beneath the lava.", SeaCreature.LAVA_FLAME);
        this.put("A Fire Eel slithers out from the depths.", SeaCreature.FIRE_EEL);
        this.put("Taurus and his steed emerge.", SeaCreature.TAURUS);
        this.put("WOAH! A Plhlegblast appeared.", SeaCreature.PLHLEGBLAST);
        this.put("You hear a massive rumble as Thunder emerges.", SeaCreature.THUNDER);
        this.put("You have angered a legendary creature... Lord Jawbus has arrived.", SeaCreature.LORD_JAWBUS);
        this.put("A Flaming Worm surfaces from the depths!", SeaCreature.FLAMING_WORM);
        this.put("A Lava Blaze has surfaced from the depths!", SeaCreature.LAVA_BLAZE);
        this.put("A Lava Pigman arose from the depths!", SeaCreature.LAVA_PIGMAN);
    }};

    public static SeaCreature getSeaCreature(String string) {
        if (!mapping.containsKey(string)) return SeaCreature.NOTHING;
        return mapping.get(string);
    }

    private static String prettyTime(long millis) {
        return Utils.prettyTime(millis);
    }

    public static void updateLastCaught(SeaCreature caught, boolean doubleHook) {
        seaCreatureSession.put(caught, seaCreatureSession.getOrDefault(caught, 0) + (doubleHook ? 2 : 1));
        if (!lastCaught.containsKey(caught) && !lastCaughtTimeStamp.containsKey(caught)) {
            if (ChatConfig.fishingChat) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/pc " + (doubleHook ? "‹⚓› ‼ DOUBLE HOOK ‼ " : "‹☂› ") + "☆ Caught a " + WordUtils.capitalizeFully(caught.toString().replaceAll("_", " ")) + "! ◆ First Catch in Session! ☆");
            }
            lastCaught.put(caught, 0);
            lastCaughtTimeStamp.put(caught, System.currentTimeMillis());
        } else {
            if (ChatConfig.fishingChat) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/pc " + (doubleHook ? "‹⚓› ‼ DOUBLE HOOK ‼ " : "‹☂› ") + "☆ Caught a " + WordUtils.capitalizeFully(caught.toString().replaceAll("_", " ")) + "! ◆ Total: " + seaCreatureSession.get(caught) + " ◆ Since Last: " + lastCaught.get(caught) + " ◆ Last Caught: " + prettyTime(System.currentTimeMillis() - lastCaughtTimeStamp.get(caught)) + " ago. ☆");
            }
            lastCaught.put(caught, 0);
            lastCaughtTimeStamp.put(caught, System.currentTimeMillis());
        }
        for (SeaCreature value : SeaCreature.values()) {
            if (value == caught) continue;
            if (lastCaught.containsKey(value)) {
                lastCaught.put(value, lastCaught.get(value) + (doubleHook ? 2 : 1));
            }
        }
    }


    private static boolean doubleHook = false;

    private static void updateDoubleHook(String string) {
        if (string.equalsIgnoreCase("Double Hook!") || string.equalsIgnoreCase("It's a Double Hook!") || string.equalsIgnoreCase("It's a Double Hook! Woot woot!")) {
            doubleHook = true;
        }
    }

    @Subscribe
    public void onChatEvent(ChatReceiveEvent event) {
        IChatComponent message = event.message;
        String unformatted = message.getUnformattedText();
        String stripped = StringUtils.cleanColour(unformatted);

        updateDoubleHook(stripped);
        SeaCreature creature = getSeaCreature(stripped);
        if (creature != SeaCreature.NOTHING) {
            updateLastCaught(creature, doubleHook);
            doubleHook = false;
            return;
        }

        if (stripped.startsWith("Party > ")) {
            String chatMessage = stripped.split(": ")[1];
            if (chatMessage.startsWith("-chess ")) {
                String[] args = chatMessage.split("-chess ");
                if (args.length > 1) {
                    String fen = args[1];
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/pc »»» " + getChessMove(fen) + " «««");
                    return;
                }
            }

            if (chatMessage.startsWith("-eval ")) {
                String[] args = chatMessage.split("-eval ");
                if (args.length > 1) {
                    String expression = args[1];
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/pc ⚡ Evaluated: ≈" + new DoubleEvaluator().evaluate(expression).toString());
                    return;
                }
            }
            String regex = "Party > (\\[.*?] )?(.*?):";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(stripped);
            String playerName = "?";
            if (matcher.find()) {
                playerName = matcher.group(2);
            }
            if (ChatConfig.gay) {
                if (chatMessage.equalsIgnoreCase("-gay")) {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/pc »»» " + playerName + " is " + random.nextInt(0, 101) + "% gay. «««");
                }
                if (ChatConfig.runOthers) {
                    if (chatMessage.startsWith("-gay ")) {
                        String[] args = chatMessage.split(" ");
                        if (args.length > 1) {
                            String gay = args[1];
                            Minecraft.getMinecraft().thePlayer.sendChatMessage("/pc »»» " + gay + " is " + random.nextInt(0, 101) + "% gay. «««");
                        }
                    }
                }
            }

            if (ChatConfig.racist) {
                if (chatMessage.equalsIgnoreCase("-racist")) {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/pc »»» " + playerName + " is " + random.nextInt(0, 101) + "% racist. «««");
                }
                if (ChatConfig.runOthers) {
                    if (chatMessage.startsWith("-racist ")) {
                        String[] args = chatMessage.split(" ");
                        if (args.length > 1) {
                            String racist = args[1];
                            Minecraft.getMinecraft().thePlayer.sendChatMessage("/pc »»» " + racist + " is " + random.nextInt(0, 101) + "% racist. «««");
                        }
                    }
                }
            }

            if (ChatConfig.pray) {
                if (chatMessage.equalsIgnoreCase("-pray")) {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/pc »»» " + playerName + " prayed to RNGesus! (+" + random.nextInt(0, 501) + "% ✯ Magic Find) «««");
                }
            }
            if (ChatConfig.diceRoll) {
                if (chatMessage.equalsIgnoreCase("-dice")) {
                    int roll = random.nextInt(1, 7);
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/pc »»» " + playerName + " rolled a " + roll + "! " + getDice(roll) + " «««");
                }
            }
            if (ChatConfig.doubleDiceRoll) {
                if (chatMessage.equalsIgnoreCase("-dice2")) {
                    int roll = random.nextInt(1, 7);
                    int roll2 = random.nextInt(1, 7);
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/pc »»» " + playerName + " rolled a " + roll + " and a " + roll2 + "! Total: " + (roll+roll2) + "! " + getDice(roll) + getDice(roll2) + " «««");
                }
            }

            if (ChatConfig.guild) {
                if (chatMessage.equalsIgnoreCase("-guild")) {
                    SkyblockUtil.Guild guild = new SkyblockUtil.Guild(SkyblockUtil.getGuild(playerName));
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/pc ✿ Guild: [" + guild.getTag() + "] " + guild.getName() + " (" + guild.getGuildID() + ") ✿");
                }
                if (ChatConfig.runOthers) {
                    if (chatMessage.startsWith("-guild ")) {
                        String[] args = chatMessage.split(" ");
                        if (args.length > 1) {
                            String player = args[1];
                            SkyblockUtil.Guild guild = new SkyblockUtil.Guild(SkyblockUtil.getGuild(player));
                            Minecraft.getMinecraft().thePlayer.sendChatMessage("/pc ✿ «" + player + "» Guild: [" + guild.getTag() + "] " + guild.getName() + " (" + guild.getGuildID() + ") ✿");
                        }
                    }
                }
            }

            if (ChatConfig.locate) {
                if (chatMessage.equalsIgnoreCase("-locate")) {
                    SkyblockUtil.Location info = InternalLocraw.getLocation();
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/pc ◇ Server: " + InternalLocraw.getServerID() + " ⚑ Island: " + WordUtils.capitalizeFully(info.toString().replaceAll("_", " ")) + " ◇");
                }
                if (ChatConfig.runOthers) {
                    if (chatMessage.startsWith("-locate ")) {
                        String[] args = chatMessage.split("-locate ");
                        if (args.length > 1) {
                            String playerToLocate = args[1];
                            if (playerToLocate.contains(" ")) return;
                            JsonObject session = SkyblockUtil.getSession(playerToLocate);
                            if (!session.get("online").getAsBoolean()) {
                                Minecraft.getMinecraft().thePlayer.sendChatMessage("/pc ◇ " + playerName + " is not currently online! ◇");
                            } else {
                                boolean onCurrent = false;
                                for (EntityPlayer playerEntity : Minecraft.getMinecraft().theWorld.playerEntities) {
                                    if (playerEntity.getName().equals(playerToLocate)) {
                                        onCurrent = true;
                                        break;
                                    }
                                }

                                Minecraft.getMinecraft().thePlayer.sendChatMessage("/pc ◇ «" + playerToLocate + "» Server: " + (onCurrent ? InternalLocraw.getServerID() : "Unknown") + " ◇ Game: " + WordUtils.capitalizeFully(session.get("gameType").getAsString().replaceAll("_", " ")) + " ⚑ Mode: " + WordUtils.capitalizeFully(session.get("mode").getAsString().replaceAll("_", " ")) + " ◇");
                            }
                        }
                    }
                }
            }
        }
    }


    private static final char[] dice = {'⚀', '⚁', '⚂', '⚃', '⚄', '⚅'};
    public static String getDice(int roll) {
        return String.valueOf(dice[roll - 1]);
    }
}
