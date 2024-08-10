package me.vlink102.melomod.util.http;

import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.util.http.packets.ServerBoundNotifyOnlinePacket;
import me.vlink102.melomod.util.http.packets.ServerBoundVersionControlPacket;
import me.vlink102.melomod.util.translation.Feature;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CommunicationHandler {

    public static final List<DataThread.CloseReason> reasons = new ArrayList<DataThread.CloseReason>() {{
        this.add(DataThread.CloseReason.INVALID_DATA);
        this.add(DataThread.CloseReason.CLOSED_BY_SERVER);
        this.add(DataThread.CloseReason.ERROR);
        this.add(DataThread.CloseReason.UNKNOWN);
    }};
    public static DataThread thread = null;

    public static void establishConnection(UUID uuid, String prettyName) {
        try {
            InetAddress address = InetAddress.getByName("api.vlands.co.uk");
            Socket socket = new Socket(address.getHostName(), 6849);

            thread = new DataThread(socket);
            thread.start();

            thread.sendPacket(new ServerBoundNotifyOnlinePacket(uuid.toString(), prettyName));
            thread.sendPacket(new ServerBoundVersionControlPacket(uuid.toString()));

        } catch (Exception e) {
            MeloMod.addError("&c" + Feature.ERROR_COULD_NOT_CONNECT_TO_SERVER + " &7(" + e.getMessage() + ": " + e.getCause() + ")", e);
        }
    }

    public void beginKeepAlive(UUID uuid, String name) {
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
        executorService.scheduleAtFixedRate(() -> {
            if (thread == null || reasons.contains(DataThread.closed)) {
                establishConnection(uuid, name);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
}
