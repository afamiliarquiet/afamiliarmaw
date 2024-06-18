package io.github.afamiliarquiet.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static io.github.afamiliarquiet.AFamiliarMaw.getNamespacedIdentifier;

public class MawItems {
    public static final CuriousVialItem CURIOUS_VIAL = new CuriousVialItem(new Item.Settings());
    public static final Identifier CURIOUS_VIAL_ID = getNamespacedIdentifier("curious_vial");

    public static void register() {
        Registry.register(Registries.ITEM, CURIOUS_VIAL_ID, CURIOUS_VIAL);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(
                content -> content.addAfter(Items.OMINOUS_BOTTLE, CURIOUS_VIAL)
        );
    }
}
