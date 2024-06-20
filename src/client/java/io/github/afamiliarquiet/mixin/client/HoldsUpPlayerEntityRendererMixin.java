package io.github.afamiliarquiet.mixin.client;

import io.github.afamiliarquiet.util.MawBearer;
import io.github.afamiliarquiet.util.MawUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class HoldsUpPlayerEntityRendererMixin {
    @Inject(at = @At("HEAD"), method = "getArmPose", cancellable = true)
    private static void getArmPose(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (!itemStack.isEmpty()) {
            if (player.getActiveHand() == hand && player.getItemUseTimeLeft() > 0) {
                if (itemStack.getItem() instanceof SwordItem) {
                    cir.setReturnValue(BipedEntityModel.ArmPose.TOOT_HORN);
                }
            } else if (player instanceof MawBearer mawBearer && mawBearer.magnificent_maw$isBreathing()
                    && MawUtils.isHoldingIgnition(player) && MawUtils.isFuelled(player)) {
                // *holds up torch*
                cir.setReturnValue(BipedEntityModel.ArmPose.TOOT_HORN);
            }
        }
    }
}
