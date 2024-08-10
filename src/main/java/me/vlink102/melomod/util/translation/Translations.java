package me.vlink102.melomod.util.translation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import me.vlink102.melomod.MeloMod;
import net.minecraft.client.Minecraft;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translations {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("%[A-Za-z-]+%");

    public static String getMessage(String path, Object... variables) {
        String text = "";
        try {
            MeloMod main = MeloMod.INSTANCE;

            // Get the string.
            String[] pathSplit = path.split(Pattern.quote("."));
            JsonObject jsonObject = main.getLanguageConfig();
            for (String pathPart : pathSplit) {
                if (!pathPart.isEmpty()) {
                    JsonElement jsonElement = jsonObject.get(pathPart);

                    if (jsonElement.isJsonObject()) {
                        jsonObject = jsonObject.getAsJsonObject(pathPart);
                    } else {
                        text = jsonObject.get(path.substring(path.lastIndexOf(pathPart))).getAsString();
                        break;
                    }
                }
            }

            // Iterate through the string and replace any variables.
            Matcher matcher = VARIABLE_PATTERN.matcher(text);
            Deque<Object> variablesDeque = new ArrayDeque<>(Arrays.asList(variables));

            while (matcher.find() && !variablesDeque.isEmpty()) {
                // Replace a variable and re-make the matcher.
                text = matcher.replaceFirst(Matcher.quoteReplacement(variablesDeque.pollFirst().toString()));
                matcher = VARIABLE_PATTERN.matcher(text);
            }

            // Handle RTL text...
            if ((main.getLanguage() == Language.HEBREW || main.getLanguage() == Language.ARABIC) &&
                    !Minecraft.getMinecraft().fontRendererObj.getBidiFlag()) {
                text = bidiReorder(text);
            }
        } catch (Exception ex) {
            text = path; // In case of fire...
        }
        return text;
    }

    private static String bidiReorder(String text) {
        try {
            Bidi bidi = new Bidi((new ArabicShaping(ArabicShaping.LETTERS_SHAPE)).shape(text), Bidi.DIRECTION_DEFAULT_RIGHT_TO_LEFT);
            bidi.setReorderingMode(Bidi.REORDER_DEFAULT);
            return bidi.writeReordered(Bidi.DO_MIRRORING);
        } catch (ArabicShapingException ex) {
            return text;
        }
    }
}