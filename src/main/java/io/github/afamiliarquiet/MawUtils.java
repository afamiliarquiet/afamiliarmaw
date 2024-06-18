package io.github.afamiliarquiet;

import io.github.afamiliarquiet.entity.MawEntities;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class MawUtils {
    public static boolean canBreathe(PlayerEntity player) {
        RegistryEntry<StatusEffect> pyrexiaEntry = getPyrexiaEntry(player.getWorld());

        return isPyrexiaTfed(player)
                || (pyrexiaEntry != null && player.hasStatusEffect(pyrexiaEntry)
                    && (player.isOnFire()
                        || player.getMainHandStack().isIn(AFamiliarMaw.FIERY_ITEMS)
                        || player.getOffHandStack().isIn(AFamiliarMaw.FIERY_ITEMS)
                        || EnchantmentHelper.hasAnyEnchantmentsIn(player.getMainHandStack(), AFamiliarMaw.FIERY_ENCHANTMENTS)
                        || EnchantmentHelper.hasAnyEnchantmentsIn(player.getOffHandStack(), AFamiliarMaw.FIERY_ENCHANTMENTS)
                    )
                );
    }

    public static void consumePyrexia(PlayerEntity player) {
        RegistryEntry<StatusEffect> pyrexiaEntry = getPyrexiaEntry(player.getWorld());
        if (pyrexiaEntry == null) {
            return;
        }

        StatusEffectInstance oldInstance = player.getStatusEffect(pyrexiaEntry);
        if (oldInstance != null) {
            player.removeStatusEffect(pyrexiaEntry);
            if (oldInstance.getDuration() > 31) {
                player.addStatusEffect(new StatusEffectInstance(oldInstance.getEffectType(), oldInstance.getDuration() - 31, oldInstance.getAmplifier(), oldInstance.isAmbient(), oldInstance.shouldShowParticles(), oldInstance.shouldShowIcon()));
            }
        }
    }

    public static RegistryEntry.Reference<StatusEffect> getPyrexiaEntry(World world) {
        return world.getRegistryManager().get(RegistryKeys.STATUS_EFFECT).getEntry(MawEntities.DRACONIC_OMEN_STATUS_EFFECT_ID)
                .orElse(null);
    }

    // todo - maybe make my own soundevents for subtitle purposes
    public static void applyPyrexiaTf(LivingEntity entity) {
        entity.addCommandTag(AFamiliarMaw.TF_TAG);
        entity.removeStatusEffect(getPyrexiaEntry(entity.getWorld()));
        if (entity instanceof PlayerEntity player) {
            player.playSoundToPlayer(SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.PLAYERS, 0.1f, 1.3f);
            entity.getWorld().playSound(null, entity.getBlockPos(), SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.PLAYERS, 0.5f, 1.3f);
            player.sendMessage(Text.translatable("message.afamiliarmaw.tf").withColor(0xFFAA00), true);
        }
    }

    public static void stripPyrexiaTf(LivingEntity entity) {
        // condition here is also removing command tag
        if (entity.removeCommandTag(AFamiliarMaw.TF_TAG) && entity instanceof PlayerEntity player) {
            player.playSoundToPlayer(SoundEvents.BLOCK_ENDER_CHEST_OPEN, SoundCategory.PLAYERS, 0.7f, 0.7f);
            entity.getWorld().playSound(null, entity.getBlockPos(), SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.PLAYERS, 0.5f, 0.7f);
            player.sendMessage(Text.translatable("message.afamiliarmaw.striptf").withColor(0xAAAAAA), true);
        }
    }

    public static boolean isPyrexiaTfed(LivingEntity entity) {
        return entity.getCommandTags().contains(AFamiliarMaw.TF_TAG);
    }
}
