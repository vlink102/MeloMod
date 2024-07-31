package me.vlink102.melomod.util.http;

import cc.polyfrost.oneconfig.libs.checker.units.qual.C;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.config.ChatConfig;
import me.vlink102.melomod.events.ChatEvent;
import me.vlink102.melomod.events.InternalLocraw;
import me.vlink102.melomod.util.StringUtils;
import me.vlink102.melomod.util.game.Utils;
import me.vlink102.melomod.util.http.packets.*;

import java.io.*;
import java.net.Socket;
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

    public void closeSocket(CloseReason... reason) {
        try {
            socket.close();
            if (reason.length==0) {
                closed = CloseReason.UNKNOWN;
            } else {
                closed = reason[0];
            }
            System.out.println("Socket Closed: " + closed);
        } catch (IOException e) {
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
            e.printStackTrace();
            closeSocket(CloseReason.ERROR);
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

                                closeSocket(CloseReason.CLOSED_BY_SERVER);
                                break;
                            case VERSION_CONTROL_RESULT:
                                ClientBoundVersionControlPacket clientBoundVersionControlPacket = (ClientBoundVersionControlPacket) Packet.parseFrom(object.toString());
                                String correct = clientBoundVersionControlPacket.getCorrectVersion();
                                String newLink = clientBoundVersionControlPacket.getUpdateLink();
                                switch (clientBoundVersionControlPacket.getCorrect()) {
                                    case INCOMPATIBLE:
                                        MeloMod.addMessage(("\n§4[§cMM§4] §cIncompatible Version! §7Client: §4" + MeloMod.VERSION_NEW + "§7, Server: §2" + Version.parse(correct) + "§r"));
                                        MeloMod.addMessage(("§eUpdate Link: §7" + newLink + "§r\n"));

                                        closeSocket(CloseReason.CLIENT_OUT_OF_DATE);
                                        break;
                                    case UP_TO_DATE:
                                        MeloMod.addMessage("\n§2[§aMM§2] §2You are up to date! §7Version: " + MeloMod.VERSION_NEW + "\n");
                                        break;
                                    case OUTDATED:
                                        MeloMod.addMessage(("\n§4[§cMM§4] §cOutdated Version! §7Client: §4" + MeloMod.VERSION_NEW + "§7, Server: §2" + Version.parse(correct) + "§r"));
                                        MeloMod.addMessage(("§eUpdate Link: §7" + newLink + "§r\n"));
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
                                MeloMod.addMessage(("§6[§eMM§6] §e" + onlineName + " connected!"));
                                break;
                            case CLOSED_CONNECTION:
                                ClientBoundDisconnectPacket clientBoundDisconnectPacket = (ClientBoundDisconnectPacket) Packet.parseFrom(object.toString());
                                String disconnectName = clientBoundDisconnectPacket.getQuitName();
                                ClientBoundDisconnectPacket.ParsedException exception = clientBoundDisconnectPacket.getData();
                                String disconnectReason = exception.getMessage();
                                ClientBoundDisconnectPacket.ParsedException.ParsedCause parsedCause = exception.getCause();
                                String parsedReason = parsedCause.getMessage();

                                MeloMod.addMessage("§6[§eMM§6] §e" + disconnectName + " disconnected! (" + disconnectReason + ": " + parsedReason + ")");
                                break;
                            case CONNECTED_CLIENTS:
                                ClientBoundConnectedClientsPacket clientBoundConnectedClientsPacket = (ClientBoundConnectedClientsPacket) Packet.parseFrom(object.toString());
                                HashMap<String, InternalLocraw.LocrawInfo> onlinePlayers = clientBoundConnectedClientsPacket.getPlayerList();
                                System.out.println(onlinePlayers);
                                int page = clientBoundConnectedClientsPacket.getPage();
                                List<String> paginated = StringUtils.paginateOnline(onlinePlayers, 8);
                                MeloMod.addMessage((paginated.get(page -1)));
                                break;
                            case CHAT_MESSAGE:
                                PacketPlayOutChat packetPlayOutChat = (PacketPlayOutChat) Packet.parseFrom(object.toString());
                                String message = packetPlayOutChat.getContents();
                                String uuid = packetPlayOutChat.getUuid();
                                String messenger = packetPlayOutChat.getName();
                                String targetName = packetPlayOutChat.getTargetName();

                                if (targetName != null) {
                                    if (uuid.equalsIgnoreCase(MeloMod.playerUUID.toString())) {
                                        // is relaying own message
                                        MeloMod.addMessage("§5[§dMM§5] §8(Private Message) §dTo: §3" + targetName + "§r§7: " + message);
                                    } else {
                                        MeloMod.addMessage("§5[§dMM§5] §8(Private Message) §dFrom: §3" + messenger + "§r§7: " + message);
                                    }
                                } else {
                                    if (uuid.equalsIgnoreCase(MeloMod.playerUUID.toString())) {
                                        MeloMod.addMessage(("§d[§bMM§d] §b" + messenger + "§r§7: " + message));
                                    } else {
                                        MeloMod.addMessage(("§d[§bMM§d] §d" + messenger + "§r§7: " + message));
                                    }
                                    if (message.startsWith(ChatConfig.chatPrefix)) {
                                        MeloMod.chatEvent.executeChatCommand(message, messenger, ApiUtil.ChatChannel.CUSTOM);
                                    }
                                }
                                break;
                            case DISCONNECT:
                                PacketPlayOutDisconnect disconnect = (PacketPlayOutDisconnect) Packet.parseFrom(object.toString());
                                String disconnectName2 = disconnect.getName();
                                String disconnectUUID = disconnect.getUuid();
                                if (!disconnectUUID.equalsIgnoreCase(MeloMod.playerUUID.toString())) {
                                    MeloMod.addMessage("§6[§eMM§6] §e" + disconnectName2 + " disconnected from the game! §8(Socket is still connected)");
                                }

                                break;

                        }

                    }
                } else {
                    if (!data.startsWith("|")) {
                        MeloMod.addMessage("§eMalformed/Unauthorized Data Packet: §7" + data);
                    }
                }

                System.out.println("Data: " + data);
            }

            //closeSocket(CloseReason.INVALID_DATA);
        } catch (IOException e) {
            MeloMod.addMessage("§4[§cMM§4]§r §cERROR: Lost connection from server (" + e.getMessage() + ": " + e.getCause() + ")");
            e.printStackTrace();
            if (closed == null) {
                closeSocket(CloseReason.ERROR);
            }
        }
    }

    public void printBan(Ban ban) {
        MeloMod.addMessage("\n§4[§cMM§4] §cUh-oh! You are currently banned from " + MeloMod.NAME + "!");
        MeloMod.addMessage("§4[§cMM§4] §cThis only applies to chat.");
        MeloMod.addMessage("\n§e  Summary: §7" + ban.getSummary());
        MeloMod.addMessage("§e  Reason: §7" + ban.getReason());
        MeloMod.addMessage("§e  Banned by: §7" + ban.getAdmin());
        MeloMod.addMessage("§e  Ban Length: §7" + Utils.prettyTime(ban.getDuration()));
        MeloMod.addMessage("§e  Issued: §7" + Utils.another(ban.getTimestamp()));
        long expiry = ban.getExpiry();
        MeloMod.addMessage("§e  Expires: §7" + Utils.another(expiry));
        MeloMod.addMessage("§e  Time Left: §7" + Utils.prettyTime(expiry - System.currentTimeMillis()) + "\n");
    }

    public void sendPacket(Object packet) {
        printWriter.println(packet.toString());
        printWriter.flush();
    }
}