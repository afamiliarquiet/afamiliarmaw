package io.github.afamiliarquiet.mixin.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// what is here was dangerous and repulsive to us. this message is a warning about danger.
// this place is best shunned and left uninhabited.
@Mixin(PlayerHeldItemFeatureRenderer.class)
public abstract class SwordSwallowingRendererMixin<T extends PlayerEntity, M extends EntityModel<T> & ModelWithArms & ModelWithHead> extends HeldItemFeatureRenderer<T, M> {

    // i have no idea what's going on with this thing i just wanted to shadow it and then it wanted a constructor and then the constructor was made because it was final and so now it's a mutable final which seems really weird but idk i don't wanna deal with this
    @Mutable
    @Final
    @Shadow
    private final HeldItemRenderer playerHeldItemRenderer;

    public SwordSwallowingRendererMixin(FeatureRendererContext<T, M> context, HeldItemRenderer heldItemRenderer, HeldItemRenderer playerHeldItemRenderer) {
        super(context, heldItemRenderer);
        this.playerHeldItemRenderer = playerHeldItemRenderer;
    }

    @Inject(at = @At("HEAD"), method = "renderItem", cancellable = true)
    private void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode transformationMode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (stack.getItem() instanceof SwordItem && entity.getActiveItem() == stack && entity.getItemUseTimeLeft() > 0) {
            // oh good lird i have to deal with quats now

            matrices.push();

            boolean lefty = arm == Arm.LEFT;
            int sign = lefty ? -1 : 1;
            this.getContextModel().setArmAngle(arm, matrices);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0f));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(sign * 90.0f));

            matrices.translate((float)sign / 16.0f, 0.125f, -0.625f);

            float pitch = entity.getPitch();
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F - Math.abs(pitch - 45f) * .25f));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(sign * (-20.0f - (pitch > 0 ? pitch * 0.5f : pitch) * .13f)));
            // the .13f is kinda like (pitch / 90) * 13

            matrices.translate(sign * 0.0625, -0.5625 + pitch * .0007, 0.125);
            // .0007 is like 1 / (16 * 90)
            // why am i not just writing these out? more magic numbers = more magical mod.

            this.playerHeldItemRenderer.renderItem(entity, stack, transformationMode, lefty, matrices, vertexConsumers, light);

            matrices.pop();

            ci.cancel();
        }
    }
}
