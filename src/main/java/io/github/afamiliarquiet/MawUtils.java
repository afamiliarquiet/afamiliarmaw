package io.github.afamiliarquiet;

import io.github.afamiliarquiet.entity.MawEntities;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

public class MawUtils {
    public static boolean canBreathe(PlayerEntity player) {
        // checking status effect is annoying, i don't like registryentry (so i'm avoiding it)
        return (player.isOnFire()
                || player.getMainHandStack().isIn(AFamiliarMaw.FIERY_ITEMS)
                || EnchantmentHelper.hasAnyEnchantmentsIn(player.getMainHandStack(), AFamiliarMaw.FIERY_ENCHANTMENTS))
                && player.hasStatusEffect(player.getWorld().getRegistryManager().get(RegistryKeys.STATUS_EFFECT).getEntry(MawEntities.PYREXIA_STATUS_EFFECT_ID).get());
    }
}
