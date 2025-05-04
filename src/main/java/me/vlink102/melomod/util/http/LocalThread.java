package me.vlink102.melomod.util.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.configuration.MainConfiguration;
import me.vlink102.melomod.util.translation.Feature;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LocalThread extends Thread {
    private final BufferedReader bufferedReader;
    @Getter
    private final PrintWriter printWriter;

    @Getter
    private final Socket socket;
    public LocalThread(Socket socket) {
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.printWriter = new PrintWriter(socket.getOutputStream());
            this.socket = socket;
        } catch (IOException e) {
            closeSocket(e);
            throw new RuntimeException(e);
        }
    }

    public void closeSocket(Exception exception) {
        try {
            socket.close();
            MeloMod.addError("&e" + Feature.GENERIC_WARNING_SOCKET_CLOSED + ": &cClient disconnected from local socket.", exception);
        } catch (IOException e) {
            MeloMod.addError("&4" + Feature.ERROR_POTENTIALLY_SEVERE + ": &c" + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public void sendRequestPacket(int id, ApiUtil.ChatChannel chatChannel) {
        if (MainConfiguration.debugMessages) {
            MeloMod.addDebug("&e" + Feature.GENERIC_DEBUG_SENT_PACKET.toString().replaceAll("\\{1}", "&7local.packet&e").replaceAll("\\{2}", "&7" + id + "&e."));
        }
        JsonObject requestObject = getRequestPacket(id, chatChannel);

        printWriter.println(requestObject.toString());
        printWriter.flush();
    }


    public JsonObject getRequestPacket(int id, ApiUtil.ChatChannel chatChannel) {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("channel", chatChannel.toString());
        return object;
    }

    @Override
    public void run() {
        try {
            String data;
            while ((data = bufferedReader.readLine()) != null) {
                if (data.startsWith("{")) {
                    JsonElement element = new JsonParser().parse(data);
                    if (element.isJsonObject()) {
                        JsonObject object = element.getAsJsonObject();
                        ApiUtil.sendLater("»»» " + object.get("content").getAsString() + " «««", ApiUtil.ChatChannel.fromString(object.get("channel").getAsString()));
                    }
                } else {
                    if (!data.startsWith("|")) {
                        MeloMod.addWarn("&e" + Feature.GENERIC_WARNING_MALFORMED_PACKET + ": &7" + data);
                    }
                }

                //MeloMod.addDebug("&7" + Feature.GENERIC_DEBUG_DATA + ": &8" + MeloMod.gson.toJson(ItemSerializer.INSTANCE.deserializeFromBase64(data)));
            }
        } catch (IOException e) {
            MeloMod.addError("&cLost connection to local server (" + e.getMessage() + ": " + e.getCause() + ")", e);
            closeSocket(e);
        }
    }
}