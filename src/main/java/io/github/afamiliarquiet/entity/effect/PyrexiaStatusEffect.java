package io.github.afamiliarquiet.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

import static io.github.afamiliarquiet.MawUtils.applyPyrexiaTf;

public class PyrexiaStatusEffect extends StatusEffect {
    public PyrexiaStatusEffect() {
        super(StatusEffectCategory.NEUTRAL, 0xfcfcb3);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.getWorld().isClient()) {
            applyPyrexiaTf(entity);
        }
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration >= 36000;
    }
}
