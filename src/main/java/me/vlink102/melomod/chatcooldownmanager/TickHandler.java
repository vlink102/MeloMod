package me.vlink102.melomod.chatcooldownmanager;

import cc.polyfrost.oneconfig.events.event.Stage;
import cc.polyfrost.oneconfig.events.event.TickEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.config.ChatConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static me.vlink102.melomod.chatcooldownmanager.ServerTracker.hasChatCooldown;
import static me.vlink102.melomod.chatcooldownmanager.ServerTracker.isHypixel;

public class TickHandler
{
    public static LinkedList<String> scheduledCommands = new LinkedList<>();

    private long lastExecutionTime = System.currentTimeMillis();

    public static void addToQueue(String message) {
        MeloMod.addDebug("&eAdded message to queue: &7" + message);
        scheduledCommands.add(message);
    }

    @Subscribe
    public void onEvent(TickEvent tickEvent)
    {
        if (tickEvent.stage == Stage.END) {
            if (System.currentTimeMillis() - lastExecutionTime >= 500 && !scheduledCommands.isEmpty()) {
                lastExecutionTime = System.currentTimeMillis();
                sendChat(scheduledCommands.pop());
            }
        }
    }
    
    @SideOnly (Side.CLIENT)
    public static void sendChat(String message)
    {
        if (message.startsWith("/")) {
            sendCommand(message);
            return;
        }
        if (ChatConfig.chatCaching) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().addToSentMessages(message);
        }
        
        C01PacketChatMessage packet = new C01PacketChatMessage(message);
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(packet);
    }
    
    @SideOnly (Side.CLIENT)
    public static void sendCommand(String command)
    {
        if (ChatConfig.chatCaching) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().addToSentMessages(command);
        }

        int executeResult = ClientCommandHandler.instance.executeCommand(Minecraft.getMinecraft().thePlayer, command);

        if (executeResult != 0) {
            MeloMod.addWarn("&eFailed to execute command: &8Status: " + executeResult);
            return;
        }

        C01PacketChatMessage packet = new C01PacketChatMessage(command);
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(packet);
    }
}