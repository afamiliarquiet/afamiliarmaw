package io.github.afamiliarquiet.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

import static io.github.afamiliarquiet.util.MawUtils.applyDraconicTf;

public class DraconicOmenStatusEffect extends StatusEffect {
    public DraconicOmenStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xfcfcb3);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.getWorld().isClient()) {
            applyDraconicTf(entity);
        }
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration >= 36000;
    }
}
