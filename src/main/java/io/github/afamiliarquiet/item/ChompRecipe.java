package io.github.afamiliarquiet.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class ChompRecipe implements Recipe<SingleStackRecipeInput> {
    private final int nutrition;
    private final float saturationModifier;
    private final Ingredient swallowable;
    private final ItemStack result;
    public ChompRecipe(int nutrition, float saturationModifier, Ingredient swallowable, ItemStack result) {
        this.nutrition = nutrition;
        this.saturationModifier = saturationModifier;
        this.swallowable = swallowable;
        this.result = result;
    }

    @Override
    public boolean matches(SingleStackRecipeInput input, World world) {
        return this.swallowable.test(input.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(SingleStackRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return input.getStackInSlot(0).copyComponentsToNewStack(this.result.getItem(), 1);
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.result;
    }

    public Item getResultItem() {
        return this.result.getItem();
    }

    public FoodComponent getFoodComponent(ItemStack stack) {
        int adjustedNutrition = this.nutrition;
        if (stack.getMaxDamage() > 0) {
            // duraMod * 0.5 + 0.5 so that there's always at least a bite. a busted sword is a busted sword but still a tasty stick
            adjustedNutrition = (int) Math.ceil((((double) (stack.getMaxDamage() - stack.getDamage()) / stack.getMaxDamage()) * 0.5 + 0.5) * adjustedNutrition);
        }
        return (new FoodComponent.Builder()).nutrition(adjustedNutrition).saturationModifier(this.saturationModifier).build();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MawItems.CHOMP_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MawItems.CHOMP_RECIPE_TYPE;
    }
    
    public static class Serializer implements RecipeSerializer<ChompRecipe> {
        private static final MapCodec<ChompRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                Codec.INT.fieldOf("nutrition").orElse(4).forGetter((recipe) -> recipe.nutrition),
                Codec.FLOAT.fieldOf("saturationModifier").orElse(0.5f).forGetter((recipe) -> recipe.saturationModifier),
                Ingredient.ALLOW_EMPTY_CODEC.fieldOf("swallowable").forGetter((recipe) -> recipe.swallowable),
                ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter((recipe) -> recipe.result)
        ).apply(instance, ChompRecipe::new));

        public static final PacketCodec<RegistryByteBuf, ChompRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                ChompRecipe.Serializer::write, ChompRecipe.Serializer::read
        );
        
        @Override
        public MapCodec<ChompRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, ChompRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static ChompRecipe read(RegistryByteBuf buf) {
            int nutrition = buf.readVarInt();
            float saturationModifier = buf.readFloat();
            Ingredient swallowable = Ingredient.PACKET_CODEC.decode(buf);
            ItemStack result = ItemStack.PACKET_CODEC.decode(buf);
            return new ChompRecipe(nutrition, saturationModifier, swallowable, result);
        }

        private static void write(RegistryByteBuf buf, ChompRecipe recipe) {
            buf.writeVarInt(recipe.nutrition);
            buf.writeFloat(recipe.saturationModifier);
            Ingredient.PACKET_CODEC.encode(buf, recipe.swallowable);
            ItemStack.PACKET_CODEC.encode(buf, recipe.result);
        }
    }
}
