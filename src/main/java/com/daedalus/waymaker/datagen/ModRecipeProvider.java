package com.daedalus.waymaker.datagen;

import com.daedalus.waymaker.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {

    public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            public void buildRecipes() {
                // List<ItemLike> WAYMAKER_SMELTABLES = List.of(ModItems.WAYMAKERS_RELIC);
                shaped(RecipeCategory.TOOLS, ModItems.WAYMAKERS_RELIC)
                        .pattern("eEe")
                        .pattern("EsE")
                        .pattern("eEe")
                        .define('e', Items.ECHO_SHARD)
                        .define('E', Items.ENDER_EYE)
                        .define('s', Items.NETHER_STAR)
                        .unlockedBy(getHasName(Items.ENDER_EYE), has(Items.ENDER_EYE))
                        .group("waymaker_recipes")
                        .save(output);
            }
        };
    }

    @Override
    public String getName() {
        return "Waymaker Recipes";
    }
}
