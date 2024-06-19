package io.github.afamiliarquiet.mixin;

import io.github.afamiliarquiet.item.MawItems;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import static io.github.afamiliarquiet.util.MawUtils.stripDraconicTf;

@Mixin(SwordItem.class)
public abstract class SwordChomperMixin extends ToolItem {
	public SwordChomperMixin(ToolMaterial material, Item.Settings settings) {
		super(material, settings);
	}

	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (user.canConsume(false) && user.getPitch() < -50f) {
			return ItemUsage.consumeHeldItem(world, user, hand);
		} else {
			return TypedActionResult.fail(user.getStackInHand(hand));
		}
	}

	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		if (user instanceof ServerPlayerEntity serverPlayerEntity) {
			Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
			serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat((SwordItem)(Object)this));

		}
		if (!world.isClient) {
			if (user instanceof ServerPlayerEntity serverPlayerEntity) {
				serverPlayerEntity.getHungerManager().add(
						(int) Math.floor(getMaterial().getEnchantability() / 1.3f),
						0.31f);
				user.getWorld().playSound(null, user.getBlockPos(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
			}

			if (getMaterial().equals(ToolMaterials.IRON)) {
				stripDraconicTf(user);
			}
		}

		// todo - try to use different texture w/o blade instead?
		stack.decrementUnlessCreative(1, user);
		if (user instanceof PlayerEntity player && !player.isInCreativeMode()) {
			Item remnant = switch (this.getMaterial()) {
                case ToolMaterials.WOOD -> MawItems.CHOMPED_WOODEN_SWORD;
                case ToolMaterials.STONE -> MawItems.CHOMPED_STONE_SWORD;
                case ToolMaterials.IRON -> MawItems.CHOMPED_IRON_SWORD;
                case ToolMaterials.GOLD -> MawItems.CHOMPED_GOLDEN_SWORD;
                case ToolMaterials.DIAMOND -> MawItems.CHOMPED_DIAMOND_SWORD;
                case ToolMaterials.NETHERITE -> MawItems.CHOMPED_NETHERITE_SWORD;
                default -> Items.STICK;
            };

			// look... it's perfect code (there's a private copy that ignores empty, but i don't wanna widen)
			stack.increment(1);
			ItemStack newStack = stack.copyComponentsToNewStack(remnant, 1);
			stack.decrement(1);

			if (stack.isEmpty()) {
				return newStack;
			}

			if (!player.getWorld().isClient()) {
				player.getInventory().insertStack(newStack);
			}
		}
		return stack;
	}


	public int getMaxUseTime(ItemStack stack, LivingEntity user) {
		return 31;
	}


	public UseAction getUseAction(ItemStack stack) {
		return UseAction.EAT;
	}
}