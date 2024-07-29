package me.vlink102.melomod.util;

import com.google.common.collect.Sets;
import me.vlink102.melomod.events.ChatEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class StringUtils {
    public static final Set<String> PROTOCOLS = Sets.newHashSet("http", "https");

    public static String cleanColour(String in) {
        return in.replaceAll("(?i)\\u00A7.", "");
    }

    public static String cleanColourNotModifiers(String in) {
        return in.replaceAll("(?i)\\u00A7[0-9a-f]", "\u00A7r");
    }

    public static List<String> paginateHelp() {
        List<String> helpMenu = new ArrayList<>();
        String startString = "⭐ Available Commands (Page #): ";
        List<ChatEvent.Commands> toggledOn = getToggledOn();

        HashMap<Integer, Integer> pages = getIntegerHashMap(startString, toggledOn);

        int currentCommand = 0;
        for (int i = 0; i < pages.size(); i++) {
            int commands = pages.get(i);
            StringJoiner joiner = new StringJoiner(", ");
            String s = startString.replaceAll("#", String.valueOf(i + 1));
            joiner.setEmptyValue(s);
            for (int j = 0; j < commands; j++) {
                ChatEvent.Commands command = toggledOn.get(currentCommand);
                joiner.add(command.getString());
                currentCommand ++;
            }
            helpMenu.add(s + joiner);
        }
        return helpMenu;
    }

    public static List<String> paginate(String prefix, List<String> strings) {
        List<String> helpMenu = new ArrayList<>();
        HashMap<Integer, Integer> pages = getPaginatedMap(prefix, strings);
        System.out.println(pages);

        int currentElement = 0;
        for (int i = 0; i < pages.size(); i++) {
            int elementCount = pages.get(i);
            StringJoiner joiner = new StringJoiner(", ");
            String s = prefix.replaceAll("#", String.valueOf(i + 1));
            joiner.setEmptyValue(s);
            for (int j = 0; j < elementCount; j++) {
                String command = strings.get(currentElement);
                joiner.add(command);
                currentElement ++;
            }
            helpMenu.add(s + joiner);
        }
        return helpMenu;
    }

    private static HashMap<Integer, Integer> getPaginatedMap(String prefix, List<String> strings) {
        int limit = 254 - prefix.length(); // 2 less since i want to add the symbol at the end
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

    private static List<ChatEvent.Commands> getToggledOn() {
        List<ChatEvent.Commands> toggledOn = new ArrayList<>();
        for (ChatEvent.Commands value : ChatEvent.Commands.values()) {
            if (value.getToggle()) {
                toggledOn.add(value);
            }
        }
        return toggledOn;
    }

    private static HashMap<Integer, Integer> getIntegerHashMap(String start, List<ChatEvent.Commands> toggledOn) {
        int limit = 254 - start.length(); // 2 less since i want to add the symbol at the end
        int page = 0;
        int commands = 0;
        HashMap<Integer, Integer> pages = new HashMap<>();
        StringJoiner joiner = new StringJoiner(", ").setEmptyValue(start);
        for (int i = 0; i < toggledOn.size(); i++) {
            ChatEvent.Commands command = toggledOn.get(i);
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

    private static final char[] c = new char[] { 'k', 'm', 'b', 't' };

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
}