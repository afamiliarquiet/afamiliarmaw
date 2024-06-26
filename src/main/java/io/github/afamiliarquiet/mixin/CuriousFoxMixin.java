package io.github.afamiliarquiet.mixin;

import io.github.afamiliarquiet.MagnificentMaw;
import io.github.afamiliarquiet.item.MawItems;
import io.github.afamiliarquiet.util.MawBearer;
import io.github.afamiliarquiet.util.MawUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FoxEntity.class)
public abstract class CuriousFoxMixin extends AnimalEntity {
    @Shadow
    private int eatingTime;

    protected CuriousFoxMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo ci) {
        if ((FoxEntity)(Object)this instanceof MawBearer thisButWithABigMaw) {
            // breathe when sitting/walking i think?
            ItemStack itemStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
            thisButWithABigMaw.magnificent_maw$setBreathing(!(this.isRollingHead() || this.isSitting() || this.isSleeping())
                    && (itemStack.isEmpty() || MawUtils.isIgnition(itemStack)));
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

    @Shadow
    public abstract boolean isRollingHead();
    @Shadow
    public abstract boolean isSitting();

    @Shadow
    public abstract boolean isSleeping();
}
