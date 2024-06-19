package io.github.afamiliarquiet.mixin;

import io.github.afamiliarquiet.util.MawUtils;
import io.github.afamiliarquiet.entity.BreathProjectileEntity;
import io.github.afamiliarquiet.util.MawBearer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.afamiliarquiet.MagnificentMaw.TF_TAG;
import static io.github.afamiliarquiet.util.MawUtils.consumeDraconicOmen;

@Mixin(ServerPlayerEntity.class)
public abstract class MawServerPlayerEntityMixin implements MawBearer {
    // copycode copycat! i think the appeal of maw$ is to better insist uniqueness so no conflict?
    // doesn't help the swordchompermixin that's still likely a mess but maybe this is good
    // if it wasn't clear i have no idea what i'm doing with mixins. but this seems to work.
    // also like surely i don't need to mixin to store a boolean. but whatever idk
    // btw i'm trusting these to be false by default. wahoo? hehe
    // now there's logic so it's not just a mixin for a boolean!
    @Unique
    private boolean magnificent_maw$metamorphosized;
    // todo - can i throw this in a datatracker instead? does that let it hand off to client? idk, maybe breath sounds would benefit

    @Unique
    private boolean magnificent_maw$breathing;
    // i think? it's ok to not save this in nbtstuffs because i don't really want this to be persistent
    // would be weird if you crash out and come back and are automatically breathing 'cause it stored breathing

    @Inject(at = @At("TAIL"), method = "writeCustomDataToNbt")
    private void writeCustomDataToNbt(NbtCompound tag, CallbackInfo ci) {
        tag.putBoolean(TF_TAG, magnificent_maw$metamorphosized);
    }

    @Inject(at = @At("TAIL"), method = "readCustomDataFromNbt")
    private void readCustomDataFromNbt(NbtCompound tag, CallbackInfo ci) {
        if (tag.contains(TF_TAG)) {
            magnificent_maw$setMetamorphosized(tag.getBoolean(TF_TAG));
        }
    }

    @Inject(at = @At("TAIL"), method = "copyFrom")
    private void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if (oldPlayer instanceof MawBearer oldMorpher) {
            magnificent_maw$setMetamorphosized(oldMorpher.magnificent_maw$isMetamorphosized());
        }
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void tickBreathing(CallbackInfo ci) {
        // this feels fishy.
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;

        if (magnificent_maw$isBreathing() && MawUtils.canBreathe(player)) {
            BreathProjectileEntity breathProjectileEntity = new BreathProjectileEntity(player, player.getServerWorld());
            breathProjectileEntity.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, 0.5F, 13F);
            breathProjectileEntity.setPosition(breathProjectileEntity.getPos().add(player.getRotationVector().multiply(0.5)).addRandom(player.getRandom(), 0.013f));
            player.getServerWorld().spawnEntity(breathProjectileEntity);

            consumeDraconicOmen(player);
            player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.2f, (player.getRandom().nextFloat() * 0.13f + 1.0f));
        }
    }

    @Override
    public void magnificent_maw$setMetamorphosized(boolean metamorphosized) {
        this.magnificent_maw$metamorphosized = metamorphosized;
    }

    @Override
    public boolean magnificent_maw$isMetamorphosized() {
        return this.magnificent_maw$metamorphosized;
    }

    @Override
    public void magnificent_maw$setBreathing(boolean breathing) {
        this.magnificent_maw$breathing = breathing;
    }

    @Override
    public boolean magnificent_maw$isBreathing() {
        return this.magnificent_maw$breathing;
    }
}
