package me.vlink102.melomod.util.http;

public interface PacketParser {
    Packet parse(String json);
    Packet.PacketID bind();
}
