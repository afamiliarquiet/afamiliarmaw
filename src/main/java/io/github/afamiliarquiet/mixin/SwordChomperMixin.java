package io.github.afamiliarquiet.mixin;

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
						(int) Math.floor(getMaterial().getMiningSpeedMultiplier() / 3.0f),
						getMaterial().getEnchantability() / 5.0f);
				user.getWorld().playSound(null, user.getBlockPos(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
			}

			if (getMaterial().equals(ToolMaterials.IRON)) {
				stripDraconicTf(user);
			}
		}

		// todo - try to use different texture w/o blade instead?
		stack.decrementUnlessCreative(1, user);
		if (user instanceof PlayerEntity player && !player.isInCreativeMode()) {
			ItemStack remnant = Items.STICK.getDefaultStack();
			if (stack.isEmpty()) {
				return remnant.copy();
			}

			if (!player.getWorld().isClient()) {
				player.getInventory().insertStack(remnant.copy());
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