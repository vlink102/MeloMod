package me.vlink102.melomod.util.http;

import cc.polyfrost.oneconfig.events.EventManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.configuration.ChatConfiguration;
import me.vlink102.melomod.configuration.MainConfiguration;
import me.vlink102.melomod.events.LocrawHandler;
import me.vlink102.melomod.util.ItemSerializer;
import me.vlink102.melomod.util.StringUtils;
import me.vlink102.melomod.util.VChatComponent;
import me.vlink102.melomod.util.game.neu.Utils;
import me.vlink102.melomod.util.http.packets.*;
import me.vlink102.melomod.util.translation.Feature;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.IChatComponent;
import org.omg.PortableInterceptor.NON_EXISTENT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;


public class DataThread extends Thread {
    public static CloseReason closed = null;
    private final BufferedReader bufferedReader;
    private final PrintWriter printWriter;
    @Getter
    private final Socket socket;
    public JsonArray onlinePlayers;

    public DataThread(Socket socket) {
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.printWriter = new PrintWriter(socket.getOutputStream());
            this.socket = socket;
            closed = null;
        } catch (IOException e) {
            closeSocket(CloseReason.ERROR, e);
            throw new RuntimeException(e);
        }
    }

    public void closeSocket(CloseReason reason, Exception exception) {
        try {
            socket.close();
            closed = reason;

            switch (closed) {
                case CLIENT_OUT_OF_DATE:
                    MeloMod.addWarn("&e" + Feature.GENERIC_WARNING_SOCKET_CLOSED + ": &c" + Feature.GENERIC_WARNING_CLIENT_OUT_OF_DATE + " [" + closed + "]");
                    break;
                case INVALID_DATA:
                    MeloMod.addError("&e" + Feature.GENERIC_WARNING_SOCKET_CLOSED + ": &c" + Feature.ERROR_CLIENT_RECEIVED_INVALID_DATA + " [" + closed + "]", exception);
                    break;
                case ERROR:
                    MeloMod.addError("&e" + Feature.GENERIC_WARNING_SOCKET_CLOSED + ": &c" + Feature.ERROR_UNEXPECTED + " [" + closed + "]", exception);
                    break;
                case UNKNOWN:
                    MeloMod.addError("&e" + Feature.GENERIC_WARNING_SOCKET_CLOSED + ": &c" + Feature.ERROR_UNKNOWN + " [" + closed + "]", exception);
                    break;
                case CLOSED_BY_SERVER:
                    MeloMod.addWarn("&e" + Feature.GENERIC_WARNING_SOCKET_CLOSED + ": &c" + Feature.GENERIC_WARNING_CONNECTION_TERMINATED + " [" + closed + "]");
                    break;
            }
        } catch (IOException e) {
            MeloMod.addError("&4" + Feature.ERROR_POTENTIALLY_SEVERE + ": &c" + e.getMessage(), e);
            throw new RuntimeException(e);
        }

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
                        Packet.PacketID id = Packet.from(object.toString());
                        switch (id) {
                            case SERVER_CLOSED_CONNECTION:
                                ClientBoundForceDisconnectPacket clientBoundForceDisconnectPacket = (ClientBoundForceDisconnectPacket) Packet.parseFrom(object.toString());
                                String title = clientBoundForceDisconnectPacket.getClosedID();
                                String reason = clientBoundForceDisconnectPacket.getReason();

                                MeloMod.addMessage((Feature.GENERIC_CONNECTION_LOST + ": " + title + " (" + Feature.GENERIC_REASON + ": " + reason + ")"));

                                closeSocket(CloseReason.CLOSED_BY_SERVER, null);
                                break;
                            case VERSION_CONTROL_RESULT:
                                ClientBoundVersionControlPacket clientBoundVersionControlPacket = (ClientBoundVersionControlPacket) Packet.parseFrom(object.toString());
                                String correct = clientBoundVersionControlPacket.getCorrectVersion();
                                String newLink = clientBoundVersionControlPacket.getUpdateLink();
                                Version.Compatibility stability = clientBoundVersionControlPacket.getCorrect();
                                MeloMod.compatibility = stability;
                                MeloMod.serverVersion = Version.parse(correct);
                                switch (stability) {
                                    case INCOMPATIBLE:
                                        MeloMod.addError(("\n&c" + Feature.ERROR_INCOMPATIBLE_VERSION + " &7" + Feature.GENERIC_CLIENT + ": &4" + MeloMod.VERSION_NEW + "&7, " + Feature.GENERIC_SERVER + ": &2" + MeloMod.serverVersion + "&r"));
                                        MeloMod.addError(("&e" + Feature.GENERIC_UPDATE_LINK + ": &7" + newLink + "&r\n"));

                                        closeSocket(CloseReason.CLIENT_OUT_OF_DATE, null);
                                        break;
                                    case UP_TO_DATE:
                                        MeloMod.addSystemNotification("\n&2" + Feature.GENERIC_UP_TO_DATE + " &7" + Feature.GENERIC_VERSION + ": " + MeloMod.VERSION_NEW + "\n");
                                        break;
                                    case OUTDATED:
                                        MeloMod.addWarn(("\n&c" + Feature.GENERIC_WARNING_OUTDATED_VERSION + " &7" + Feature.GENERIC_CLIENT + ": &4" + MeloMod.VERSION_NEW + "&7, " + Feature.GENERIC_SERVER + ": &2" + MeloMod.serverVersion + "&r"));
                                        MeloMod.addWarn(("&e" + Feature.GENERIC_UPDATE_LINK + ": &7" + newLink + "&r\n"));
                                        break;
                                }
                                if (clientBoundVersionControlPacket.isBanned()) {
                                    BanPacket banPacket = clientBoundVersionControlPacket.getBanReason();
                                    printBan(banPacket);
                                }
                                break;
                            case BAN_PACKET:
                                ClientBoundBanStatus clientBoundBanStatus = (ClientBoundBanStatus) Packet.parseFrom(object.toString());
                                BanPacket banPacket = clientBoundBanStatus.getBanReason();
                                printBan(banPacket);
                                break;
                            case NOTIFY_ONLINE:
                                ClientBoundNotifyOnlinePacket clientBoundNotifyOnlinePacket = (ClientBoundNotifyOnlinePacket) Packet.parseFrom(object.toString());
                                String onlineName = clientBoundNotifyOnlinePacket.getName();
                                MeloMod.addSystemNotification(("&e" + onlineName + " " + Feature.GENERIC_SYSTEM_CONNECTED + "!"));
                                break;
                            case CLOSED_CONNECTION:
                                ClientBoundDisconnectPacket clientBoundDisconnectPacket = (ClientBoundDisconnectPacket) Packet.parseFrom(object.toString());
                                String disconnectName = clientBoundDisconnectPacket.getQuitName();
                                ClientBoundDisconnectPacket.ParsedException exception = clientBoundDisconnectPacket.getData();
                                String disconnectReason = exception.getMessage();
                                ClientBoundDisconnectPacket.ParsedException.ParsedCause parsedCause = exception.getCause();
                                String parsedReason = parsedCause.getMessage();

                                MeloMod.addSystemNotification("&e" + disconnectName + " " + Feature.GENERIC_SYSTEM_DISCONNECTED + "! (" + disconnectReason + ": " + parsedReason + ")");
                                break;
                            case CONNECTED_CLIENTS:
                                ClientBoundConnectedClientsPacket clientBoundConnectedClientsPacket = (ClientBoundConnectedClientsPacket) Packet.parseFrom(object.toString());
                                HashMap<String, LocrawHandler.LocrawInfo> onlinePlayers = clientBoundConnectedClientsPacket.getPlayerList();
                                int page = clientBoundConnectedClientsPacket.getPage();
                                List<String> paginated = StringUtils.paginateOnline(onlinePlayers, 8);
                                MeloMod.addRaw((paginated.get(page - 1)));
                                break;
                            case CHAT_MESSAGE:
                                System.out.println("on god");
                                PacketPlayOutChat packetPlayOutChat = (PacketPlayOutChat) Packet.parseFrom(object.toString());
                                String message = packetPlayOutChat.getContents();
                                String uuid = packetPlayOutChat.getUuid();
                                String messenger = packetPlayOutChat.getName();
                                String targetName = packetPlayOutChat.getTargetName();
                                String dataAddon = packetPlayOutChat.getData() == null ? null : packetPlayOutChat.getData();

                                MeloMod.addDebug("&7" + Feature.GENERIC_DEBUG_MESSAGE + ": " + message);
                                MeloMod.addDebug("&7" + "UUID: " + uuid);
                                MeloMod.addDebug("&7" + Feature.GENERIC_DEBUG_MESSENGER + ": " + messenger);
                                MeloMod.addDebug("&7" + Feature.GENERIC_DEBUG_TARGET + ": " + targetName);
                                VChatComponent dataComponent = new VChatComponent(MeloMod.MessageScheme.DEBUG);
                                dataComponent.add("&7" + Feature.GENERIC_DEBUG_DATA + ": ");
                                String dataAddonParsed = ItemSerializer.INSTANCE.deserializeFromBase64(dataAddon);
                                dataComponent.add("&8(" + Feature.GENERIC_HOVER + ")&r", MeloMod.gson.toJson(dataAddonParsed), (ClickEvent) null, StringUtils.VComponentSettings.INHERIT_NONE);
                                MeloMod.addMessage(dataComponent);
                                if (message == null) {
                                    break;
                                }

                                VChatComponent chatComponent = new VChatComponent(MeloMod.MessageScheme.RAW);
                                MeloMod.addDebug(Feature.GENERIC_DEBUG_CHAT_COMPONENT + ": " + chatComponent);

                                IChatComponent inserted = VChatComponent.insert(new VChatComponent(MeloMod.MessageScheme.RAW).add(message).build(), "<item>", dataAddon);
                                MeloMod.addDebug(Feature.GENERIC_DEBUG_INSERTED + ": " + inserted);

                                chatComponent.add(inserted);
                                MeloMod.addDebug(Feature.GENERIC_DEBUG_INSERTED + " " + Feature.GENERIC_DEBUG_CHAT_COMPONENT + ": " + chatComponent);

                                if (targetName != null) {
                                    if (messenger.equals(targetName)) {
                                        // is relaying own message
                                        MeloMod.addRaw("&8" + Feature.GENERIC_SENT_TO_SELF);
                                    } else {
                                        if (targetName.equalsIgnoreCase(MeloMod.playerName)) {
                                            VChatComponent prefix = new VChatComponent(MeloMod.MessageScheme.RAW).add("&8(" + Feature.GENERIC_PRIVATE_MESSAGE + ") &d" + Feature.GENERIC_FROM + ": &3" + targetName + "&r&7: ");
                                            MeloMod.addPrivateChat(chatComponent.prefix(prefix.build()), messenger);
                                        } else {
                                            VChatComponent prefix = new VChatComponent(MeloMod.MessageScheme.RAW).add("&8(" + Feature.GENERIC_PRIVATE_MESSAGE + ") &d" + Feature.GENERIC_TO + ": &3" + targetName + "&r&7: ");
                                            MeloMod.addPrivateChat(chatComponent.prefix(prefix.build()), targetName);
                                        }
                                    }
                                } else {
                                    if (uuid.startsWith("discord:")) {
                                        VChatComponent prefix = new VChatComponent(MeloMod.MessageScheme.RAW).add("&9[DISCORD] " + messenger + "&r&7: ");
                                        MeloMod.addChat(chatComponent.prefix(prefix.build()));
                                        break;
                                    }
                                    VChatComponent prefix;
                                    if (uuid.equalsIgnoreCase(MeloMod.playerUUID.toString())) {
                                        prefix = new VChatComponent(MeloMod.MessageScheme.RAW).add("&b" + messenger + "&r&7: ");
                                    } else {
                                        prefix = new VChatComponent(MeloMod.MessageScheme.RAW).add("&d" + messenger + "&r&7: ");
                                    }
                                    MeloMod.addChat(chatComponent.prefix(prefix.build()));
                                    if (message.startsWith(ChatConfiguration.chatPrefix)) {
                                        MeloMod.chatEventHandler.executeChatCommand(message, messenger, ApiUtil.ChatChannel.CUSTOM);
                                        // todo update colors and stuff
                                    }
                                }
                                break;
                            case DISCONNECT:
                                PacketPlayOutDisconnect disconnect = (PacketPlayOutDisconnect) Packet.parseFrom(object.toString());
                                String disconnectName2 = disconnect.getName();
                                String disconnectUUID = disconnect.getUuid();
                                if (!disconnectUUID.equalsIgnoreCase(MeloMod.playerUUID.toString())) {
                                    MeloMod.addSystemNotification("&e" + disconnectName2 + " " + Feature.GENERIC_SYSTEM_GAME_DISCONNECT);
                                }
                                break;

                        }

                    }
                } else {
                    if (!data.startsWith("|")) {
                        MeloMod.addWarn("&e" + Feature.GENERIC_WARNING_MALFORMED_PACKET + ": &7" + data);
                    }
                }

                MeloMod.addDebug("&7" + Feature.GENERIC_DEBUG_DATA + ": &8" + MeloMod.gson.toJson(ItemSerializer.INSTANCE.deserializeFromBase64(data)));
            }

            //closeSocket(CloseReason.INVALID_DATA);
        } catch (IOException e) {
            MeloMod.addError("&c" + Feature.ERROR_LOST_CONNECTION + " (" + e.getMessage() + ": " + e.getCause() + ")", e);

            if (closed == null) {
                closeSocket(CloseReason.ERROR, e);
            }
        }
    }

    public void printBan(BanPacket banPacket) {
        MeloMod.addMessage("\n&4[&cMM&4] &c" + Feature.GENERIC_BANNED);
        MeloMod.addMessage("&4[&cMM&4] &c" + Feature.GENERIC_BANNED_2);
        MeloMod.addMessage("\n&e  " + Feature.GENERIC_BAN_INFO_SUMMARY + ": &7" + banPacket.getSummary());
        MeloMod.addMessage("&e  " + Feature.GENERIC_BAN_INFO_REASON + ": &7" + banPacket.getReason());
        MeloMod.addMessage("&e  " + Feature.GENERIC_BAN_INFO_ADMIN + ": &7" + banPacket.getAdmin());
        MeloMod.addMessage("&e  " + Feature.GENERIC_BAN_INFO_DURATION + ": &7" + Utils.prettyTime(banPacket.getDuration()));
        MeloMod.addMessage("&e  " + Feature.GENERIC_BAN_INFO_ISSUED + ": &7" + Utils.another(banPacket.getTimestamp()));
        long expiry = banPacket.getExpiry();
        MeloMod.addMessage("&e  " + Feature.GENERIC_BAN_INFO_EXPIRY + ": &7" + Utils.another(expiry));
        MeloMod.addMessage("&e  " + Feature.GENERIC_BAN_INFO_REMAINING + ": &7" + Utils.prettyTime(expiry - System.currentTimeMillis()) + "\n");
    }

    public void sendPacket(Object packet) {
        if (MainConfiguration.debugMessages) {
            String simpleName = packet.getClass().getCanonicalName() == null ? packet.getClass().toString() : packet.getClass().getCanonicalName();
            int packetID = Packet.from(packet.toString()).getPacketID();
            MeloMod.addDebug("&e" + Feature.GENERIC_DEBUG_SENT_PACKET.toString().replaceAll("\\{1}", "&7" + simpleName + "&e").replaceAll("\\{2}", "&7" + packetID + "&e."));
        }
        printWriter.println(packet.toString());
        printWriter.flush();
    }

    public enum CloseReason {
        CLIENT_OUT_OF_DATE,
        INVALID_DATA,
        ERROR,
        UNKNOWN,
        CLOSED_BY_SERVER
    }
}