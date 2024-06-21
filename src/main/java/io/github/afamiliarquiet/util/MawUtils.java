package io.github.afamiliarquiet.util;

import io.github.afamiliarquiet.MagnificentMaw;
import io.github.afamiliarquiet.entity.MawEntities;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MawUtils {
    public static boolean canBreathe(LivingEntity entity) {
        return isDraconicTfed(entity) || canBreatheNaturally(entity);
    }

    public static boolean canBreatheNaturally(LivingEntity entity) {
        return isFuelled(entity) && (entity.isOnFire() || isHoldingIgnition(entity));
    }

    public static boolean isHoldingIgnition(LivingEntity entity) {
        return entity.getMainHandStack().isIn(MagnificentMaw.FIERY_ITEMS)
                || entity.getOffHandStack().isIn(MagnificentMaw.FIERY_ITEMS)
                || EnchantmentHelper.hasAnyEnchantmentsIn(entity.getMainHandStack(), MagnificentMaw.FIERY_ENCHANTMENTS)
                || EnchantmentHelper.hasAnyEnchantmentsIn(entity.getOffHandStack(), MagnificentMaw.FIERY_ENCHANTMENTS);
    }

    public static boolean isFuelled(LivingEntity entity) {
        return (entity instanceof MawBearer mawBearer && mawBearer.magnificent_maw$isFuelled());
    }

    public static void consumeDraconicOmen(LivingEntity entity) {
        RegistryEntry<StatusEffect> draconicOmenEntry = getDraconicOmenEntry(entity.getWorld());
        if (draconicOmenEntry == null) {
            return;
        }

        StatusEffectInstance oldInstance = entity.getStatusEffect(draconicOmenEntry);
        if (oldInstance != null) {
            entity.removeStatusEffect(draconicOmenEntry);
            if (oldInstance.getDuration() > 31) {
                entity.addStatusEffect(new StatusEffectInstance(oldInstance.getEffectType(), oldInstance.getDuration() - 31, oldInstance.getAmplifier(), oldInstance.isAmbient(), oldInstance.shouldShowParticles(), oldInstance.shouldShowIcon()));
            }
        }
    }

    public static RegistryEntry.Reference<StatusEffect> getDraconicOmenEntry(World world) {
        return world.getRegistryManager().get(RegistryKeys.STATUS_EFFECT).getEntry(MawEntities.DRACONIC_OMEN_STATUS_EFFECT_ID)
                .orElse(null);
    }

    // todo - maybe make my own soundevents for subtitle purposes
    public static void applyDraconicTf(LivingEntity entity) {
        if (entity instanceof MawBearer morpher) {
            morpher.magnificent_maw$setMetamorphosized(true);
            poof(entity, ParticleTypes.FLAME);
            entity.removeStatusEffect(getDraconicOmenEntry(entity.getWorld()));

            if (entity instanceof PlayerEntity player) {
                Vec3d p = entity.getPos();
                entity.getWorld().playSound(null, p.x, p.y, p.z, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.PLAYERS, 0.5f, 1.3f);
                player.playSoundToPlayer(SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.PLAYERS, 0.1f, 1.3f);
                player.sendMessage(Text.translatable("message.magnificent_maw.apply_tf").withColor(0x4fe7ac), true);
            }
        }
    }

    public static void stripDraconicTf(LivingEntity entity) {
        if (entity instanceof MawBearer morpher && morpher.magnificent_maw$isMetamorphosized()) {
            morpher.magnificent_maw$setMetamorphosized(false);
            poof(entity);

            if (entity instanceof PlayerEntity player) {
                Vec3d p = entity.getPos();
                entity.getWorld().playSound(null, p.x, p.y, p.z, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.PLAYERS, 0.5f, 0.7f);
                player.playSoundToPlayer(SoundEvents.BLOCK_ENDER_CHEST_OPEN, SoundCategory.PLAYERS, 0.7f, 0.7f);
                player.sendMessage(Text.translatable("message.magnificent_maw.strip_tf").withColor(0x4fe7ac), true);
            }
        }
    }

    // todo - make this use confettilib and throw confetti instead because tf is party time
    public static void poof(LivingEntity entity, @Nullable ParticleEffect extraParticle) {
        if (entity.getWorld() instanceof ServerWorld world) {
            Box size = entity.getDimensions(entity.getPose()).getBoxAt(0,0,0);

            world.spawnParticles(ParticleTypes.GUST,
                    entity.offsetX(0.5), entity.getBodyY(0.5), entity.offsetZ(0.5),
                    6, size.getLengthX()*0.75, size.getLengthY()*0.5, size.getLengthZ()*0.75, 0);

            if (extraParticle != null) {
                world.spawnParticles(extraParticle,
                        entity.getX(), entity.getBodyY(0.5), entity.getZ(),
                        7, size.getLengthX()*0.75, size.getLengthY()*0.5, size.getLengthZ()*0.75, 0);
            }
        }
    }

    public static void poof(LivingEntity entity) {
        poof(entity, null);
    }

    public static boolean isDraconicTfed(LivingEntity entity) {
        if (entity instanceof MawBearer morpher) {
            return morpher.magnificent_maw$isMetamorphosized();
        } else {
            return false;
        }
    }
}
