package io.github.afamiliarquiet.item;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.*;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;

import static io.github.afamiliarquiet.MagnificentMaw.id;

public class MawItems {
    public static final Identifier CURIOUS_VIAL_ID = id("curious_vial");
    public static final CuriousVialItem CURIOUS_VIAL = new CuriousVialItem(new Item.Settings());

    public static final Identifier CHOMPED_WOODEN_SWORD_ID = id("chomped_wooden_sword");
    public static final Identifier CHOMPED_STONE_SWORD_ID = id("chomped_stone_sword");
    public static final Identifier CHOMPED_IRON_SWORD_ID = id("chomped_iron_sword");
    public static final Identifier CHOMPED_GOLDEN_SWORD_ID = id("chomped_golden_sword");
    public static final Identifier CHOMPED_DIAMOND_SWORD_ID = id("chomped_diamond_sword");
    public static final Identifier CHOMPED_NETHERITE_SWORD_ID = id("chomped_netherite_sword");

    public static final Item CHOMPED_WOODEN_SWORD = new Item(new Item.Settings().maxCount(1));
    public static final Item CHOMPED_STONE_SWORD = new Item(new Item.Settings().maxCount(1));
    public static final Item CHOMPED_IRON_SWORD = new Item(new Item.Settings().maxCount(1));
    public static final Item CHOMPED_GOLDEN_SWORD = new Item(new Item.Settings().maxCount(1));
    public static final Item CHOMPED_DIAMOND_SWORD = new Item(new Item.Settings().maxCount(1));
    public static final Item CHOMPED_NETHERITE_SWORD = new Item(new Item.Settings().maxCount(1));

    public static void register() {
        registerItem(CURIOUS_VIAL_ID, CURIOUS_VIAL);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(
                content -> content.addAfter(Items.OMINOUS_BOTTLE, CURIOUS_VIAL)
        );

        // technically obtainable. would you really want to do this? probably not!
        LootTableEvents.MODIFY.register((key, tableBuilder, source) -> {
            if (source.isBuiltin() && key.equals(LootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_COMMON_CHEST)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(ItemEntry.builder(CURIOUS_VIAL)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3, 6))));

                tableBuilder.pool(poolBuilder);
            }
        });


        // todo - datagen? is that real?
        registerItem(CHOMPED_WOODEN_SWORD_ID, CHOMPED_WOODEN_SWORD);
        registerItem(CHOMPED_STONE_SWORD_ID, CHOMPED_STONE_SWORD);
        registerItem(CHOMPED_IRON_SWORD_ID, CHOMPED_IRON_SWORD);
        registerItem(CHOMPED_GOLDEN_SWORD_ID, CHOMPED_GOLDEN_SWORD);
        registerItem(CHOMPED_DIAMOND_SWORD_ID, CHOMPED_DIAMOND_SWORD);
        registerItem(CHOMPED_NETHERITE_SWORD_ID, CHOMPED_NETHERITE_SWORD);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(
                content -> content.addBefore(Items.WOODEN_AXE, CHOMPED_WOODEN_SWORD, CHOMPED_STONE_SWORD,
                        CHOMPED_IRON_SWORD, CHOMPED_GOLDEN_SWORD, CHOMPED_DIAMOND_SWORD, CHOMPED_NETHERITE_SWORD)
        );


        List<Item> vanillaSwallowables = List.of(Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD,
                Items.GOLDEN_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);


        DefaultItemComponentEvents.MODIFY.register((context) ->
                context.modify(vanillaSwallowables::contains, ((builder, item) -> {
                    if (item instanceof SwordItem swordItem) {
                        Item remnant = switch (swordItem.getMaterial()) {
                            case ToolMaterials.WOOD -> MawItems.CHOMPED_WOODEN_SWORD;
                            case ToolMaterials.STONE -> MawItems.CHOMPED_STONE_SWORD;
                            case ToolMaterials.IRON -> MawItems.CHOMPED_IRON_SWORD;
                            case ToolMaterials.GOLD -> MawItems.CHOMPED_GOLDEN_SWORD;
                            case ToolMaterials.DIAMOND -> MawItems.CHOMPED_DIAMOND_SWORD;
                            case ToolMaterials.NETHERITE -> MawItems.CHOMPED_NETHERITE_SWORD;
                            default -> Items.STICK;
                        };

                        builder.add(DataComponentTypes.FOOD, (new FoodComponent.Builder())
                                .nutrition((int) Math.floor((swordItem.getMaterial().getEnchantability() / (1.3f))))
                                .saturationModifier(0.31f).usingConvertsTo(remnant).build());
                    } else {
                        // idk how we got here.
                        builder.add(DataComponentTypes.FOOD, (new FoodComponent.Builder())
                                .nutrition(2).saturationModifier(0.5f).build());
                    }
        })));
    }

    private static void registerItem(Identifier id, Item item) {
        Registry.register(Registries.ITEM, id, item);
    }
}
