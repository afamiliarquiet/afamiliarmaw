package io.github.afamiliarquiet.mixin;

import io.github.afamiliarquiet.MagnificentMaw;
import io.github.afamiliarquiet.item.ChompRecipe;
import io.github.afamiliarquiet.item.MawItems;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Item.class)
public abstract class ChompableItemMixin {
    @Inject(at = @At("TAIL"), method = "use", cancellable = true)
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (user.canConsume(false) && user.getStackInHand(hand).isIn(MagnificentMaw.SWORDLY_SWALLOWABLE)) {
            cir.setReturnValue(ItemUsage.consumeHeldItem(world, user, hand));
        }
    }

    @Inject(at = @At("HEAD"), method = "finishUsing", cancellable = true)
    public void finishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (stack.isIn(MagnificentMaw.SWORDLY_SWALLOWABLE)) {
            Optional<RecipeEntry<ChompRecipe>> match = world.getRecipeManager().getFirstMatch(MawItems.CHOMP_RECIPE_TYPE, new SingleStackRecipeInput(stack), world);
            if (match.isPresent()) {
                ChompRecipe recipe = match.get().value();
                FoodComponent foodComponent = recipe.getFoodComponent(stack);

                if (user instanceof PlayerEntity player && !player.isInCreativeMode()) {
                    ItemStack chomped = stack.copyComponentsToNewStack(recipe.getResultItem(), 1);

                    if (stack.getCount() == 1) {
                        // if will be eaten, chew without caring abt stack and return new stack.
                        // its a bit fluffy. don't worry about it as long as it works before submission it's perfect
                        user.eatFood(world, stack, foodComponent);
                        cir.setReturnValue(chomped);
                        return;
                    } else {
                        if (!player.getWorld().isClient()) {
                            player.getInventory().insertStack(chomped);
                        }
                    }
                }

                cir.setReturnValue(user.eatFood(world, stack, foodComponent));
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "getMaxUseTime", cancellable = true)
    public void getMaxUseTime(ItemStack stack, LivingEntity user, CallbackInfoReturnable<Integer> cir) {
        if (stack.isIn(MagnificentMaw.SWORDLY_SWALLOWABLE)) {
            // not quite standard use time! i'm using this to cheat for detecting if the use is actually eating and not whatever else an item might do. like twirl.
            cir.setReturnValue(MagnificentMaw.TOTALLY_UNIQUE_TO_SWALLOWABLE_USE_TIME);
        }
    }

    @Inject(at = @At("TAIL"), method = "getUseAction", cancellable = true)
    public void getUseAction(ItemStack stack, CallbackInfoReturnable<UseAction> cir) {
        if (stack.isIn(MagnificentMaw.SWORDLY_SWALLOWABLE)) {
            cir.setReturnValue(UseAction.EAT);
        }
    }
}
