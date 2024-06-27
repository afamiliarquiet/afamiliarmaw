package io.github.afamiliarquiet;

import io.github.afamiliarquiet.entity.MawEntities;
import io.github.afamiliarquiet.item.ChompRecipe;
import io.github.afamiliarquiet.item.MawItems;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

import static io.github.afamiliarquiet.MagnificentMaw.id;

public class MagnificentMawDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(MawEnglishLanguageGenerator::new);
		pack.addProvider(MawMeowishLanguageGenerator::new);
		//pack.addProvider(MawJapaneseLanguageGenerator::new);

		pack.addProvider(MawModelGenerator::new);

		pack.addProvider(MawEnchantmentTagGenerator::new);
		pack.addProvider(MawItemTagGenerator::new);

		pack.addProvider(MawRecipeGenerator::new);
	}

	private static class MawEnglishLanguageGenerator extends FabricLanguageProvider {
		protected MawEnglishLanguageGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
			super(dataOutput, registryLookup);
		}

		@Override
		public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
			translationBuilder.add("key.magnificent_maw.breathe", "Breathe");
			translationBuilder.add("category.magnificent_maw.maw", "your Magnificent Maw");

			translationBuilder.add(MawItems.CURIOUS_VIAL, "Curious Vial");

			translationBuilder.add(MawItems.CHOMPED_WOODEN_SWORD, "Wooden Sword..?");
			translationBuilder.add(MawItems.CHOMPED_STONE_SWORD, "Stone Sword..?");
			translationBuilder.add(MawItems.CHOMPED_IRON_SWORD, "Iron Sword..?");
			translationBuilder.add(MawItems.CHOMPED_GOLDEN_SWORD, "Golden Sword..?");
			translationBuilder.add(MawItems.CHOMPED_DIAMOND_SWORD, "Diamond Sword..?");
			translationBuilder.add(MawItems.CHOMPED_NETHERITE_SWORD, "Netherite Sword..?");

			translationBuilder.add(MawEntities.BREATH_PROJECTILE_TYPE, "Fire Breath");
			translationBuilder.add(MawEntities.DRACONIC_OMEN_STATUS_EFFECT, "Draconic Omen");

			translationBuilder.add(MagnificentMaw.EXTRANATURAL_REPELLENT, "Extranatural Repellent");
			translationBuilder.add(MagnificentMaw.FIERY_ENCHANTMENTS, "Fiery Enchantments");
			translationBuilder.add(MagnificentMaw.FIERY_ITEMS, "Fiery Items");
			translationBuilder.add(MagnificentMaw.SWORDLY_SWALLOWABLE, "Swallowable like a Sword");

			translationBuilder.add("message.magnificent_maw.apply_tf", "you seem the draconic sort, let's give you a spark!");
			translationBuilder.add("message.magnificent_maw.strip_tf", "oh.. your fire...");
		}
	}

	private static class MawMeowishLanguageGenerator extends FabricLanguageProvider {
		protected MawMeowishLanguageGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
			super(dataOutput, "lol_us", registryLookup);
		}

		@Override
		public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
			translationBuilder.add("key.magnificent_maw.breathe", "hiss");
			translationBuilder.add("category.magnificent_maw.maw", "yer ferowshus Teef");

			translationBuilder.add(MawItems.CURIOUS_VIAL, "warm! derg stufs");

			translationBuilder.add(MawItems.CHOMPED_WOODEN_SWORD, "nommed a sord");
			translationBuilder.add(MawItems.CHOMPED_STONE_SWORD, "rock on stik");
			translationBuilder.add(MawItems.CHOMPED_IRON_SWORD, "Irun?");
			translationBuilder.add(MawItems.CHOMPED_GOLDEN_SWORD, "banna peel");
			translationBuilder.add(MawItems.CHOMPED_DIAMOND_SWORD, "shiny Swurd! still! prommy");
			translationBuilder.add(MawItems.CHOMPED_NETHERITE_SWORD, "wuznt me...");

			translationBuilder.add(MawEntities.BREATH_PROJECTILE_TYPE, "dragon BLAST!!1");
			translationBuilder.add(MawEntities.DRACONIC_OMEN_STATUS_EFFECT, "Dargonz lookin!!");
		}
	}

	private static class MawJapaneseLanguageGenerator extends FabricLanguageProvider {
		protected MawJapaneseLanguageGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
			super(dataOutput, registryLookup);
		}

		@Override
		public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
			translationBuilder.add("key.magnificent_maw.breathe", "吐く");
			translationBuilder.add("category.magnificent_maw.maw", "自分の堂々たる口");

			translationBuilder.add(MawItems.CURIOUS_VIAL, "奇妙な瓶");

			translationBuilder.add(MawItems.CHOMPED_WOODEN_SWORD, "木の剣…か？");
			translationBuilder.add(MawItems.CHOMPED_STONE_SWORD, "石の剣…か？");
			translationBuilder.add(MawItems.CHOMPED_IRON_SWORD, "鉄の剣…か？");
			translationBuilder.add(MawItems.CHOMPED_GOLDEN_SWORD, "金の剣…か？");
			translationBuilder.add(MawItems.CHOMPED_DIAMOND_SWORD, "ダイヤモンドの剣…か？");
			translationBuilder.add(MawItems.CHOMPED_NETHERITE_SWORD, "ネザライトの剣…か？");

			translationBuilder.add(MawEntities.DRACONIC_OMEN_STATUS_EFFECT, "ドラゴン予感");

			translationBuilder.add(MagnificentMaw.EXTRANATURAL_REPELLENT, "超自然除け");
			translationBuilder.add(MagnificentMaw.FIERY_ENCHANTMENTS, "Fiery Enchantments");
			translationBuilder.add(MagnificentMaw.FIERY_ITEMS, "Fiery Items");
			translationBuilder.add(MagnificentMaw.SWORDLY_SWALLOWABLE, "Swallowable like a Sword");

			translationBuilder.add("message.magnificent_maw.apply_tf", "you seem the draconic sort, let's give you a spark!");
			translationBuilder.add("message.magnificent_maw.strip_tf", "oh.. your fire...");
		}
	}

	private static class MawModelGenerator extends FabricModelProvider {
		public MawModelGenerator(FabricDataOutput output) {
			super(output);
		}

		@Override
		public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {}

		@Override
		public void generateItemModels(ItemModelGenerator itemModelGenerator) {
			itemModelGenerator.register(MawItems.CURIOUS_VIAL, Models.GENERATED);

			itemModelGenerator.register(MawItems.CHOMPED_WOODEN_SWORD, Models.GENERATED);
			itemModelGenerator.register(MawItems.CHOMPED_STONE_SWORD, Models.GENERATED);
			itemModelGenerator.register(MawItems.CHOMPED_IRON_SWORD, Models.GENERATED);
			itemModelGenerator.register(MawItems.CHOMPED_GOLDEN_SWORD, Models.GENERATED);
			itemModelGenerator.register(MawItems.CHOMPED_DIAMOND_SWORD, Models.GENERATED);
			itemModelGenerator.register(MawItems.CHOMPED_NETHERITE_SWORD, Models.GENERATED);
		}
	}

	private static class MawEnchantmentTagGenerator extends FabricTagProvider.EnchantmentTagProvider {
		public MawEnchantmentTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
			super(output, completableFuture);
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
			getOrCreateTagBuilder(MagnificentMaw.FIERY_ENCHANTMENTS).setReplace(false)
					.add(Enchantments.FIRE_ASPECT);
		}
	}

	private static class MawItemTagGenerator extends FabricTagProvider.ItemTagProvider {
		public MawItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
			super(output, completableFuture);
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
			getOrCreateTagBuilder(MagnificentMaw.EXTRANATURAL_REPELLENT).setReplace(false)
					.add(Items.IRON_SWORD);

			getOrCreateTagBuilder(MagnificentMaw.FIERY_ITEMS).setReplace(false)
					.add(Items.TORCH)
					.add(Items.SOUL_TORCH)
					.add(Items.CAMPFIRE)
					.add(Items.SOUL_CAMPFIRE)
					.add(Items.BLAZE_POWDER)
					.add(Items.BLAZE_ROD)
					.add(Items.FLINT_AND_STEEL)
					.add(Items.FIRE_CHARGE)
					.add(Items.LAVA_BUCKET);

			getOrCreateTagBuilder(MagnificentMaw.SWORDLY_SWALLOWABLE).setReplace(false)
					.add(Items.WOODEN_SWORD)
					.add(Items.STONE_SWORD)
					.add(Items.IRON_SWORD)
					.add(Items.GOLDEN_SWORD)
					.add(Items.DIAMOND_SWORD)
					.add(Items.NETHERITE_SWORD)
					.add(Items.BLAZE_ROD)
					.add(Items.STICK);
		}
	}

	private static class MawRecipeGenerator extends FabricRecipeProvider {
		public MawRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		public void generate(RecipeExporter exporter) {
			blastificateTheCode(exporter, "chomped_wooden_sword",
					3, 1.0f, Items.WOODEN_SWORD, MawItems.CHOMPED_WOODEN_SWORD);
			blastificateTheCode(exporter, "chomped_stone_sword",
					6, 0f, Items.STONE_SWORD, MawItems.CHOMPED_STONE_SWORD);
			blastificateTheCode(exporter, "chomped_iron_sword",
					8, 0.5f, Items.IRON_SWORD, MawItems.CHOMPED_IRON_SWORD);
			blastificateTheCode(exporter, "chomped_golden_sword",
					7, 1.5f, Items.GOLDEN_SWORD, MawItems.CHOMPED_GOLDEN_SWORD);
			blastificateTheCode(exporter, "chomped_diamond_sword",
					12, 0.3f, Items.DIAMOND_SWORD, MawItems.CHOMPED_DIAMOND_SWORD);
			blastificateTheCode(exporter, "chomped_netherite_sword",
					13, 1.3f, Items.NETHERITE_SWORD, MawItems.CHOMPED_NETHERITE_SWORD);
		}

		private void blastificateTheCode(RecipeExporter exporter, String idString, int nutrition, float saturationModifier, Item swallowable, Item result) {
			exporter.accept(
					id(idString),
					new ChompRecipe(nutrition,
							saturationModifier,
							Ingredient.ofItems(swallowable),
							result.getDefaultStack()),
					null
			);
		}
	}
}
