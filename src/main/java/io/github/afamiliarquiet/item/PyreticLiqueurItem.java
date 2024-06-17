package io.github.afamiliarquiet.item;

import io.github.afamiliarquiet.AFamiliarMaw;
import io.github.afamiliarquiet.entity.MawEntities;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class PyreticLiqueurItem extends Item {

    public PyreticLiqueurItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        if (!world.isClient) {
            if (user.getCommandTags().contains(AFamiliarMaw.TF_TAG) && user instanceof PlayerEntity player) {
                player.getHungerManager().add(1, 1.0F);
            } else {
                RegistryEntry<StatusEffect> pyrexiaEntry = user.getWorld().getRegistryManager().get(RegistryKeys.STATUS_EFFECT).getEntry(MawEntities.PYREXIA_STATUS_EFFECT_ID).get();
                StatusEffectInstance effectInstance = user.getStatusEffect(pyrexiaEntry);
                int currentDuration = effectInstance == null ? 0 : effectInstance.getDuration();
                user.addStatusEffect(new StatusEffectInstance(pyrexiaEntry, 1200 + currentDuration, 0, false, false, true));
            }
            world.playSound((PlayerEntity) null, user.getBlockPos(), SoundEvents.ITEM_OMINOUS_BOTTLE_DISPOSE, user.getSoundCategory(), 1.0F, 1.0F);
        }

        stack.decrementUnlessCreative(1, user);
        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 13;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        List<StatusEffectInstance> list = List.of(new StatusEffectInstance(RegistryEntry.of(MawEntities.PYREXIA_STATUS_EFFECT), 3600, 0, false, false, true));
        Objects.requireNonNull(tooltip);
        PotionContentsComponent.buildTooltip(list, tooltip::add, 1.0F, context.getUpdateTickRate());
    }
}
