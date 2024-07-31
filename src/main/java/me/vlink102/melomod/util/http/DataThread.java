package me.vlink102.melomod.util.http;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.vlink102.melomod.MeloMod;
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
                                    MeloMod.addMessage("\n§4[§cMM§4] §cUh-oh! You are currently banned from " + MeloMod.NAME + "!");
                                    MeloMod.addMessage("§4[§cMM§4] §cThis only applies to chat.");
                                    Ban ban = clientBoundVersionControlPacket.getBanReason();
                                    MeloMod.addMessage("\n§e  Summary: §7" + ban.getSummary());
                                    MeloMod.addMessage("§e  Reason: §7" + ban.getReason());
                                    MeloMod.addMessage("§e  Banned by: §7" + ban.getAdmin());
                                    MeloMod.addMessage("§e  Ban Length: §7" + Utils.prettyTime(ban.getDuration()));
                                    MeloMod.addMessage("§e  Issued: §7" + Utils.another(ban.getTimestamp()));
                                    long expiry = ban.getExpiry();
                                    MeloMod.addMessage("§e  Expires: §7" + Utils.another(expiry));
                                    MeloMod.addMessage("§e  Time Left: §7" + Utils.prettyTime(expiry - System.currentTimeMillis()) + "\n");
                                }
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
                                List<String> onlinePlayers = clientBoundConnectedClientsPacket.getPlayerList();
                                int page = clientBoundConnectedClientsPacket.getPage();
                                List<String> paginated = StringUtils.paginateOnline(onlinePlayers, 8);
                                MeloMod.addMessage((paginated.get(page)));
                                break;
                            case CHAT_MESSAGE:
                                PacketPlayOutChat packetPlayOutChat = (PacketPlayOutChat) Packet.parseFrom(object.toString());
                                String message = packetPlayOutChat.getContents();
                                String uuid = packetPlayOutChat.getUuid();
                                String messenger = packetPlayOutChat.getName();
                                if (!uuid.equalsIgnoreCase(MeloMod.playerUUID.toString())) {
                                    MeloMod.addMessage(("§d[§bMM§d] §d" + messenger + "§7: " + message));
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

    public void sendPacket(Object packet) {
        printWriter.println(packet.toString());
        printWriter.flush();
    }
}