package io.github.afamiliarquiet;

import io.github.afamiliarquiet.entity.MawEntities;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class MawUtils {
    public static boolean canBreathe(PlayerEntity player) {
        // checking status effect is annoying, i don't like registryentry (so i'm avoiding it)
        return isPyrexiaTfed(player)
                || (player.hasStatusEffect(player.getWorld().getRegistryManager().get(RegistryKeys.STATUS_EFFECT).getEntry(MawEntities.PYREXIA_STATUS_EFFECT_ID).get())
                    && (player.isOnFire()
                        || player.getMainHandStack().isIn(AFamiliarMaw.FIERY_ITEMS)
                        || player.getOffHandStack().isIn(AFamiliarMaw.FIERY_ITEMS)
                        || EnchantmentHelper.hasAnyEnchantmentsIn(player.getMainHandStack(), AFamiliarMaw.FIERY_ENCHANTMENTS)
                        || EnchantmentHelper.hasAnyEnchantmentsIn(player.getOffHandStack(), AFamiliarMaw.FIERY_ENCHANTMENTS)
                    )
                );
    }

    // todo - maybe make my own soundevents for subtitle purposes
    public static void applyPyrexiaTf(LivingEntity entity) {
        entity.addCommandTag(AFamiliarMaw.TF_TAG);
        entity.removeStatusEffect(entity.getWorld().getRegistryManager().get(RegistryKeys.STATUS_EFFECT).getEntry(MawEntities.PYREXIA_STATUS_EFFECT_ID).get());
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
