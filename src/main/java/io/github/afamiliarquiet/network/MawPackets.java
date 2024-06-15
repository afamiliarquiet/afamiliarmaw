package io.github.afamiliarquiet.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class MawPackets {

    public static void registerC2SPayloads() {
        PayloadTypeRegistry.playC2S().register(BreathPayload.ID, BreathPayload.CODEC);
    }

    public static void registerC2SReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(BreathPayload.ID, BreathPayload::receive);
    }

    public static void registerS2CPayloads() {

    }

    public static void registerS2CReceivers() {

    }
}
