package io.github.afamiliarquiet;

import io.github.afamiliarquiet.network.BreathPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

public class BreathHandler {
    private static BreathHandler instance = null;
    private boolean lastBreathing;

    public BreathHandler(boolean lastBreathing) {
        this.lastBreathing = lastBreathing;
    }

    public static BreathHandler init() {
        if (instance == null) {
            BreathHandler.instance = new BreathHandler(false);
        }

        return BreathHandler.instance;
    }

    public void tick(MinecraftClient client, boolean breathing) {
//        while (breathKey.wasPressed()) {
//            client.player.sendMessage(Text.literal("Breath!!! waaah"), false);
//        }
//        if (client.world == null || client.getNetworkHandler() == null) {
//            return;
//        }

        if (breathing && ClientPlayNetworking.canSend(BreathPayload.ID)) {
            BreathPayload.Mode mode = breathing ? BreathPayload.Mode.START_BREATHING : BreathPayload.Mode.STOP_BREATHING;

            ClientPlayNetworking.send(new BreathPayload(mode));
        }

        lastBreathing = breathing;
    }
}
