package me.vlink102.melomod.events.chatcooldownmanager;

import me.vlink102.melomod.config.ChatConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

import static me.vlink102.melomod.events.chatcooldownmanager.ServerTracker.hasChatCooldown;
import static me.vlink102.melomod.events.chatcooldownmanager.ServerTracker.isHypixel;

public class TickHandler
{
    public static List<String> scheduledCommands = new ArrayList<>();
    public static List<String> scheduledChat = new ArrayList<>();
    
    public static int ticksSinceLastCommand = 11;
    public static int ticksSinceLastChat = 71;

    public static void addToQueue(String message) {
        if (message.startsWith("/pc") || message.startsWith("/pchat") || message.startsWith("/partychat")) {
            scheduledChat.add(message);
        } else {
            if (message.startsWith("/")) {
                scheduledCommands.add(message);
            } else {
                scheduledChat.add(message);
            }
        }

    }
    
    @SideOnly (Side.CLIENT)
    @SubscribeEvent (priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onEvent(TickEvent.ClientTickEvent tickEvent)
    {
        if (isHypixel && hasChatCooldown) {
            if (ticksSinceLastChat >= 160 && !scheduledChat.isEmpty()) {
                sendChat(scheduledChat.remove(0));

            }
        } else if(!scheduledChat.isEmpty()) {
            if (ticksSinceLastChat >= 20) {
                sendChat(scheduledChat.remove(0));
            }
        }

        if (isHypixel)
        {
            if (ticksSinceLastCommand > 20 && !scheduledCommands.isEmpty())
            {
                sendCommand(scheduledCommands.remove(0));

            }
        } else if (!scheduledCommands.isEmpty()) {
            if (ticksSinceLastCommand >= 20) {
                sendCommand(scheduledCommands.remove(0));
            }

        }

        if (ticksSinceLastChat < 160)
        {
            ticksSinceLastChat++;
        }

        if (ticksSinceLastCommand < 20)
        {
            ticksSinceLastCommand++;
        }

    }
    
    @SideOnly (Side.CLIENT)
    public static void sendChat(String message)
    {
        if (ChatConfig.chatCaching) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().addToSentMessages(message);
        }
        
        //C01PacketChatMessage packet = new C01PacketChatMessage(message);
        Minecraft.getMinecraft().thePlayer.sendChatMessage(message);
        ticksSinceLastChat = 0;
    }
    
    @SideOnly (Side.CLIENT)
    public static void sendCommand(String command)
    {
        if (ChatConfig.chatCaching) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().addToSentMessages(command);
        }
    
        if (ClientCommandHandler.instance.executeCommand(Minecraft.getMinecraft().thePlayer, command) != 0) {
            return;
        }

        Minecraft.getMinecraft().thePlayer.sendChatMessage(command);
        ticksSinceLastCommand = 0;
    }
}