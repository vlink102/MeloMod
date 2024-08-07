package me.vlink102.melomod.util.http;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.config.ChatConfig;
import me.vlink102.melomod.config.MeloConfiguration;
import me.vlink102.melomod.events.InternalLocraw;
import me.vlink102.melomod.util.ItemSerializer;
import me.vlink102.melomod.util.StringUtils;
import me.vlink102.melomod.util.VChatComponent;
import me.vlink102.melomod.util.game.Utils;
import me.vlink102.melomod.util.http.packets.*;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentProcessor;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;


public class DataThread extends Thread {
    private final BufferedReader bufferedReader;
    private final PrintWriter printWriter;

    private final Socket socket;
    public static CloseReason closed = null;

    public enum CloseReason {
        CLIENT_OUT_OF_DATE,
        INVALID_DATA,
        ERROR,
        UNKNOWN,
        CLOSED_BY_SERVER
    }

    public void closeSocket(CloseReason reason, Exception exception) {
        try {
            socket.close();
            closed = reason;
            switch (closed) {
                case CLIENT_OUT_OF_DATE:
                    MeloMod.addWarn("&eSocket closed: &cClient is out of date! &8(Hover over prefix for details) [" + closed + "]");
                    break;
                case INVALID_DATA:
                    MeloMod.addError("&eSocket closed: &cClient received invalid data from the server! Please report this. [" + closed + "]", exception);
                    break;
                case ERROR:
                    MeloMod.addError("&eSocket closed: &cClient found unexpected error (Check console) Please report this. [" + closed + "]", exception);
                    break;
                case UNKNOWN:
                    MeloMod.addError("&eSocket closed: &cNo reason provided. Please report this. [" + closed + "]", exception);
                    break;
                case CLOSED_BY_SERVER:
                    MeloMod.addWarn("&eSocket closed: &cThe connection was forcibly terminated by the server. This is NOT a bug. [" + closed + "]");
                    break;
            }
        } catch (IOException e) {
            MeloMod.addError("&4Potentially severe error (Please report this): &c" + e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

    public Socket getSocket() {
        return socket;
    }

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
    public JsonArray onlinePlayers;

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

                                MeloMod.addMessage(("Connection lost: " + title + " (Reason: " + reason + ")"));

                                closeSocket(CloseReason.CLOSED_BY_SERVER, null);
                                break;
                            case VERSION_CONTROL_RESULT:
                                ClientBoundVersionControlPacket clientBoundVersionControlPacket = (ClientBoundVersionControlPacket) Packet.parseFrom(object.toString());
                                String correct = clientBoundVersionControlPacket.getCorrectVersion();
                                String newLink = clientBoundVersionControlPacket.getUpdateLink();
                                Version.VersionStability stability = clientBoundVersionControlPacket.getCorrect();
                                MeloMod.versionStability = stability;
                                MeloMod.serverVersion = Version.parse(correct);
                                switch (stability) {
                                    case INCOMPATIBLE:
                                        MeloMod.addError(("\n&cIncompatible Version! &7Client: &4" + MeloMod.VERSION_NEW + "&7, Server: &2" + MeloMod.serverVersion + "&r"));
                                        MeloMod.addError(("&eUpdate Link: &7" + newLink + "&r\n"));

                                        closeSocket(CloseReason.CLIENT_OUT_OF_DATE, null);
                                        break;
                                    case UP_TO_DATE:
                                        MeloMod.addSystemNotification("\n&2You are up to date! &7Version: " + MeloMod.VERSION_NEW + "\n");
                                        break;
                                    case OUTDATED:
                                        MeloMod.addWarn(("\n&cOutdated Version! &7Client: &4" + MeloMod.VERSION_NEW + "&7, Server: &2" + MeloMod.serverVersion + "&r"));
                                        MeloMod.addWarn(("&eUpdate Link: &7" + newLink + "&r\n"));
                                        break;
                                }
                                if (clientBoundVersionControlPacket.isBanned()) {
                                    Ban ban = clientBoundVersionControlPacket.getBanReason();
                                    printBan(ban);
                                }
                                break;
                            case BAN_PACKET:
                                ClientBoundBanStatus clientBoundBanStatus = (ClientBoundBanStatus) Packet.parseFrom(object.toString());
                                Ban ban = clientBoundBanStatus.getBanReason();
                                printBan(ban);
                                break;
                            case NOTIFY_ONLINE:
                                ClientBoundNotifyOnlinePacket clientBoundNotifyOnlinePacket = (ClientBoundNotifyOnlinePacket) Packet.parseFrom(object.toString());
                                String onlineName = clientBoundNotifyOnlinePacket.getName();
                                MeloMod.addSystemNotification(("&e" + onlineName + " connected!"));
                                break;
                            case CLOSED_CONNECTION:
                                ClientBoundDisconnectPacket clientBoundDisconnectPacket = (ClientBoundDisconnectPacket) Packet.parseFrom(object.toString());
                                String disconnectName = clientBoundDisconnectPacket.getQuitName();
                                ClientBoundDisconnectPacket.ParsedException exception = clientBoundDisconnectPacket.getData();
                                String disconnectReason = exception.getMessage();
                                ClientBoundDisconnectPacket.ParsedException.ParsedCause parsedCause = exception.getCause();
                                String parsedReason = parsedCause.getMessage();

                                MeloMod.addSystemNotification("&e" + disconnectName + " disconnected! (" + disconnectReason + ": " + parsedReason + ")");
                                break;
                            case CONNECTED_CLIENTS:
                                ClientBoundConnectedClientsPacket clientBoundConnectedClientsPacket = (ClientBoundConnectedClientsPacket) Packet.parseFrom(object.toString());
                                HashMap<String, InternalLocraw.LocrawInfo> onlinePlayers = clientBoundConnectedClientsPacket.getPlayerList();
                                int page = clientBoundConnectedClientsPacket.getPage();
                                List<String> paginated = StringUtils.paginateOnline(onlinePlayers, 8);
                                MeloMod.addRaw((paginated.get(page -1)));
                                break;
                            case CHAT_MESSAGE:
                                System.out.println("on god");
                                PacketPlayOutChat packetPlayOutChat = (PacketPlayOutChat) Packet.parseFrom(object.toString());
                                String message = packetPlayOutChat.getContents();
                                String uuid = packetPlayOutChat.getUuid();
                                String messenger = packetPlayOutChat.getName();
                                String targetName = packetPlayOutChat.getTargetName();
                                String dataAddon = packetPlayOutChat.getData() == null ? null : packetPlayOutChat.getData();

                                MeloMod.addDebug("Message: " + message);
                                MeloMod.addDebug("UUID: " + uuid);
                                MeloMod.addDebug("Messenger: " + messenger);
                                MeloMod.addDebug("Target: " + targetName);
                                MeloMod.addDebug("Data Addon: " + dataAddon);
                                if (message == null) {
                                    break;
                                }

                                VChatComponent chatComponent = new VChatComponent(MeloMod.MessageScheme.RAW);

                                IChatComponent inserted = VChatComponent.insert(new VChatComponent(MeloMod.MessageScheme.RAW).add(message).build(), "<item>", dataAddon);

                                chatComponent.add(inserted);

                                if (targetName != null) {
                                    if (messenger.equals(targetName)) {
                                        // is relaying own message
                                        MeloMod.addRaw("&8Your message was lost in the wind...");
                                    } else {
                                        if (targetName.equalsIgnoreCase(MeloMod.playerName)) {
                                            VChatComponent prefix = new VChatComponent(MeloMod.MessageScheme.RAW).add("&8(Private Message) &dFrom: &3" + targetName + "&r&7: ");
                                            MeloMod.addPrivateChat(chatComponent.prefix(prefix.build()), messenger);
                                        } else {
                                            VChatComponent prefix = new VChatComponent(MeloMod.MessageScheme.RAW).add("&8(Private Message) &dTo: &3" + targetName + "&r&7: ");
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
                                    if (message.startsWith(ChatConfig.chatPrefix)) {
                                        MeloMod.chatEvent.executeChatCommand(message, messenger, ApiUtil.ChatChannel.CUSTOM);
                                        // todo update colors and stuff
                                    }
                                }
                                break;
                            case DISCONNECT:
                                PacketPlayOutDisconnect disconnect = (PacketPlayOutDisconnect) Packet.parseFrom(object.toString());
                                String disconnectName2 = disconnect.getName();
                                String disconnectUUID = disconnect.getUuid();
                                if (!disconnectUUID.equalsIgnoreCase(MeloMod.playerUUID.toString())) {
                                    MeloMod.addSystemNotification("&e" + disconnectName2 + " disconnected from the game! &8(Socket is still connected)");
                                }
                                break;

                        }

                    }
                } else {
                    if (!data.startsWith("|")) {
                        MeloMod.addWarn("&eMalformed/Unauthorized Data Packet: &7" + data);
                    }
                }

                MeloMod.addDebug("&7Data: &8" + data);
            }

            //closeSocket(CloseReason.INVALID_DATA);
        } catch (IOException e) {
            MeloMod.addError("Lost connection from server (" + e.getMessage() + ": " + e.getCause() + ")", e);

            if (closed == null) {
                closeSocket(CloseReason.ERROR, e);
            }
        }
    }

    public void printBan(Ban ban) {
        MeloMod.addMessage("\n&4[&cMM&4] &cUh-oh! You are currently banned from " + MeloMod.NAME + "!");
        MeloMod.addMessage("&4[&cMM&4] &cThis only applies to chat.");
        MeloMod.addMessage("\n&e  Summary: &7" + ban.getSummary());
        MeloMod.addMessage("&e  Reason: &7" + ban.getReason());
        MeloMod.addMessage("&e  Banned by: &7" + ban.getAdmin());
        MeloMod.addMessage("&e  Ban Length: &7" + Utils.prettyTime(ban.getDuration()));
        MeloMod.addMessage("&e  Issued: &7" + Utils.another(ban.getTimestamp()));
        long expiry = ban.getExpiry();
        MeloMod.addMessage("&e  Expires: &7" + Utils.another(expiry));
        MeloMod.addMessage("&e  Time Left: &7" + Utils.prettyTime(expiry - System.currentTimeMillis()) + "\n");
    }

    public void sendPacket(Object packet) {
        if (MeloConfiguration.debugMessages) MeloMod.addDebug("&eSuccessfully sent packet &7" + packet.getClass().getSimpleName() + "&e with packet ID &7" + Packet.from(packet.toString()).getPacketID() + "&e.");
        printWriter.println(packet.toString());
        printWriter.flush();
    }
}