package me.vlink102.melomod.events;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.ChatReceiveEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import com.google.gson.JsonObject;
import lombok.Getter;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.chatcooldownmanager.ServerTracker;
import me.vlink102.melomod.configuration.ChatConfiguration;
import me.vlink102.melomod.util.StringUtils;
import me.vlink102.melomod.util.game.neu.Utils;
import me.vlink102.melomod.util.http.ApiUtil;
import me.vlink102.melomod.util.math.eval.DoubleEvaluator;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.vlink102.melomod.util.StringUtils.paginateHelp;
import static me.vlink102.melomod.util.http.ApiUtil.sendLater;

public class ChatEventHandler {

    public static final HashMap<SeaCreature, Integer> seaCreatureSession = new HashMap<>();
    public static final HashMap<SeaCreature, Integer> lastCaught = new HashMap<>();
    public static final HashMap<SeaCreature, Long> lastCaughtTimeStamp = new HashMap<>();
    public static final HashMap<String, String> commands = new HashMap<>();
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

    // TODO
    private static final HashMap<String, Integer[]> hidden = new HashMap<String, Integer[]>() {{
        this.put("ZenmosM", new Integer[]{});
    }};
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
    private static boolean doubleHook = false;
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
    public List<String> party = new ArrayList<>();
    private int memberCount = -1;

    public ChatEventHandler(MeloMod mod) {
        this.mod = mod;
        EventManager.INSTANCE.register(this);
    }

    public static SeaCreature getSeaCreature(String string) {
        if (!mapping.containsKey(string)) return SeaCreature.NOTHING;
        return mapping.get(string);
    }

    private static String prettyTime(long millis) {
        return Utils.prettyTime(millis);
    }

    private static void updateDoubleHook(String string) {
        if (string.equalsIgnoreCase("Double Hook!") || string.equalsIgnoreCase("It's a Double Hook!") || string.equalsIgnoreCase("It's a Double Hook! Woot woot!")) {
            doubleHook = true;
        }
    }

    public static ApiUtil.ChatChannel from(String string) {
        switch (string) {
            case "Party":
                return ApiUtil.ChatChannel.PARTY;
            case "Officer":
                return ApiUtil.ChatChannel.OFFICER;
            case "Guild":
                return ApiUtil.ChatChannel.GUILD;
            case "Co-op":
                return ApiUtil.ChatChannel.COOP;
        }
        return null;
    }

    public static String getDice(int roll) {
        return String.valueOf(dice[roll - 1]);
    }

    public synchronized void getChessMove(String fen, ApiUtil.ChatChannel chatChannel) {
        JsonObject body = new JsonObject();
        body.addProperty("fen", fen);
        body.addProperty("depth", 10);
        mod.apiUtil.requestServer("https://chess-api.com/v1", body, object -> {
            if (object.isJsonObject()) {
                sendLater("»»» " + object.getAsJsonObject().get("text").getAsString() + " «««", chatChannel);
            }
        });
    }

    public void updateLastCaught(SeaCreature caught, boolean doubleHook, ApiUtil.ChatChannel chatChannel) {
        seaCreatureSession.put(caught, seaCreatureSession.getOrDefault(caught, 0) + (doubleHook ? 2 : 1));
        if (!lastCaught.containsKey(caught) && !lastCaughtTimeStamp.containsKey(caught)) {
            if (ChatConfiguration.fishingChat) {
                sendLater((doubleHook ? "‹⚓› ‼ DOUBLE HOOK ‼ " : "‹☂› ") + "☆ Caught a " + WordUtils.capitalizeFully(caught.toString().replaceAll("_", " ")) + "! ◆ First Catch in Session! ☆", chatChannel);
            }
        } else {
            if (ChatConfiguration.fishingChat) {
                sendLater((doubleHook ? "‹⚓› ‼ DOUBLE HOOK ‼ " : "‹☂› ") + "☆ Caught a " + WordUtils.capitalizeFully(caught.toString().replaceAll("_", " ")) + "! ◆ Total: " + seaCreatureSession.get(caught) + " ◆ Since Last: " + lastCaught.get(caught) + " ◆ Last Caught: " + prettyTime(System.currentTimeMillis() - lastCaughtTimeStamp.get(caught)) + " ago. ☆", chatChannel);
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
            updateLastCaught(creature, doubleHook, ApiUtil.ChatChannel.PARTY);
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

        StringJoiner joiner = new StringJoiner("|");
        if (ChatConfiguration.guildChat) {
            joiner.add("Guild");
        }
        if (ChatConfiguration.partyChat) {
            joiner.add("Party");
        }
        if (ChatConfiguration.officerChat) {
            joiner.add("Officer");
        }
        if (ChatConfiguration.coopChat) {
            joiner.add("Co-op");
        }
        String chatType = "(" + joiner + ")";

        if (stripped.matches(chatType + " >\\s.*?:.*")) {
            String chatMessage = stripped.split(chatType + "\\s>.*?:\\s")[1];
            String regex = chatType + " > (?>\\[.*?] )?(.*?):";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(stripped);
            String playerName;
            if (matcher.find()) {
                playerName = matcher.group(2);
            } else {
                playerName = "?";
            }

            String chatChannel = matcher.group(1);

            ApiUtil.ChatChannel chatChannel1 = from(chatChannel);

            MeloMod.addDebug("&7Using chat channel: &8" + chatChannel1);

            executeChatCommand(chatMessage, playerName, chatChannel1);
        }
    }

    public void executeChatCommand(String chatMessage, String playerName, ApiUtil.ChatChannel chatChannel) {
        if (chatMessage.equalsIgnoreCase(ChatConfiguration.chatPrefix + Commands.HELP.getCommand())) {
            sendLater(paginateHelp().get(0), chatChannel);
        }
        if (chatMessage.startsWith(ChatConfiguration.chatPrefix + "help ")) {
            String[] args = chatMessage.split("\\" + ChatConfiguration.chatPrefix + Commands.HELP.getCommand() + " ");
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
                    sendLater(paginated.get(lengthInt - 1), chatChannel);
                }
            }
        }


        if (ChatConfiguration.wholePartySecret) {
            if (chatMessage.equalsIgnoreCase(ChatConfiguration.chatPrefix + Commands.SECRET)) {
                //sendLater("/party list"); TODO
            }
        }
        if (ChatConfiguration.chessEngine) {
            if (chatMessage.startsWith(ChatConfiguration.chatPrefix + Commands.CHESSENGINE.getCommand() + " ")) {
                String[] args = chatMessage.split("\\" + ChatConfiguration.chatPrefix + Commands.CHESSENGINE.getCommand() + " ");
                if (args.length > 1) {
                    String fen = args[1];
                    getChessMove(fen, chatChannel);
                }
            }
        }

        if (ChatConfiguration.stalk) {
            if (chatMessage.startsWith(ChatConfiguration.chatPrefix + Commands.STALK.getCommand() + " ")) {
                String[] args = chatMessage.split("\\" + ChatConfiguration.chatPrefix + Commands.STALK.getCommand() + " ");
                if (args.length > 1) {
                    String player = args[1];
                    mod.apiUtil.lastLogin(player, chatChannel);
                }
            }
        }
        if (ChatConfiguration.mathEvaluation) {
            if (chatMessage.startsWith(ChatConfiguration.chatPrefix + Commands.MATHEVALUATION.getCommand() + " ")) {
                String[] args = chatMessage.split("\\" + ChatConfiguration.chatPrefix + Commands.MATHEVALUATION.getCommand() + " ");
                if (args.length > 1) {
                    String expression = args[1];
                    sendLater("⚡ Evaluated: ≈" + new DoubleEvaluator().evaluate(expression).toString(), chatChannel);
                }
            }
        }


        if (ChatConfiguration.userNameHistory) {
            if (chatMessage.equalsIgnoreCase(ChatConfiguration.chatPrefix + Commands.HISTORY.getCommand())) {
                mod.apiUtil.getPlayerPastNames(playerName, 1, chatChannel);
            }
            if (ChatConfiguration.runOthers) {
                if (chatMessage.startsWith(ChatConfiguration.chatPrefix + Commands.HISTORY.getCommand() + " ")) {
                    String[] args = chatMessage.split(" ");
                    if (args.length == 2) {
                        String player = args[1];
                        if (args[1].matches("\\d+?")) {
                            int page = Integer.parseInt(args[1]);
                            mod.apiUtil.getPlayerPastNames(player, page, chatChannel);
                        } else {
                            mod.apiUtil.getPlayerPastNames(player, 1, chatChannel);
                        }
                    }
                    if (args.length == 3) {
                        String player = args[1];
                        if (args[2].matches("\\d+?")) {
                            int page = Integer.parseInt(args[2]);
                            mod.apiUtil.getPlayerPastNames(player, page, chatChannel);
                        }
                    }
                }
            }
        }
        if (ChatConfiguration.secret) {
            if (chatMessage.equalsIgnoreCase(ChatConfiguration.chatPrefix + Commands.SECRET.getCommand())) {
                mod.apiUtil.sayPlayerSecrets(playerName, chatChannel);
            }
            if (ChatConfiguration.runOthers) {
                if (chatMessage.startsWith(ChatConfiguration.chatPrefix + Commands.SECRET.getCommand() + " ")) {
                    String[] args = chatMessage.split("\\" + ChatConfiguration.chatPrefix + Commands.SECRET.getCommand() + " ");
                    if (args.length == 2) {
                        String player = args[1];
                        mod.apiUtil.sayPlayerSecrets(player, chatChannel);
                    }
                }
            }
        }
        if (ChatConfiguration.networth) {
            if (chatMessage.equalsIgnoreCase(ChatConfiguration.chatPrefix + Commands.NETWORTH)) {
                mod.apiUtil.sayPlayerNetworth(playerName, chatChannel);
            }
            if (ChatConfiguration.runOthers) {
                if (chatMessage.startsWith(ChatConfiguration.chatPrefix + Commands.NETWORTH.getCommand() + " ")) {
                    String[] args = chatMessage.split("\\" + ChatConfiguration.chatPrefix + Commands.NETWORTH.getCommand() + " ");
                    if (args.length == 2) {
                        String check = args[1];
                        mod.apiUtil.sayPlayerNetworth(check, chatChannel);
                    }
                }
            }
        }
        if (ChatConfiguration.coinFlip) {
            if (chatMessage.equalsIgnoreCase(ChatConfiguration.chatPrefix + Commands.COINFLIP.getCommand())) {
                sendLater("»»» " + playerName + " flipped a coin! It's " + (RandomUtils.nextInt(0, 2) == 0 ? "Heads!" : "Tails!") + " «««", chatChannel);
            }
        }
        if (ChatConfiguration.socialMedia) {
            if (chatMessage.equalsIgnoreCase(ChatConfiguration.chatPrefix + Commands.DISCORD.getCommand())) {
                mod.apiUtil.playerSocials(playerName, "dc", chatChannel);
            }
            if (ChatConfiguration.runOthers) {
                if (chatMessage.startsWith(ChatConfiguration.chatPrefix + "dc ")) {
                    String[] args = chatMessage.split("\\" + ChatConfiguration.chatPrefix + Commands.DISCORD.getCommand() + " ");
                    if (args.length > 1) {
                        String player = args[1];
                        mod.apiUtil.playerSocials(player, "dc", chatChannel);
                        return;
                    }
                }
            }
            if (chatMessage.equalsIgnoreCase(ChatConfiguration.chatPrefix + Commands.TWITTER.getCommand())) {
                mod.apiUtil.playerSocials(playerName, "twitter", chatChannel);
            }
            if (ChatConfiguration.runOthers) {
                if (chatMessage.startsWith(ChatConfiguration.chatPrefix + Commands.TWITTER.getCommand() + " ")) {
                    String[] args = chatMessage.split("\\" + ChatConfiguration.chatPrefix + Commands.TWITTER.getCommand() + " ");
                    if (args.length > 1) {
                        String player = args[1];
                        mod.apiUtil.playerSocials(player, "twitter", chatChannel);
                        return;
                    }
                }
            }
            if (chatMessage.equalsIgnoreCase(ChatConfiguration.chatPrefix + Commands.TWITCH.getCommand())) {
                mod.apiUtil.playerSocials(playerName, "twitch", chatChannel);
            }
            if (ChatConfiguration.runOthers) {
                if (chatMessage.startsWith(ChatConfiguration.chatPrefix + Commands.TWITCH.getCommand() + " ")) {
                    String[] args = chatMessage.split("\\" + ChatConfiguration.chatPrefix + Commands.TWITCH.getCommand() + " ");
                    if (args.length > 1) {
                        String player = args[1];
                        mod.apiUtil.playerSocials(player, "twitch", chatChannel);
                        return;
                    }
                }
            }
            if (chatMessage.equalsIgnoreCase(ChatConfiguration.chatPrefix + Commands.INSTAGRAM.getCommand())) {
                mod.apiUtil.playerSocials(playerName, "instagram", chatChannel);
            }
            if (ChatConfiguration.runOthers) {
                if (chatMessage.startsWith(ChatConfiguration.chatPrefix + Commands.INSTAGRAM.getCommand() + " ")) {
                    String[] args = chatMessage.split("\\" + ChatConfiguration.chatPrefix + Commands.INSTAGRAM.getCommand() + " ");
                    if (args.length > 1) {
                        String player = args[1];
                        mod.apiUtil.playerSocials(player, "instagram", chatChannel);
                        return;
                    }
                }
            }
            if (chatMessage.equalsIgnoreCase(ChatConfiguration.chatPrefix + Commands.YOUTUBE.getCommand())) {
                mod.apiUtil.playerSocials(playerName, "youtube", chatChannel);
            }
            if (ChatConfiguration.runOthers) {
                if (chatMessage.startsWith(ChatConfiguration.chatPrefix + Commands.YOUTUBE.getCommand() + " ")) {
                    String[] args = chatMessage.split("\\" + ChatConfiguration.chatPrefix + Commands.YOUTUBE.getCommand() + " ");
                    if (args.length > 1) {
                        String player = args[1];
                        mod.apiUtil.playerSocials(player, "youtube", chatChannel);
                        return;
                    }
                }
            }
            if (chatMessage.equalsIgnoreCase(ChatConfiguration.chatPrefix + Commands.TIKTOK.getCommand())) {
                mod.apiUtil.playerSocials(playerName, "tiktok", chatChannel);
            }
            if (ChatConfiguration.runOthers) {
                if (chatMessage.startsWith(ChatConfiguration.chatPrefix + Commands.TIKTOK.getCommand() + " ")) {
                    String[] args = chatMessage.split("\\" + ChatConfiguration.chatPrefix + Commands.TIKTOK.getCommand() + " ");
                    if (args.length > 1) {
                        String player = args[1];
                        mod.apiUtil.playerSocials(player, "tiktok", chatChannel);
                        return;
                    }
                }
            }
            if (chatMessage.equalsIgnoreCase(ChatConfiguration.chatPrefix + Commands.FORUMS.getCommand())) {
                mod.apiUtil.playerSocials(playerName, "forums", chatChannel);
            }
            if (ChatConfiguration.runOthers) {
                if (chatMessage.startsWith(ChatConfiguration.chatPrefix + Commands.FORUMS.getCommand() + " ")) {
                    String[] args = chatMessage.split("\\" + ChatConfiguration.chatPrefix + Commands.FORUMS.getCommand() + " ");
                    if (args.length > 1) {
                        String player = args[1];
                        mod.apiUtil.playerSocials(player, "forums", chatChannel);
                        return;
                    }
                }
            }
        }
        if (ChatConfiguration.gay) {
            if (chatMessage.equalsIgnoreCase(ChatConfiguration.chatPrefix + Commands.GAY.getCommand())) {
                int randomNum = RandomUtils.nextInt(0, 101);
                if (playerName.equalsIgnoreCase("kalabash")) randomNum = 100;
                sendLater("»»» " + playerName + " is " + randomNum + "% gay. «««", chatChannel);
                return;
            }
            if (ChatConfiguration.runOthers) {
                if (chatMessage.startsWith(ChatConfiguration.chatPrefix + Commands.GAY.getCommand() + " ")) {
                    String[] args = chatMessage.split(" ");
                    if (args.length > 1) {
                        String gay = args[1];
                        int randomNum = RandomUtils.nextInt(0, 101);
                        if (gay.equalsIgnoreCase("kalabash")) randomNum = 100;
                        sendLater("»»» " + gay + " is " + randomNum + "% gay. «««", chatChannel);
                        return;
                    }
                }
            }
        }
        if (ChatConfiguration.nWordPass) {
            if (chatMessage.equalsIgnoreCase(ChatConfiguration.chatPrefix + Commands.NPASS.getCommand())) {
                sendLater("»»» " + playerName + " was granted the N pass. «««", chatChannel);
            }
        }
        if (ChatConfiguration.femboy) {
            if (chatMessage.equalsIgnoreCase(ChatConfiguration.chatPrefix + Commands.FEMBOY.getCommand())) {
                sendLater("»»» " + playerName + " is " + RandomUtils.nextInt(0, 101) + "% femboy. «««", chatChannel);
                return;
            }
            if (ChatConfiguration.runOthers) {
                if (chatMessage.startsWith(ChatConfiguration.chatPrefix + Commands.FEMBOY.getCommand() + " ")) {
                    String[] args = chatMessage.split(" ");
                    if (args.length > 1) {
                        String gay = args[1];
                        sendLater("»»» " + gay + " is " + RandomUtils.nextInt(0, 101) + "% femboy. «««", chatChannel);
                        return;
                    }
                }
            }
        }

        if (ChatConfiguration.ai) {
            if (chatMessage.startsWith(ChatConfiguration.chatPrefix + Commands.AI.getCommand())) {
                String[] args = chatMessage.split("\\" + ChatConfiguration.chatPrefix + Commands.AI.getCommand() + " ");
                if (args.length == 2) {
                    String prompt = args[1];
                    mod.apiUtil.getAI(prompt, chatChannel);
                }
            }
        }

        if (ChatConfiguration.lastOnlineInfo && ChatConfiguration.runOthers) {
            if (chatMessage.startsWith(ChatConfiguration.chatPrefix + Commands.LASTONLINE.getCommand() + " ")) {
                String[] args = chatMessage.split("\\" + ChatConfiguration.chatPrefix + Commands.LASTONLINE.getCommand() + " ");
                if (args.length == 2) {
                    String player = args[1];
                    mod.apiUtil.getPlayerLastLogin(player, chatChannel);
                }
            }
        }
        if (ChatConfiguration.racist) {
            if (chatMessage.equalsIgnoreCase(ChatConfiguration.chatPrefix + Commands.RACIST.getCommand())) {
                int racistAmount = RandomUtils.nextInt(0, 101);
                if (playerName.equalsIgnoreCase("dedj")) {
                    racistAmount = 100;
                }
                sendLater("»»» " + playerName + " is " + racistAmount + "% racist. «««", chatChannel);
                return;
            }
            if (ChatConfiguration.runOthers) {
                if (chatMessage.startsWith(ChatConfiguration.chatPrefix + Commands.RACIST.getCommand() + " ")) {
                    String[] args = chatMessage.split(" ");
                    if (args.length > 1) {
                        String racist = args[1];
                        int racistNumber = RandomUtils.nextInt(0, 101);
                        if (racist.equalsIgnoreCase("dedj")) {
                            racistNumber = 100;
                        }
                        sendLater("»»» " + racist + " is " + racistNumber + "% racist. «««", chatChannel);
                        return;
                    }
                }
            }
        }

        if (ChatConfiguration.pray) {
            if (chatMessage.equalsIgnoreCase(ChatConfiguration.chatPrefix + Commands.PRAY.getCommand())) {
                sendLater("»»» " + playerName + " prayed to RNGesus! (+" + RandomUtils.nextInt(0, 501) + "% ✯ Magic Find) «««", chatChannel);
                return;
            }
        }
        if (ChatConfiguration.diceRoll) {
            if (chatMessage.equalsIgnoreCase(ChatConfiguration.chatPrefix + Commands.DICEROLL.getCommand())) {
                int roll = RandomUtils.nextInt(1, 7);
                sendLater("»»» " + playerName + " rolled a " + roll + "! " + getDice(roll) + " «««", chatChannel);
                return;
            }
        }
        if (ChatConfiguration.multiDiceRoll) {
            if (chatMessage.startsWith(ChatConfiguration.chatPrefix + Commands.DICEROLL.getCommand() + " ")) {
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
                        sendLater("»»» " + playerName + " rolled " + roll + " dice! Total: " + total + "! " + builder + " «««", chatChannel);
                        return;
                    }
                }
            }
        }


        if (ChatConfiguration.guild) {
            if (chatMessage.equalsIgnoreCase(ChatConfiguration.chatPrefix + Commands.GUILD.getCommand())) {
                mod.apiUtil.sayGuildInformation(playerName, chatChannel);
                return;
            }
            if (ChatConfiguration.runOthers) {
                if (chatMessage.startsWith(ChatConfiguration.chatPrefix + Commands.GUILD.getCommand() + " ")) {
                    String[] args = chatMessage.split(" ");
                    if (args.length > 1) {
                        String player = args[1];
                        mod.apiUtil.sayGuildInformation(player, chatChannel);
                        return;
                    }
                }
            }
        }

        if (ChatConfiguration.locate) {
            if (chatMessage.equalsIgnoreCase(ChatConfiguration.chatPrefix + Commands.LOCATE.getCommand())) {
                mod.apiUtil.sayPlayerStatus(playerName, chatChannel);
                return;
            }
            if (ChatConfiguration.runOthers) {
                if (chatMessage.startsWith(ChatConfiguration.chatPrefix + "locate ")) {
                    String[] args = chatMessage.split("\\" + ChatConfiguration.chatPrefix + Commands.LOCATE.getCommand() + " ");
                    if (args.length > 1) {
                        String playerToLocate = args[1];
                        if (playerToLocate.contains(" ")) return;
                        mod.apiUtil.sayPlayerStatus(playerToLocate, chatChannel);
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


    @Getter
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

        public static SeaCreature convertBestiaryMob(String bestiaryString) {
            for (SeaCreature value : SeaCreature.values()) {
                if (Arrays.asList(value.internalID).contains(bestiaryString)) {
                    return value;
                }
            }
            return NOTHING;
        }

    }

    public enum Commands {
        GAY("gay", ChatConfiguration::isGay, optional("player", true)),
        RACIST("racist", ChatConfiguration::isRacist, optional("player", true)),
        DICEROLL("dice", ChatConfiguration::isDiceRoll, optional("amount", false)),
        PRAY("pray", ChatConfiguration::isPray),
        CHESSENGINE("chess", ChatConfiguration::isChessEngine, required("fen", false)),
        MATHEVALUATION("eval", ChatConfiguration::isMathEvaluation, required("expression", false)),
        GUILD("guild", ChatConfiguration::isGuild, optional("player", true)),
        LOCATE("locate", ChatConfiguration::isLocate, optional("player", true)),
        COINFLIP("coinflip", ChatConfiguration::isCoinFlip),
        STALK("stalk", ChatConfiguration::isStalk, required("player", true)),
        DISCORD("dc", ChatConfiguration::isSocialMedia, optional("player", true)),
        YOUTUBE("youtube", ChatConfiguration::isSocialMedia, optional("player", true)),
        TWITTER("twitter", ChatConfiguration::isSocialMedia, optional("player", true)),
        TWITCH("twitch", ChatConfiguration::isSocialMedia, optional("player", true)),
        TIKTOK("tiktok", ChatConfiguration::isSocialMedia, optional("player", true)),
        FORUMS("forums", ChatConfiguration::isSocialMedia, optional("player", true)),
        INSTAGRAM("instagram", ChatConfiguration::isSocialMedia, optional("player", true)),
        HELP("help", () -> true, optional("page", false)),
        SECRET("secrets", ChatConfiguration::isSecret, optional("player", true)),
        NPASS("npass", ChatConfiguration::isnWordPass, optional("player", true)),
        FEMBOY("femboy", ChatConfiguration::isFemboy, optional("player", true)),
        NETWORTH("networth", ChatConfiguration::isNetworth, optional("player", true)),
        HISTORY("history", ChatConfiguration::isUserNameHistory, optional("player", true), optional("page", false)),
        LASTONLINE("seen", ChatConfiguration::isLastOnlineInfo, optional("player", true)),
        AI("ai", ChatConfiguration::isAi, required("prompt", false));

        @Getter
        private final String command;
        @Getter
        private final List<CommandParameter> params;
        private final Supplier<Boolean> toggle;

        Commands(String command, Supplier<Boolean> toggle, CommandParameter... params) {
            this.command = command;
            this.toggle = toggle;
            this.params = Arrays.asList(params);
        }

        public static CommandParameter required(String paramName, boolean isOther) {
            return new CommandParameter(paramName, true, isOther);
        }

        public static CommandParameter optional(String paramName, boolean isOther) {
            return new CommandParameter(paramName, false, isOther);
        }

        public boolean getToggle() {
            return toggle.get();
        }

        public String getString() {
            StringJoiner joiner = new StringJoiner(" ");
            joiner.add(ChatConfiguration.chatPrefix + getCommand());
            for (CommandParameter param : params) {
                if (param.isOther && !ChatConfiguration.runOthers) {
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

        public static class CommandParameter {
            @Getter
            private final String paramName;
            @Getter
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

        }
    }
}
