package io.github.afamiliarquiet.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public abstract class MawPlayerDataSaver {
    public boolean isBreathing;
}
