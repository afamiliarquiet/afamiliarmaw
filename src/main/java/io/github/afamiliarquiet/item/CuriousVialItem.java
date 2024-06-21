package io.github.afamiliarquiet.item;

import io.github.afamiliarquiet.entity.MawEntities;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

import static io.github.afamiliarquiet.util.MawUtils.getDraconicOmenEntry;
import static io.github.afamiliarquiet.util.MawUtils.isDraconicTfed;

public class CuriousVialItem extends Item {

    public CuriousVialItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        if (!world.isClient) {
            if (isDraconicTfed(user) && user instanceof PlayerEntity player) {
                player.getHungerManager().add(1, 1.3F);
            } else {
                RegistryEntry<StatusEffect> draconicOmenEntry = getDraconicOmenEntry(user.getWorld());
                StatusEffectInstance effectInstance = user.getStatusEffect(draconicOmenEntry);
                int currentDuration = effectInstance == null ? 0 : effectInstance.getDuration();
                user.addStatusEffect(new StatusEffectInstance(draconicOmenEntry, 1200 + currentDuration, 0, false, false, true));
            }

            Vec3d p = user.getPos();
            world.playSound(null, p.x, p.y, p.z, SoundEvents.ITEM_OMINOUS_BOTTLE_DISPOSE, user.getSoundCategory(), 1.0F, 1.0F);
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
        List<StatusEffectInstance> list = List.of(new StatusEffectInstance(RegistryEntry.of(
                MawEntities.DRACONIC_OMEN_STATUS_EFFECT), 1200, 0,
                false, false, true));
        Objects.requireNonNull(tooltip);
        PotionContentsComponent.buildTooltip(list, tooltip::add, 1.0F, context.getUpdateTickRate());
    }
}
