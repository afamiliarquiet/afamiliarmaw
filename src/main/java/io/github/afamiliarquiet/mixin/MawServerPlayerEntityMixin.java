package io.github.afamiliarquiet.mixin;

import io.github.afamiliarquiet.entity.BreathProjectileEntity;
import io.github.afamiliarquiet.util.MawBearer;
import io.github.afamiliarquiet.util.MawUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.afamiliarquiet.MagnificentMaw.*;
import static io.github.afamiliarquiet.util.MawUtils.consumeDraconicOmen;
import static io.github.afamiliarquiet.util.MawUtils.getDraconicOmenEntry;

@SuppressWarnings("WrongEntityDataParameterClass") // mmmmmmmm... probably fine..
@Mixin(PlayerEntity.class)
public abstract class MawServerPlayerEntityMixin extends LivingEntity implements MawBearer {
    // copycode copycat! i think the appeal of maw$ is to better insist uniqueness so no conflict?
    // doesn't help the swordchompermixin that's still likely a mess but maybe this is good
    // if it wasn't clear i have no idea what i'm doing with mixins. but this seems to work.
    // also like surely i don't need to mixin to store a boolean. but whatever idk
    // btw i'm trusting these to be false by default. wahoo? hehe
    // now there's logic so it's not just a mixin for a boolean!

    // these probably could (and should? but kinda minor issue) be a byte of flags instead?
    @Unique
    private static final TrackedData<Boolean> magnificent_maw$BREATHING = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Unique
    private static final TrackedData<Boolean> magnificent_maw$METAMORPHOSIZED = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Unique
    private static final TrackedData<Boolean> magnificent_maw$FUELLED = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    protected MawServerPlayerEntityMixin(EntityType<? extends PlayerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("TAIL"), method = "initDataTracker")
    protected void initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(magnificent_maw$BREATHING, false);
        builder.add(magnificent_maw$METAMORPHOSIZED, false);
        builder.add(magnificent_maw$FUELLED, false);
    }

    @Inject(at = @At("TAIL"), method = "writeCustomDataToNbt")
    private void writeCustomDataToNbt(NbtCompound tag, CallbackInfo ci) {
        tag.putBoolean(TF_TAG, magnificent_maw$isMetamorphosized());
        tag.putBoolean(BREATHING_TAG, magnificent_maw$isBreathing());
        tag.putBoolean(FUELLED_TAG, magnificent_maw$isFuelled());
    }

    @Inject(at = @At("TAIL"), method = "readCustomDataFromNbt")
    private void readCustomDataFromNbt(NbtCompound tag, CallbackInfo ci) {
        if (tag.contains(TF_TAG)) {
            magnificent_maw$setMetamorphosized(tag.getBoolean(TF_TAG));
        }
        if (tag.contains(BREATHING_TAG)) {
            magnificent_maw$setBreathing(tag.getBoolean(BREATHING_TAG));
        }
        if (tag.contains(FUELLED_TAG)) {
            magnificent_maw$setFuelled(tag.getBoolean(FUELLED_TAG));
        }
    }

    // weirder to deal with when not directly injectingi. gonna ignore and hope its ok!
//    @Inject(at = @At("TAIL"), method = "copyFrom")
//    private void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
//        if (oldPlayer instanceof MawBearer oldMorpher) {
//            magnificent_maw$setMetamorphosized(oldMorpher.magnificent_maw$isMetamorphosized());
//        }
//    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo ci) {
        // this feels fishy.
        PlayerEntity player = (PlayerEntity)(Object)this;
        World world = player.getWorld();


        if (player.getWorld().isClient()) {
            if (magnificent_maw$isMetamorphosized() && random.nextFloat() < 0.013) {
                world.addParticle(ParticleTypes.FLAME,
                        player.getParticleX(0.5), player.getRandomBodyY(), player.getParticleZ(0.5),
                        (random.nextFloat() - 0.5) * 0.031, random.nextFloat() * 0.031, (random.nextFloat() - 0.5) * 0.031);
            }
        } else {
            if (magnificent_maw$isBreathing() && MawUtils.canBreathe(player)) {
                BreathProjectileEntity breathProjectileEntity = new BreathProjectileEntity(player, world);
                breathProjectileEntity.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, 0.5F, 13F);
                breathProjectileEntity.setPosition(breathProjectileEntity.getPos().add(player.getRotationVector().multiply(0.5)).addRandom(player.getRandom(), 0.013f));
                world.spawnEntity(breathProjectileEntity);

                consumeDraconicOmen(player);
                Vec3d p = player.getPos();
                world.playSound(null, p.x, p.y, p.z, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.2f, (player.getRandom().nextFloat() * 0.13f + 1.0f));
            }
        }
    }

    // you shouldn't do this. don't copy me. idk what i'm doing when i'm overriding stuff in mixins but it's probably bad for compat
    // ok i know a little bit what i'm doing here and yeah don't do this. have a parent mixin target this and override that?
    // i'm not gonna do that though! not unless i have to!! which i may!!!
    // but like yeah this is not the right place for this. whatever! i just want to take advantage of the status effects changed thing..
    @Override
    protected void updatePotionVisibility() {
        super.updatePotionVisibility();

        if (!this.getWorld().isClient()) {
            RegistryEntry<StatusEffect> draconicOmenEntry = getDraconicOmenEntry(this.getWorld());
            boolean hasDraconicOmen = draconicOmenEntry != null && this.hasStatusEffect(draconicOmenEntry);

            if (hasDraconicOmen != magnificent_maw$isFuelled()) {
                magnificent_maw$setFuelled(hasDraconicOmen);
            }
        }
    }

    @Override
    public void magnificent_maw$setMetamorphosized(boolean metamorphosized) {
        this.getDataTracker().set(magnificent_maw$METAMORPHOSIZED, metamorphosized);
    }

    @Override
    public boolean magnificent_maw$isMetamorphosized() {
        return this.getDataTracker().get(magnificent_maw$METAMORPHOSIZED);
    }

    @Override
    public void magnificent_maw$setBreathing(boolean breathing) {
        this.getDataTracker().set(magnificent_maw$BREATHING, breathing);
    }

    @Override
    public boolean magnificent_maw$isBreathing() {
        return this.getDataTracker().get(magnificent_maw$BREATHING);
    }

    @Override
    public void magnificent_maw$setFuelled(boolean fuelled) {
        this.getDataTracker().set(magnificent_maw$FUELLED, fuelled);
    }

    @Override
    public boolean magnificent_maw$isFuelled() {
        return this.getDataTracker().get(magnificent_maw$FUELLED);
    }
}
