package me.vlink102.melomod.util;

import me.vlink102.melomod.events.ChatEventHandler;
import me.vlink102.melomod.events.LocrawHandler;
import me.vlink102.melomod.util.enums.DefaultFontInfo;
import me.vlink102.melomod.util.game.neu.Utils;
import me.vlink102.melomod.util.translation.Feature;
import net.minecraft.util.ChatStyle;
import org.apache.commons.lang3.text.WordUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@SuppressWarnings("unused")
public class StringUtils {

    @Deprecated
    public static final ChatStyle RESET_INTERACTIONS = new ChatStyle()
            .setChatHoverEvent(null)
            .setChatClickEvent(null);
    private static final char[] c = new char[]{'k', 'm', 'b', 't'};

    public static String cleanColour(String in) {
        return in.replaceAll("(?i)\\u00A7.", "");
    }

    public static String cleanColourNotModifiers(String in) {
        return in.replaceAll("(?i)\\u00A7[0-9a-f]", "§r");
    }

    public static String cc(String in) {
        return in.replaceAll("(?i)&([\\da-fklmnorx])", "§$1");
    }

    public static List<String> paginateHelp() {
        List<String> helpMenu = new ArrayList<>();
        String startString = "⭐ " + Feature.GENERIC_AVAILABLE_COMMANDS + ": ";
        List<ChatEventHandler.Commands> toggledOn = getToggledOn();

        HashMap<Integer, Integer> pages = getIntegerHashMap(startString, toggledOn);

        int currentCommand = 0;
        for (int i = 0; i < pages.size(); i++) {
            int commands = pages.get(i);
            StringJoiner joiner = new StringJoiner(", ");
            String s = startString.replaceAll("#", String.valueOf(i + 1));
            joiner.setEmptyValue(s);
            for (int j = 0; j < commands; j++) {
                ChatEventHandler.Commands command = toggledOn.get(currentCommand);
                joiner.add(command.getString());
                currentCommand++;
            }
            helpMenu.add(s + joiner + " ⭐");
        }
        return helpMenu;
    }

    public static List<String> paginate(String prefix, List<String> strings) {
        List<String> helpMenu = new ArrayList<>();
        HashMap<Integer, Integer> pages = getPaginatedMap(prefix, strings);

        int currentElement = 0;
        for (int i = 0; i < pages.size(); i++) {
            int elementCount = pages.get(i);
            StringJoiner joiner = new StringJoiner(", ");
            String s = prefix.replaceAll("#", String.valueOf(i + 1));
            joiner.setEmptyValue(s);
            for (int j = 0; j < elementCount; j++) {
                String command = strings.get(currentElement);
                joiner.add(command);
                currentElement++;
            }
            helpMenu.add(s + joiner);
        }
        return helpMenu;
    }

    public static List<String> paginateOnline(HashMap<String, LocrawHandler.LocrawInfo> strings, int perLine) {
        List<String> menu = new ArrayList<>();
        HashMap<Integer, Integer> pages = getPaginatedMap(strings, perLine);

        List<String> fromStrings = new ArrayList<>(strings.keySet());
        int currentElement = 0;
        for (int i = 0; i < pages.size(); i++) {
            int elementCount = pages.get(i);
            StringJoiner builder = new StringJoiner("\n");
            builder.add("§9-----------------------------------------------------");
            builder.add(getCentredMessage("§6" + Feature.GENERIC_ONLINE_PLAYERS + " (" + Feature.GENERIC_PAGE + " " + (i + 1) + " / " + pages.size() + ")"));
            for (int j = 0; j < elementCount; j++) {
                LocrawHandler.LocrawInfo command = strings.get(fromStrings.get(currentElement));
                StringBuilder playing = new StringBuilder();
                playing.append("§3").append(fromStrings.get(currentElement)).append(" §7is ");
                switch (command.isHypixel()) {
                    case HYPIXEL:
                        playing.append("in §e");
                        if (command.getGametype() != null) {
                            playing.append(WordUtils.capitalizeFully(command.getGametype().replaceAll("[-_]", "")));
                        } else {
                            playing.append(command.getServerIP());
                        }
                        if (command.getGamemode() != null) {
                            playing.append(" §6").append(command.getGamemode());
                        }
                        if (command.getMap() != null) {
                            playing.append(" §7(" + Feature.GENERIC_MAP + ": §b").append(command.getMap()).append("§7)");
                        }
                        if (command.getServerID() != null) {
                            playing.append(" §8[§d").append(command.getServerID()).append("§8]");
                        }
                        builder.add(playing.toString());
                        break;
                    case SERVER:
                        playing.append("in §e");
                        playing.append(command.getServerIP());
                        builder.add(playing.toString());
                        break;
                    case SINGLEPLAYER:
                        playing.append("in §e");
                        playing.append("§b" + Feature.GENERIC_SINGLE_PLAYER + ": §7'").append(command.getServerIP()).append("'");
                        builder.add(playing.toString());
                        break;
                    case OFFLINE:
                        playing.append("§c" + Feature.GENERIC_OFFLINE + "§7.");
                        builder.add(playing.toString());
                        break;
                }
                currentElement++;
            }
            builder.add("§9-----------------------------------------------------");
            menu.add(builder.toString());
        }
        return menu;
    }

    public static String getCentredMessage(String message) {
        String[] lines = message.split("\n", 40);
        StringBuilder returnMessage = new StringBuilder();


        for (String line : lines) {
            int messagePxSize = 0;
            boolean previousCode = false;
            boolean isBold = false;

            for (char c : line.toCharArray()) {
                if (c == '§') {
                    previousCode = true;
                } else if (previousCode) {
                    previousCode = false;
                    isBold = c == 'l';
                } else {
                    DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                    messagePxSize = isBold ? messagePxSize + dFI.getBoldLength() : messagePxSize + dFI.getLength();
                    messagePxSize++;
                }
            }
            int toCompensate = 154 - messagePxSize / 2;
            int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
            int compensated = 0;
            StringBuilder sb = new StringBuilder();
            while (compensated < toCompensate) {
                sb.append(" ");
                compensated += spaceLength;
            }
            returnMessage.append(sb).append(line);
        }

        return returnMessage.toString();
    }

    public static HashMap<Integer, Integer> getPaginatedMap(HashMap<String, LocrawHandler.LocrawInfo> strings, int perPage) {
        int length = strings.size();
        HashMap<Integer, Integer> pages = new HashMap<>();
        int page = 0;
        if (length < perPage) {
            pages.put(page, length);
            return pages;
        }
        int pageDiv = length / perPage;
        for (int i = 0; i < pageDiv; i++) {
            page++;
            pages.put(page, length);
        }
        if (length % perPage != 0) {
            page++;
            pages.put(page, length % perPage);
        }
        return pages;
    }

    public static HashMap<Integer, Integer> getPaginatedMap(String prefix, List<String> strings) {
        int limit = 254 - prefix.length(); // 2 less since I want to add the symbol at the end
        int page = 0;
        int commands = 0;
        HashMap<Integer, Integer> pages = new HashMap<>();
        StringJoiner joiner = new StringJoiner(", ").setEmptyValue(prefix);
        for (int i = 0; i < strings.size(); i++) {
            String command = strings.get(i);

            if (joiner.length() + command.length() + 2 >= limit) {
                pages.put(page, commands);
                page++;
                joiner = new StringJoiner(", ").setEmptyValue(prefix);
                commands = 0;
            }
            joiner.add(command);
            commands++;
            if (i + 1 == strings.size()) {
                pages.put(page, commands);
            }
        }
        return pages;
    }

    private static List<ChatEventHandler.Commands> getToggledOn() {
        List<ChatEventHandler.Commands> toggledOn = new ArrayList<>();
        for (ChatEventHandler.Commands value : ChatEventHandler.Commands.values()) {
            if (value.getToggle()) {
                toggledOn.add(value);
            }
        }
        return toggledOn;
    }

    private static HashMap<Integer, Integer> getIntegerHashMap(String start, List<ChatEventHandler.Commands> toggledOn) {
        int limit = 254 - start.length(); // 2 less since I want to add the symbol at the end
        int page = 0;
        int commands = 0;
        HashMap<Integer, Integer> pages = new HashMap<>();
        StringJoiner joiner = new StringJoiner(", ").setEmptyValue(start);
        for (int i = 0; i < toggledOn.size(); i++) {
            ChatEventHandler.Commands command = toggledOn.get(i);
            if (!command.getToggle()) continue;

            if (joiner.length() + command.getString().length() + 2 >= limit) {
                pages.put(page, commands);
                page++;
                joiner = new StringJoiner(", ").setEmptyValue(start);
                commands = 0;
            }
            joiner.add(command.getString());
            commands++;
            if (i + 1 == toggledOn.size()) {
                pages.put(page, commands);
            }
        }
        return pages;
    }

    public static String trimToWidth(String str, int len) {
        return Utils.trimToWidth(str, len);
    }

    public static String substringBetween(String str, String open, String close) {
        return org.apache.commons.lang3.StringUtils.substringBetween(str, open, close);
    }

    public static int cleanAndParseInt(String str) {
        str = cleanColour(str);
        str = str.replace(",", "");
        return Integer.parseInt(str);
    }

    public static String shortNumberFormat(double n) {
        return shortNumberFormat(n, 0);
    }

    public static String shortNumberFormat(double n, int iteration) {
        if (n < 1000) {
            if (n % 1 == 0) {
                return Integer.toString((int) n);
            } else {
                return String.format("%.2f", n);
            }
        }

        double d = ((double) (long) n / 100) / 10.0;
        boolean isRound = (d * 10) % 10 == 0;
        return d < 1000 ? (isRound || d > 9.99 ? (int) d * 10 / 10 : d + "") + "" + c[iteration] : shortNumberFormat(d, iteration + 1);
    }

    public static String urlEncode(String something) {
        try {
            return URLEncoder.encode(something, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e); // UTF 8 should always be present
        }
    }

    /**
     * taken and modified from <a href="https://stackoverflow.com/a/23326014/5507634">...</a>
     */
    public static String replaceLast(String string, String toReplace, String replacement) {
        int start = string.lastIndexOf(toReplace);
        return string.substring(0, start) + replacement + string.substring(start + toReplace.length());
    }

    public enum VComponentSettings {
        INHERIT_NONE(),
        INHERIT_ALL(),
        INHERIT_FORMAT() // Color/Bold/Italic/etc

    }
}