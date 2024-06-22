package io.github.afamiliarquiet.mixin;

import io.github.afamiliarquiet.MagnificentMaw;
import io.github.afamiliarquiet.entity.BreathProjectileEntity;
import io.github.afamiliarquiet.util.MawBearer;
import io.github.afamiliarquiet.util.MawUtils;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static io.github.afamiliarquiet.MagnificentMaw.*;
import static io.github.afamiliarquiet.util.MawUtils.*;

@SuppressWarnings("WrongEntityDataParameterClass") // mmmmmmmm... probably fine..
@Mixin(LivingEntity.class)
public abstract class MawLivingEntityMixin extends Entity implements MawBearer {
    // copycode copycat! i think the appeal of maw$ is to better insist uniqueness so no conflict?
    // if it wasn't clear i have no idea what i'm doing with mixins. but this seems to work.
    // also like surely i don't need to mixin to store a boolean. but whatever idk
    // now there's logic so it's not just a mixin for a boolean!

    @Shadow
    private boolean effectsChanged;

    // these probably could (and should? but kinda minor issue) be a byte of flags instead?
    @Unique
    private static final TrackedData<Boolean> magnificent_maw$BREATHING = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Unique
    private static final TrackedData<Boolean> magnificent_maw$METAMORPHOSIZED = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Unique
    private static final TrackedData<Boolean> magnificent_maw$FUELLED = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public MawLivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
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

    // weirder to deal with when not directly injecting. gonna ignore and hope its ok! maybe not great, but ok
//    @Inject(at = @At("TAIL"), method = "copyFrom")
//    private void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
//        if (oldPlayer instanceof MawBearer oldMorpher) {
//            magnificent_maw$setMetamorphosized(oldMorpher.magnificent_maw$isMetamorphosized());
//        }
//    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(CallbackInfo ci) {
        // this feels fishy.
        LivingEntity livingEntity = (LivingEntity)(Object)this;
        World world = livingEntity.getWorld();


        if (livingEntity.getWorld().isClient()) {
            if (magnificent_maw$isMetamorphosized() && random.nextFloat() < 0.013) {
                world.addParticle(ParticleTypes.FLAME,
                        livingEntity.getParticleX(0.5), livingEntity.getRandomBodyY(), livingEntity.getParticleZ(0.5),
                        (random.nextFloat() - 0.5) * 0.031, random.nextFloat() * 0.031, (random.nextFloat() - 0.5) * 0.031);
            }
        } else {
            if (magnificent_maw$isBreathing() && MawUtils.canBreathe(livingEntity)) {
                if (consumeDraconicOmen(livingEntity)) {
                    BreathProjectileEntity breathProjectileEntity = new BreathProjectileEntity(livingEntity, world);
                    breathProjectileEntity.setVelocity(livingEntity, livingEntity.getPitch(), livingEntity.getYaw(), 0.0F, 0.5F, 13F);
                    breathProjectileEntity.setPosition(breathProjectileEntity.getPos().add(livingEntity.getRotationVector().multiply(0.5)).addRandom(livingEntity.getRandom(), 0.013f));
                    world.spawnEntity(breathProjectileEntity);

                    Vec3d p = livingEntity.getPos();
                    world.playSound(null, p.x, p.y, p.z, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.2f, (livingEntity.getRandom().nextFloat() * 0.13f + 1.0f));
                }
            }

            if (this.effectsChanged) {
                RegistryEntry<StatusEffect> draconicOmenEntry = getDraconicOmenEntry(this.getWorld());
                boolean hasDraconicOmen = draconicOmenEntry != null && livingEntity.hasStatusEffect(draconicOmenEntry);

                if (hasDraconicOmen != magnificent_maw$isFuelled()) {
                    magnificent_maw$setFuelled(hasDraconicOmen);
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "eatFood")
    private void eatFood(World world, ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> cir) {
        if (!world.isClient) {
            if (stack.isIn(MagnificentMaw.SWORDLY_SWALLOWABLE)) {
                Vec3d p = this.getPos();
                this.getWorld().playSound(null, p.x, p.y, p.z, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);
            }


            if (stack.isIn(MagnificentMaw.EXTRANATURAL_REPELLENT)) {
                stripDraconicTf((LivingEntity)(Object)this);
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
