package io.github.afamiliarquiet.mixin.client;

import io.github.afamiliarquiet.MagnificentMaw;
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

    public SwordSwallowingRendererMixin(FeatureRendererContext<T, M> context, HeldItemRenderer heldItemRenderer) {
        super(context, heldItemRenderer);
        this.playerHeldItemRenderer = heldItemRenderer;
    }

    // relies on the item using the handheld model. if it doesn't... it's gonna be funky. don't do that.
    @Inject(at = @At("HEAD"), method = "renderItem", cancellable = true)
    private void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode transformationMode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (stack.isIn(MagnificentMaw.SWORDLY_SWALLOWABLE) && entity.getActiveItem() == stack && entity.getItemUseTimeLeft() > 0) {
            // oh good lird i have to deal with quats now

            matrices.push();

            boolean lefty = arm == Arm.LEFT;
            int handedSign = lefty ? 1 : -1;

            float pitchPercent = entity.getPitch();

            pitchPercent = pitchPercent > 0 ? Math.min(pitchPercent, 75) : Math.max(pitchPercent, -75);
            float pitchPerfect = (entity.getPitch() - pitchPercent);
            pitchPercent = pitchPercent / 90;

            this.getContextModel().getHead().rotate(matrices);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(112.5f - pitchPercent * 22.5f - pitchPerfect));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(handedSign * 90f));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(0f));

            float prcRemaining = entity.getItemUseTimeLeft() / 31f;
            // x: down/up on face, y: in/out, z: left/right on face
            matrices.translate(
                    handedSign * ((2.5 - pitchPercent) * -.075),
                    -0.625 - prcRemaining * 0.375,
                    prcRemaining * .0875
            );

            this.playerHeldItemRenderer.renderItem(entity, stack, transformationMode, lefty, matrices, vertexConsumers, light);

            matrices.pop();

            ci.cancel();
        }
    }
}
