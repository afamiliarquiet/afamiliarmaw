package io.github.afamiliarquiet;

import io.github.afamiliarquiet.network.BreathPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicBoolean;

public class MawKeybinds {
    public static final KeyBinding breatheKey = new KeyBinding(
            "key.magnificent_maw.breathe",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "category.magnificent_maw.maw"
    );

    public static void register() {
        KeyBindingHelper.registerKeyBinding(breatheKey);

        AtomicBoolean breathingLastTick = new AtomicBoolean(false);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean breathingNow = breatheKey.isPressed();

            if (ClientPlayNetworking.canSend(BreathPacket.ID) && breathingNow != breathingLastTick.get()) {
                BreathPacket.Mode mode = breathingNow ? BreathPacket.Mode.START_BREATHING : BreathPacket.Mode.STOP_BREATHING;

                ClientPlayNetworking.send(new BreathPacket(mode));
            }

            breathingLastTick.set(breathingNow);
        });
    }
}
