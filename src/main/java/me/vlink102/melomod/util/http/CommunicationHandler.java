package me.vlink102.melomod.util.http;

import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.events.SkyblockRunnable;
import me.vlink102.melomod.util.http.packets.ServerBoundNotifyOnlinePacket;
import me.vlink102.melomod.util.http.packets.ServerBoundVersionControlPacket;
import me.vlink102.melomod.util.translation.Feature;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommunicationHandler {
    private final MeloMod mod;

    public CommunicationHandler(MeloMod mod) {
        this.mod = mod;
    }

    public static final List<DataThread.CloseReason> reasons = new ArrayList<DataThread.CloseReason>() {{
        this.add(DataThread.CloseReason.INVALID_DATA);
        this.add(DataThread.CloseReason.CLOSED_BY_SERVER);
        this.add(DataThread.CloseReason.ERROR);
        this.add(DataThread.CloseReason.UNKNOWN);
    }};
    public static DataThread thread = null;
    public static LocalThread localThread = null;

    public static void establishInternal() {
        MeloMod.runAsync(() -> {
            try {
                InetAddress address = InetAddress.getByName("localhost");
                Socket socket = new Socket(address.getHostName(), 12345);

                localThread = new LocalThread(socket);
                localThread.start();

            } catch (IOException e) {
                MeloMod.addError("&cCould not connect to local server. &7(" + e.getMessage() + ": " + e.getCause() + ")", e);
                if (localThread != null) {
                    localThread.closeSocket(e);

                    localThread = null;
                }
            }
        });
    }

    public static void establishConnection(UUID uuid, String prettyName, String endpoint) {
        MeloMod.runAsync(() -> {
            try {
                InetAddress address = InetAddress.getByName(endpoint);
                Socket socket = new Socket(address.getHostName(), 6849);

                thread = new DataThread(socket);
                thread.start();

                thread.sendPacket(new ServerBoundNotifyOnlinePacket(uuid.toString(), prettyName));
                thread.sendPacket(new ServerBoundVersionControlPacket(uuid.toString()));

            } catch (Exception e) {
                MeloMod.addError("&c" + Feature.ERROR_COULD_NOT_CONNECT_TO_SERVER + " &7(" + e.getMessage() + ": " + e.getCause() + ")", e);
                if (thread != null) {
                    thread.closeSocket(DataThread.CloseReason.ERROR, e);

                    thread = null;
                }
            }
        });
    }

    public void beginKeepAlive(UUID uuid, String name, String endpoint) {
        mod.getNewScheduler().scheduleDelayedTask(new SkyblockRunnable() {
            @Override
            public void run() {
                if (thread == null || reasons.contains(DataThread.closed)) {
                    establishConnection(uuid, name, endpoint);
                }
            }
        }, 5 * 20);
    }

    public void beginLocalKeepAlive() {
        mod.getNewScheduler().scheduleDelayedTask(new SkyblockRunnable() {
            @Override
            public void run() {
                if (localThread == null) {
                    establishInternal();
                }
            }
        }, 5 * 20);
    }
}
