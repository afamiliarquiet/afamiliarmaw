package io.github.afamiliarquiet.entity.effect;

import io.github.afamiliarquiet.AFamiliarMaw;
import io.github.afamiliarquiet.entity.MawEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class PyrexiaStatusEffect extends StatusEffect {
    public PyrexiaStatusEffect() {
        super(StatusEffectCategory.NEUTRAL, 0xfcfcb3);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.getWorld().isClient()) {
            entity.addCommandTag(AFamiliarMaw.TF_TAG);
            entity.removeStatusEffect(entity.getWorld().getRegistryManager().get(RegistryKeys.STATUS_EFFECT).getEntry(MawEntities.PYREXIA_STATUS_EFFECT_ID).get());
            if (entity instanceof PlayerEntity player) {
                player.playSoundToPlayer(SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.PLAYERS, 0.2f, 1.3f);
                entity.getWorld().playSound(null, entity.getBlockPos(), SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.PLAYERS, 0.5f, 1.3f);
            }
        }
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration >= 216000;
    }
}
