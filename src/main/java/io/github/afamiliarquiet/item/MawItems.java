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
    public static final PyreticLiqueurItem PYRETIC_LIQUEUR = new PyreticLiqueurItem(new Item.Settings());
    public static final Identifier PYRETIC_LIQUEUR_ID = getNamespacedIdentifier("pyretic_liqueur");

    public static void register() {
        Registry.register(Registries.ITEM, PYRETIC_LIQUEUR_ID, PYRETIC_LIQUEUR);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(content -> {
            content.addAfter(Items.HONEY_BOTTLE, PYRETIC_LIQUEUR);
        });
    }
}
