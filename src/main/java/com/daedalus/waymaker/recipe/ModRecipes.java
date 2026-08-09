package com.daedalus.waymaker.recipe;

import com.daedalus.waymaker.Waymaker;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.List;

public class ModRecipes {

    private static final com.mojang.serialization.Codec<Item> ITEM_CODEC = BuiltInRegistries.ITEM.byNameCodec();
    private static final StreamCodec<RegistryFriendlyByteBuf, Item> ITEM_STREAM_CODEC =
            ByteBufCodecs.registry(net.minecraft.core.registries.Registries.ITEM);

    public static final MapCodec<CenterLockedShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ITEM_CODEC.fieldOf("result").forGetter(CenterLockedShapelessRecipe::getResult),
                    ITEM_CODEC.fieldOf("center").forGetter(CenterLockedShapelessRecipe::getCenter),
                    ITEM_CODEC.listOf().fieldOf("surroundings").forGetter(CenterLockedShapelessRecipe::getSurroundings)
            ).apply(instance, CenterLockedShapelessRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CenterLockedShapelessRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    ITEM_STREAM_CODEC, CenterLockedShapelessRecipe::getResult,
                    ITEM_STREAM_CODEC, CenterLockedShapelessRecipe::getCenter,
                    ITEM_STREAM_CODEC.apply(ByteBufCodecs.list()), CenterLockedShapelessRecipe::getSurroundings,
                    CenterLockedShapelessRecipe::new
            );

    public static final RecipeSerializer<CenterLockedShapelessRecipe> CENTER_LOCKED_SHAPELESS =
            Registry.register(
                    BuiltInRegistries.RECIPE_SERIALIZER,
                    Waymaker.id("center_locked_shapeless"),
                    new RecipeSerializer<>(CODEC, STREAM_CODEC)
            );

    public static void register() {
        Waymaker.LOGGER.info("Registering recipe serializers for " + Waymaker.MOD_ID);
        CENTER_LOCKED_SHAPELESS.getClass(); // trigger static init
    }
}
