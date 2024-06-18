package io.github.afamiliarquiet;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class MawKeybinds {
    public static final KeyBinding breatheKey = new KeyBinding(
            "key.afamiliarmaw.breathe",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "category.afamiliarmaw.maw"
    );

    public static void register() {
        KeyBindingHelper.registerKeyBinding(breatheKey);
    }
}
