package me.vlink102.melomod.util.translation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.util.http.ApiUtil;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class DataUtils {
    private static String path;
    private static final Gson gson = ApiUtil.getGson();
    private static final MeloMod main = MeloMod.INSTANCE;

    public static void loadLocalizedStrings(Language language) {
        // logger.info("Loading localized strings for " + language.name() + "...");

        path = "lang/" + language.getPath() + ".json";
        try (InputStream inputStream = DataUtils.class.getClassLoader().getResourceAsStream(path);
             InputStreamReader inputStreamReader = new InputStreamReader(Objects.requireNonNull(inputStream),
                     StandardCharsets.UTF_8)) {
            main.setLanguageConfig(gson.fromJson(inputStreamReader, JsonObject.class));
            main.setLanguage(language);
        } catch (Exception ex) {
            handleLocalFileReadException(path, ex);
        }
    }

    /**
     * This method handles errors that can occur when reading the local configuration files.
     * If the game is still initializing, it displays an error screen and prints the stacktrace of the given
     * {@code Throwable} in the console.
     * If the game is initialized, it crashes the game with a crash report containing the file path and the stacktrace
     * of the given {@code Throwable}.
     *
     * @param filePath the path to the file that caused the exception
     * @param exception the exception that occurred
     */
    private static void handleLocalFileReadException(String filePath, Throwable exception) {
        if (FMLClientHandler.instance().isLoading()) {
            throw new DataLoadingException(filePath, exception);
        } else {
            CrashReport crashReport = CrashReport.makeCrashReport(exception, String.format("Loading data file at %s",
                    filePath));
            throw new ReportedException(crashReport);
        }
    }
}
