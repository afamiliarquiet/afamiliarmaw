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
    public static final PyreticLiquerItem PYRETIC_LIQUER = new PyreticLiquerItem(new Item.Settings());
    public static final Identifier PYRETIC_LIQUER_ID = getNamespacedIdentifier("pyretic_liquer");

    public static void register() {
        Registry.register(Registries.ITEM, PYRETIC_LIQUER_ID, PYRETIC_LIQUER);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(content -> {
            content.addAfter(Items.HONEY_BOTTLE, PYRETIC_LIQUER);
        });
    }
}
