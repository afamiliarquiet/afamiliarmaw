package io.github.afamiliarquiet.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class MawPackets {

    public static void registerC2SPayloads() {
        PayloadTypeRegistry.playC2S().register(BreathPacket.ID, BreathPacket.CODEC);
    }

    public static void registerC2SReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(BreathPacket.ID, BreathPacket::receive);
    }
}
