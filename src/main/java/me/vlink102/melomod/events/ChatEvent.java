package me.vlink102.melomod.events;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.ChatReceiveEvent;
import cc.polyfrost.oneconfig.events.event.ChatSendEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import com.google.gson.*;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.config.ChatConfig;
import me.vlink102.melomod.chatcooldownmanager.ServerTracker;
import me.vlink102.melomod.chatcooldownmanager.TickHandler;
import me.vlink102.melomod.util.StringUtils;
import me.vlink102.melomod.util.game.Utils;
import me.vlink102.melomod.util.math.eval.DoubleEvaluator;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.vlink102.melomod.util.http.ApiUtil.sendLaterParty;
import static me.vlink102.melomod.util.StringUtils.paginateHelp;

public class ChatEvent {

    public synchronized void getChessMove(String fen) {
        JsonObject body = new JsonObject();
        body.addProperty("fen", fen);
        body.addProperty("depth", 10);
        mod.apiUtil.requestServer("https://chess-api.com/v1", body, object -> {
            if (object.isJsonObject()) {
                TickHandler.addToQueue("/pc »»» " + object.getAsJsonObject().get("text").getAsString() + " «««");
            }
        });
    }

    public List<String> party = new ArrayList<>();

    /*
    public CompletableFuture<Void> getChessMove(String fen) {
        return CompletableFuture.runAsync(() -> {
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

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


     */
    private final MeloMod mod;

    private final RandomUtils random;

    public ChatEvent(MeloMod mod) {
        this.mod = mod;
        this.random = new RandomUtils();
        EventManager.INSTANCE.register(this);
    }

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

    public void updateLastCaught(SeaCreature caught, boolean doubleHook) {
        seaCreatureSession.put(caught, seaCreatureSession.getOrDefault(caught, 0) + (doubleHook ? 2 : 1));
        if (!lastCaught.containsKey(caught) && !lastCaughtTimeStamp.containsKey(caught)) {
            if (ChatConfig.fishingChat) {
                sendLaterParty("/pc " + (doubleHook ? "‹⚓› ‼ DOUBLE HOOK ‼ " : "‹☂› ") + "☆ Caught a " + WordUtils.capitalizeFully(caught.toString().replaceAll("_", " ")) + "! ◆ First Catch in Session! ☆");
            }
        } else {
            if (ChatConfig.fishingChat) {
                sendLaterParty("/pc " + (doubleHook ? "‹⚓› ‼ DOUBLE HOOK ‼ " : "‹☂› ") + "☆ Caught a " + WordUtils.capitalizeFully(caught.toString().replaceAll("_", " ")) + "! ◆ Total: " + seaCreatureSession.get(caught) + " ◆ Since Last: " + lastCaught.get(caught) + " ◆ Last Caught: " + prettyTime(System.currentTimeMillis() - lastCaughtTimeStamp.get(caught)) + " ago. ☆");
            }
        }
        lastCaught.put(caught, 0);
        lastCaughtTimeStamp.put(caught, System.currentTimeMillis());
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

    public enum Commands {
        GAY("gay", ChatConfig::isGay, optional("player", true)),
        RACIST("racist", ChatConfig::isRacist, optional("player", true)),
        DICEROLL("dice", ChatConfig::isDiceRoll, optional("amount", false)),
        PRAY("pray", ChatConfig::isPray),
        CHESSENGINE("chess", ChatConfig::isChessEngine, required("fen", false)),
        MATHEVALUATION("eval", ChatConfig::isMathEvaluation, required("expression", false)),
        GUILD("guild", ChatConfig::isGuild, optional("player", true)),
        LOCATE("locate", ChatConfig::isLocate, optional("player", true)),
        COINFLIP("coinflip", ChatConfig::isCoinFlip),
        STALK("stalk", ChatConfig::isStalk, required("player", true)),
        DISCORD("dc", ChatConfig::isSocialMedia, optional("player", true)),
        YOUTUBE("youtube", ChatConfig::isSocialMedia, optional("player", true)),
        TWITTER("twitter", ChatConfig::isSocialMedia, optional("player", true)),
        TWITCH("twitch", ChatConfig::isSocialMedia, optional("player", true)),
        TIKTOK("tiktok", ChatConfig::isSocialMedia, optional("player", true)),
        FORUMS("forums", ChatConfig::isSocialMedia, optional("player", true)),
        INSTAGRAM("instagram", ChatConfig::isSocialMedia, optional("player", true)),
        HELP("help", () -> true, optional("page", false)),
        SECRET("secrets", ChatConfig::isSecret, optional("player", true)),
        NPASS("npass", ChatConfig::isnWordPass, optional("player", true)),
        FEMBOY("femboy", ChatConfig::isFemboy, optional("player", true)),
        NETWORTH("networth", ChatConfig::isNetworth, optional("player", true)),
        HISTORY("history", ChatConfig::isUserNameHistory, optional("player", true), optional("page", false)),
        LASTONLINE("seen", ChatConfig::isLastOnlineInfo, optional("player", true)),
        AI("ai", ChatConfig::isAi, required("prompt", false));

        private final String command;
        private final List<CommandParameter> params;
        private final Supplier<Boolean> toggle;

        Commands(String command, Supplier<Boolean> toggle, CommandParameter... params) {
            this.command = command;
            this.toggle = toggle;
            this.params = Arrays.asList(params);
        }

        public boolean getToggle() {
            return toggle.get();
        }



        public static class CommandParameter {
            private final String paramName;
            private final boolean required;
            private final boolean isOther;

            public CommandParameter(String paramName, boolean required, boolean isOther) {
                this.paramName = paramName;
                this.required = required;
                this.isOther = isOther;
            }

            public boolean isOther() {
                return isOther;
            }

            public String getParamName() {
                return paramName;
            }

            public boolean isRequired() {
                return required;
            }
        }

        public static CommandParameter required(String paramName, boolean isOther) {
            return new CommandParameter(paramName, true, isOther);
        }

        public static CommandParameter optional(String paramName, boolean isOther) {
            return new CommandParameter(paramName, false, isOther);
        }

        public List<CommandParameter> getParams() {
            return params;
        }

        public String getCommand() {
            return command;
        }

        public String getString() {
            StringJoiner joiner = new StringJoiner(" ");
            joiner.add(ChatConfig.chatPrefix + getCommand());
            for (CommandParameter param : params) {
                if (param.isOther && !ChatConfig.runOthers) {
                    continue;
                }
                if (param.required) {
                    joiner.add("<" + param.getParamName() + ">");
                } else {
                    joiner.add("(" + param.getParamName() + ")");
                }
            }
            return (joiner.toString());
        }

        public int getLength() {
            return getString().length();
        }
    }

    public static final HashMap<String, String> commands = new HashMap<>();

    private int memberCount = -1;

    private static final HashMap<String, Integer[]> hidden = new HashMap<String, Integer[]>() {{
        this.put("ZenmosM", new Integer[] {});
    }};

    @Subscribe
    public void onChatSend(ChatSendEvent event) {
        if (event.message.startsWith("/")) {
            TickHandler.ticksSinceLastCommand = 0;
        } else {
            TickHandler.ticksSinceLastChat = 0;
        }
    }

    @Subscribe
    public void onChatEvent(ChatReceiveEvent event) {
        String fullMessage = event.message.getUnformattedText();
        if (ServerTracker.testingRank) {
            if (fullMessage.contains("You must be vip or higher to use this command!")) {
                ServerTracker.testingRank = false;
                ServerTracker.hasChatCooldown = true;
                return;
            }
        }
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

        if (stripped.startsWith("Party Members (")) {
            memberCount = Integer.parseInt(String.valueOf(stripped.split("\\(")[1].charAt(0)));
        }

        /*
        if ((stripped.startsWith("Party Leader: ") || stripped.startsWith("Party Moderators: ") || stripped.startsWith("Party Members: "))) {
            String names = "\\s([a-zA-Z0-9_]*?)\\s●";
            Pattern regex = Pattern.compile(names);
            Matcher matcher = regex.matcher(stripped);

            while (matcher.find()) {
                String match = matcher.group(1);
                if (!party.contains(match)) {
                    party.add(match);
                    memberCount--;
                }
            }

        }
        if (stripped.equalsIgnoreCase("-----------------------------------------------------") && memberCount == 0) {
            sayAllPlayerSecrets();
            party.clear();
            memberCount = -1;
        }

         */

        if (stripped.startsWith("Party > ")) {
            String chatMessage = stripped.split("Party\\s>.*?:\\s")[1];
            if (chatMessage.equalsIgnoreCase(ChatConfig.chatPrefix + Commands.HELP.getCommand())) {
                sendLaterParty("/pc " + paginateHelp().get(0));
            }
            if (chatMessage.startsWith(ChatConfig.chatPrefix + "help ")) {
                String[] args = chatMessage.split("\\" + ChatConfig.chatPrefix + Commands.HELP.getCommand() + " ");
                if (args.length == 2) {
                    String length = args[1];
                    if (length.matches("\\d+?")) {
                        List<String> paginated = paginateHelp();
                        int lengthInt = Integer.parseInt(length);
                        if (lengthInt > paginated.size()) {
                            lengthInt = paginated.size();
                        }
                        if (lengthInt < 1) {
                            lengthInt = 1;
                        }
                        sendLaterParty("/pc " + paginated.get(lengthInt - 1));
                    }
                }
            }



            if (ChatConfig.wholePartySecret) {
                if (chatMessage.equalsIgnoreCase(ChatConfig.chatPrefix + Commands.SECRET)) {
                    sendLaterParty("/party list");
                }
            }
            if (ChatConfig.chessEngine) {
                if (chatMessage.startsWith(ChatConfig.chatPrefix + Commands.CHESSENGINE.getCommand() + " ")) {
                    String[] args = chatMessage.split("\\" + ChatConfig.chatPrefix + Commands.CHESSENGINE.getCommand() + " ");
                    if (args.length > 1) {
                        String fen = args[1];
                        getChessMove(fen);
                    }
                }
            }

            if (ChatConfig.stalk) {
                if (chatMessage.startsWith(ChatConfig.chatPrefix + Commands.STALK.getCommand() + " ")) {
                    String[] args = chatMessage.split("\\" + ChatConfig.chatPrefix + Commands.STALK.getCommand() + " ");
                    if (args.length > 1) {
                        String player = args[1];
                        mod.apiUtil.lastLogin(player);
                    }
                }
            }
            if (ChatConfig.mathEvaluation) {
                if (chatMessage.startsWith(ChatConfig.chatPrefix + Commands.MATHEVALUATION.getCommand() + " ")) {
                    String[] args = chatMessage.split("\\" + ChatConfig.chatPrefix + Commands.MATHEVALUATION.getCommand() + " ");
                    if (args.length > 1) {
                        String expression = args[1];
                        sendLaterParty("/pc ⚡ Evaluated: ≈" + new DoubleEvaluator().evaluate(expression).toString());
                    }
                }
            }

            String regex = "Party > (\\[.*?] )?(.*?):";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(stripped);
            String playerName;
            if (matcher.find()) {
                playerName = matcher.group(2);
            } else {
                playerName = "?";
            }
            if (ChatConfig.userNameHistory) {
                if (chatMessage.equalsIgnoreCase(ChatConfig.chatPrefix + Commands.HISTORY.getCommand())) {
                    mod.apiUtil.getPlayerPastNames(playerName, 1);
                }
                if (ChatConfig.runOthers) {
                    if (chatMessage.startsWith(ChatConfig.chatPrefix + Commands.HISTORY.getCommand() + " ")) {
                        String[] args = chatMessage.split(" ");
                        if (args.length == 2) {
                            String player = args[1];
                            if (args[1].matches("\\d+?")) {
                                int page = Integer.parseInt(args[1]);
                                mod.apiUtil.getPlayerPastNames(player, page);
                            } else {
                                mod.apiUtil.getPlayerPastNames(player, 1);
                            }
                        }
                        if (args.length == 3) {
                            String player = args[1];
                            if (args[2].matches("\\d+?")) {
                                int page = Integer.parseInt(args[2]);
                                mod.apiUtil.getPlayerPastNames(player, page);
                            }
                        }
                    }
                }
            }
            if (ChatConfig.secret) {
                if (chatMessage.equalsIgnoreCase(ChatConfig.chatPrefix + Commands.SECRET.getCommand())) {
                    mod.apiUtil.sayPlayerSecrets(playerName);
                }
                if (ChatConfig.runOthers) {
                    if (chatMessage.startsWith(ChatConfig.chatPrefix + Commands.SECRET.getCommand() + " ")) {
                        String[] args = chatMessage.split("\\" + ChatConfig.chatPrefix + Commands.SECRET.getCommand() + " ");
                        if (args.length == 2) {
                            String player = args[1];
                            mod.apiUtil.sayPlayerSecrets(player);
                        }
                    }
                }
            }
            if (ChatConfig.networth) {
                if (chatMessage.equalsIgnoreCase(ChatConfig.chatPrefix + Commands.NETWORTH)) {
                    mod.apiUtil.sayPlayerNetworth(playerName);
                }
                if (ChatConfig.runOthers) {
                    if (chatMessage.startsWith(ChatConfig.chatPrefix + Commands.NETWORTH.getCommand() + " ")) {
                        String[] args = chatMessage.split("\\" + ChatConfig.chatPrefix + Commands.NETWORTH.getCommand() + " ");
                        if (args.length == 2) {
                            String check = args[1];
                            mod.apiUtil.sayPlayerNetworth(check);
                        }
                    }
                }
            }
            if (ChatConfig.coinFlip) {
                if (chatMessage.equalsIgnoreCase(ChatConfig.chatPrefix + Commands.COINFLIP.getCommand())) {
                    sendLaterParty("/pc »»» " + playerName + " flipped a coin! It's " + (RandomUtils.nextInt(0, 2) == 0 ? "Heads!" : "Tails!") + " «««");
                }
            }
            if (ChatConfig.socialMedia) {
                if (chatMessage.equalsIgnoreCase(ChatConfig.chatPrefix + Commands.DISCORD.getCommand())) {
                    mod.apiUtil.playerSocials(playerName, "dc");
                }
                if (ChatConfig.runOthers) {
                    if (chatMessage.startsWith(ChatConfig.chatPrefix + "dc ")) {
                        String[] args = chatMessage.split("\\" + ChatConfig.chatPrefix + Commands.DISCORD.getCommand() + " ");
                        if (args.length > 1) {
                            String player = args[1];
                            mod.apiUtil.playerSocials(player, "dc");
                            return;
                        }
                    }
                }
                if (chatMessage.equalsIgnoreCase(ChatConfig.chatPrefix + Commands.TWITTER.getCommand())) {
                    mod.apiUtil.playerSocials(playerName, "twitter");
                }
                if (ChatConfig.runOthers) {
                    if (chatMessage.startsWith(ChatConfig.chatPrefix + Commands.TWITTER.getCommand() + " ")) {
                        String[] args = chatMessage.split("\\" + ChatConfig.chatPrefix + Commands.TWITTER.getCommand() + " ");
                        if (args.length > 1) {
                            String player = args[1];
                            mod.apiUtil.playerSocials(player, "twitter");
                            return;
                        }
                    }
                }
                if (chatMessage.equalsIgnoreCase(ChatConfig.chatPrefix + Commands.TWITCH.getCommand())) {
                    mod.apiUtil.playerSocials(playerName, "twitch");
                }
                if (ChatConfig.runOthers) {
                    if (chatMessage.startsWith(ChatConfig.chatPrefix + Commands.TWITCH.getCommand() + " ")) {
                        String[] args = chatMessage.split("\\" + ChatConfig.chatPrefix + Commands.TWITCH.getCommand() + " ");
                        if (args.length > 1) {
                            String player = args[1];
                            mod.apiUtil.playerSocials(player, "twitch");
                            return;
                        }
                    }
                }
                if (chatMessage.equalsIgnoreCase(ChatConfig.chatPrefix + Commands.INSTAGRAM.getCommand())) {
                    mod.apiUtil.playerSocials(playerName, "instagram");
                }
                if (ChatConfig.runOthers) {
                    if (chatMessage.startsWith(ChatConfig.chatPrefix + Commands.INSTAGRAM.getCommand() + " ")) {
                        String[] args = chatMessage.split("\\" + ChatConfig.chatPrefix + Commands.INSTAGRAM.getCommand() + " ");
                        if (args.length > 1) {
                            String player = args[1];
                            mod.apiUtil.playerSocials(player, "instagram");
                            return;
                        }
                    }
                }
                if (chatMessage.equalsIgnoreCase(ChatConfig.chatPrefix + Commands.YOUTUBE.getCommand())) {
                    mod.apiUtil.playerSocials(playerName, "youtube");
                }
                if (ChatConfig.runOthers) {
                    if (chatMessage.startsWith(ChatConfig.chatPrefix + Commands.YOUTUBE.getCommand() + " ")) {
                        String[] args = chatMessage.split("\\" + ChatConfig.chatPrefix + Commands.YOUTUBE.getCommand() + " ");
                        if (args.length > 1) {
                            String player = args[1];
                            mod.apiUtil.playerSocials(player, "youtube");
                            return;
                        }
                    }
                }
                if (chatMessage.equalsIgnoreCase(ChatConfig.chatPrefix + Commands.TIKTOK.getCommand())) {
                    mod.apiUtil.playerSocials(playerName, "tiktok");
                }
                if (ChatConfig.runOthers) {
                    if (chatMessage.startsWith(ChatConfig.chatPrefix + Commands.TIKTOK.getCommand() + " ")) {
                        String[] args = chatMessage.split("\\" + ChatConfig.chatPrefix + Commands.TIKTOK.getCommand() + " ");
                        if (args.length > 1) {
                            String player = args[1];
                            mod.apiUtil.playerSocials(player, "tiktok");
                            return;
                        }
                    }
                }
                if (chatMessage.equalsIgnoreCase(ChatConfig.chatPrefix + Commands.FORUMS.getCommand())) {
                    mod.apiUtil.playerSocials(playerName, "forums");
                }
                if (ChatConfig.runOthers) {
                    if (chatMessage.startsWith(ChatConfig.chatPrefix + Commands.FORUMS.getCommand() + " ")) {
                        String[] args = chatMessage.split("\\" + ChatConfig.chatPrefix + Commands.FORUMS.getCommand() + " ");
                        if (args.length > 1) {
                            String player = args[1];
                            mod.apiUtil.playerSocials(player, "forums");
                            return;
                        }
                    }
                }
            }
            if (ChatConfig.gay) {
                if (chatMessage.equalsIgnoreCase(ChatConfig.chatPrefix + Commands.GAY.getCommand())) {
                    int randomNum = RandomUtils.nextInt(0, 101);
                    if (playerName.equalsIgnoreCase("kalabash")) randomNum = 100;
                    sendLaterParty("/pc »»» " + playerName + " is " + randomNum + "% gay. «««");
                    return;
                }
                if (ChatConfig.runOthers) {
                    if (chatMessage.startsWith(ChatConfig.chatPrefix + Commands.GAY.getCommand() + " ")) {
                        String[] args = chatMessage.split(" ");
                        if (args.length > 1) {
                            String gay = args[1];
                            int randomNum = RandomUtils.nextInt(0, 101);
                            if (gay.equalsIgnoreCase("kalabash")) randomNum = 100;
                            sendLaterParty("/pc »»» " + gay + " is " + randomNum + "% gay. «««");
                            return;
                        }
                    }
                }
            }
            if (ChatConfig.nWordPass) {
                if (chatMessage.equalsIgnoreCase(ChatConfig.chatPrefix + Commands.NPASS.getCommand())) {
                    sendLaterParty("/pc »»» " + playerName + " was granted the N pass. «««");
                }
            }
            if (ChatConfig.femboy) {
                if (chatMessage.equalsIgnoreCase(ChatConfig.chatPrefix + Commands.FEMBOY.getCommand())) {
                    sendLaterParty("/pc »»» " + playerName + " is " + RandomUtils.nextInt(0, 101) + "% femboy. «««");
                    return;
                }
                if (ChatConfig.runOthers) {
                    if (chatMessage.startsWith(ChatConfig.chatPrefix + Commands.FEMBOY.getCommand() + " ")) {
                        String[] args = chatMessage.split(" ");
                        if (args.length > 1) {
                            String gay = args[1];
                            sendLaterParty("/pc »»» " + gay + " is " + RandomUtils.nextInt(0, 101) + "% femboy. «««");
                            return;
                        }
                    }
                }
            }

            if (ChatConfig.ai) {
                if (chatMessage.startsWith(ChatConfig.chatPrefix + Commands.AI.getCommand())) {
                    String[] args = chatMessage.split("\\" + ChatConfig.chatPrefix + Commands.AI.getCommand() + " ");
                    if (args.length == 2) {
                        String prompt = args[1];
                        mod.apiUtil.getAI(prompt);
                    }
                }
            }

            if (ChatConfig.lastOnlineInfo && ChatConfig.runOthers) {
                if (chatMessage.startsWith(ChatConfig.chatPrefix + Commands.LASTONLINE.getCommand() + " ")) {
                    String[] args = chatMessage.split("\\" + ChatConfig.chatPrefix + Commands.LASTONLINE.getCommand() + " ");
                    if (args.length == 2) {
                        String player = args[1];
                        mod.apiUtil.getPlayerLastLogin(player);
                    }
                }
            }
            if (ChatConfig.racist) {
                if (chatMessage.equalsIgnoreCase(ChatConfig.chatPrefix + Commands.RACIST.getCommand())) {
                    int racistAmount = RandomUtils.nextInt(0, 101);
                    if (playerName.equalsIgnoreCase("dedj")) {
                        racistAmount = 100;
                    }
                    sendLaterParty("/pc »»» " + playerName + " is " + racistAmount + "% racist. «««");
                    return;
                }
                if (ChatConfig.runOthers) {
                    if (chatMessage.startsWith(ChatConfig.chatPrefix + Commands.RACIST.getCommand() + " ")) {
                        String[] args = chatMessage.split(" ");
                        if (args.length > 1) {
                            String racist = args[1];
                            int racistNumber = RandomUtils.nextInt(0, 101);
                            if (racist.equalsIgnoreCase("dedj")) {
                                racistNumber = 100;
                            }
                            sendLaterParty("/pc »»» " + racist + " is " + racistNumber + "% racist. «««");
                            return;
                        }
                    }
                }
            }

            if (ChatConfig.pray) {
                if (chatMessage.equalsIgnoreCase(ChatConfig.chatPrefix + Commands.PRAY.getCommand())) {
                    sendLaterParty("/pc »»» " + playerName + " prayed to RNGesus! (+" + RandomUtils.nextInt(0, 501) + "% ✯ Magic Find) «««");
                    return;
                }
            }
            if (ChatConfig.diceRoll) {
                if (chatMessage.equalsIgnoreCase(ChatConfig.chatPrefix + Commands.DICEROLL.getCommand())) {
                    int roll = RandomUtils.nextInt(1, 7);
                    sendLaterParty("/pc »»» " + playerName + " rolled a " + roll + "! " + getDice(roll) + " «««");
                    return;
                }
            }
            if (ChatConfig.multiDiceRoll) {
                if (chatMessage.startsWith(ChatConfig.chatPrefix + Commands.DICEROLL.getCommand() + " ")) {
                    String[] args = chatMessage.split(" ");
                    if (args.length == 2) {
                        String rollCount = args[1];
                        if (rollCount.matches("\\d+?")) {
                            int roll = Integer.parseInt(rollCount);
                            if (roll < 1) roll = 1;
                            if (roll > 10) roll = 10;
                            int total = 0;
                            StringBuilder builder = new StringBuilder();
                            for (int i = 0; i < roll; i++) {
                                int diceRoll = RandomUtils.nextInt(1, 7);
                                total += diceRoll;
                                builder.append(getDice(diceRoll));
                            }
                            sendLaterParty("/pc »»» " + playerName + " rolled " + roll + " dice! Total: " + total + "! " + builder + " «««");
                            return;
                        }
                    }
                }
            }



            if (ChatConfig.guild) {
                if (chatMessage.equalsIgnoreCase(ChatConfig.chatPrefix + Commands.GUILD.getCommand())) {
                    mod.apiUtil.sayGuildInformation(playerName);
                    return;
                }
                if (ChatConfig.runOthers) {
                    if (chatMessage.startsWith(ChatConfig.chatPrefix + Commands.GUILD.getCommand() + " ")) {
                        String[] args = chatMessage.split(" ");
                        if (args.length > 1) {
                            String player = args[1];
                            mod.apiUtil.sayGuildInformation(player);
                            return;
                        }
                    }
                }
            }

            if (ChatConfig.locate) {
                if (chatMessage.equalsIgnoreCase(ChatConfig.chatPrefix + Commands.LOCATE.getCommand())) {
                    mod.apiUtil.sayPlayerStatus(playerName);
                    return;
                }
                if (ChatConfig.runOthers) {
                    if (chatMessage.startsWith(ChatConfig.chatPrefix + "locate ")) {
                        String[] args = chatMessage.split("\\" + ChatConfig.chatPrefix + Commands.LOCATE.getCommand() + " ");
                        if (args.length > 1) {
                            String playerToLocate = args[1];
                            if (playerToLocate.contains(" ")) return;
                            mod.apiUtil.sayPlayerStatus(playerToLocate);
                            return;
                        }
                    }
                }
            }
        }
    }





/*
    public synchronized UUID fromName(String name) {
        ApiUtil.Request request = new ApiUtil.Request().url("https://api.minecraftservices.com/minecraft/profile/lookup/bulk/byname").method("POST");
        JsonArray object = new JsonArray();
        object.add(new JsonPrimitive(name));
        CompletableFuture<JsonElement> completableFuture = request.requestJsonAnon(object);
        UUID requested = null;

        try {
            JsonElement jsonElement = completableFuture.get();
            if (jsonElement.isJsonArray()) {
                JsonArray array = jsonElement.getAsJsonArray();
                for (JsonElement element : array) {
                    JsonObject jsonObject = element.getAsJsonObject();
                    requested = SkyblockUtil.fixMalformed(SkyblockUtil.getAsString("id", jsonObject));
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return requested;
    }

 */

    /*
    public UUID fromName(String name) {
        return EntityPlayer.getOfflineUUID(name);
    }

     */










/*
    public void getGuild(String playerName) {
        AsyncHttpClient client = asyncHttpClient();
        ListenableFuture<Response> whenResponse = client.prepareGet("https://api.hypixel.net/v2/guild?player=" + uuid)
                .addHeader("API-Key", MeloConfiguration.apiKey)
                .setHeader("Accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .execute();
        Runnable callback = () -> {
            try {
                Response response = whenResponse.get();
                JsonObject object = new JsonParser().parse(response.getResponseBody()).getAsJsonObject();
                SkyblockUtil.Guild guild = new SkyblockUtil.Guild(object.get("guild").getAsJsonObject());
                if (!playerName.equals(Minecraft.getMinecraft().thePlayer.getName())) {
                    queue.add("/pc ✿ «" + playerName + "» Guild: [" + guild.getTag() + "] " + guild.getName() + " (" + guild.getGuildID() + ") ✿");
                } else {
                    queue.add("/pc ✿ Guild: [" + guild.getTag() + "] " + guild.getName() + " (" + guild.getGuildID() + ") ✿");
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        };
        Executor executor = Executors.newSingleThreadExecutor();
        whenResponse.addListener(callback, executor);
    }

    /*
    public CompletableFuture<Void> getGuild(String playerName) {
        return CompletableFuture.runAsync(() -> {
            try {
                URL url = new URL();
                HttpURLConnection con = (HttpURLConnection)  url.openConnection();

                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("API-Key", MeloConfiguration.apiKey);
                int status = con.getResponseCode();




                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    JsonObject object = new JsonParser().parse(response.toString()).getAsJsonObject();
                    SkyblockUtil.Guild guild = new SkyblockUtil.Guild(object.getAsJsonObject("session"));
                    if (!playerName.equals(Minecraft.getMinecraft().thePlayer.getName())) {
                        queue.add("/pc ✿ «" + playerName + "» Guild: [" + guild.getTag() + "] " + guild.getName() + " (" + guild.getGuildID() + ") ✿");
                    } else {
                        queue.add("/pc ✿ Guild: [" + guild.getTag() + "] " + guild.getName() + " (" + guild.getGuildID() + ") ✿");
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


     */
    private static final char[] dice = {'⚀', '⚁', '⚂', '⚃', '⚄', '⚅'};
    public static String getDice(int roll) {
        return String.valueOf(dice[roll - 1]);
    }
}
