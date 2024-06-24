package io.github.afamiliarquiet.mixin;

import io.github.afamiliarquiet.MagnificentMaw;
import io.github.afamiliarquiet.item.MawItems;
import io.github.afamiliarquiet.util.MawBearer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FoxEntity.class)
public abstract class CuriousFoxMixin extends AnimalEntity {
    @Shadow
    private int eatingTime;

    protected CuriousFoxMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("TAIL"), method = "initialize")
    private void initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, CallbackInfoReturnable<EntityData> cir) {
        if ((FoxEntity)(Object)this instanceof MawBearer thisButWithABigMaw) {
            thisButWithABigMaw.magnificent_maw$setBreathing(true);
        }
    }

    @Inject(at = @At("TAIL"), method = "canEat", cancellable = true)
    private void canEat(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.isOf(MawItems.CURIOUS_VIAL) || stack.isIn(MagnificentMaw.SWORDLY_SWALLOWABLE)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(at = @At("TAIL"), method = "canPickupItem", cancellable = true)
    private void canPickupItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
        if (this.eatingTime > 0 && stack.isOf(MawItems.CURIOUS_VIAL) && !(itemStack.contains(DataComponentTypes.FOOD) || itemStack.isOf(MawItems.CURIOUS_VIAL))) {
            cir.setReturnValue(true);
        }
    }
}
