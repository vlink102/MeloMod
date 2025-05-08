package me.vlink102.melomod.util.game;

import lombok.Getter;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Getter
@Cancelable
public class ReceivePacketEvent extends Event {
    private final Packet<?> packet;

    public ReceivePacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

}