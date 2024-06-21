package io.github.afamiliarquiet;

import io.github.afamiliarquiet.entity.MawEntities;
import io.github.afamiliarquiet.item.MawItems;
import io.github.afamiliarquiet.network.MawPackets;
import net.fabricmc.api.ModInitializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MagnificentMaw implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "magnificent_maw";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final String TF_TAG = MOD_ID + "_metamorphosized";
	public static final String BREATHING_TAG = MOD_ID + "_breathing";
	public static final String FUELLED_TAG = MOD_ID + "_fuelled";

	// todo - descriptions for mods such as emi?
	public static final TagKey<Enchantment> FIERY_ENCHANTMENTS = TagKey.of(RegistryKeys.ENCHANTMENT, id("fiery_enchantments"));
	public static final TagKey<Item> FIERY_ITEMS = TagKey.of(RegistryKeys.ITEM, id("fiery_items"));
	public static final TagKey<Item> EXTRANATURAL_REPELLENT = TagKey.of(RegistryKeys.ITEM, id("extranatural_repellent"));
	public static final TagKey<Item> SWORDLY_SWALLOWABLE = TagKey.of(RegistryKeys.ITEM, id("swordly_swallowable"));

	public static Identifier id(String name) {
		return Identifier.of(MOD_ID, name);
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		// todo - add advancements maybe? chomping an enchanted+named item, transforming?

		MawPackets.registerC2SPayloads();
		MawPackets.registerC2SReceivers();
		MawEntities.register();
		MawItems.register();
	}
}