package com.daedalus.waymaker.datagen;

import com.daedalus.waymaker.item.ModItems;
import com.daedalus.waymaker.recipe.CenterLockedShapelessRecipe;
import com.daedalus.waymaker.recipe.ModRecipes;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.List;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.world.item.slot.SlotSources.group;

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

                // Elemental Compass — cross pattern:
                //   Sky (N), Sea (W), Sun (E), Mountain (S)
                //   Sun rises in the east; sea stretches to the west
                shaped(RecipeCategory.TOOLS, ModItems.ELEMENTAL_COMPASS)
                        .pattern(" n ")
                        .pattern("wCe")
                        .pattern(" s ")
                        .define('n', ModItems.HEART_OF_THE_SKY)
                        .define('C', Items.RECOVERY_COMPASS)
                        .define('w', Items.HEART_OF_THE_SEA)
                        .define('e', ModItems.HEART_OF_THE_SUN)
                        .define('s', ModItems.HEART_OF_THE_MOUNTAIN)
                        .unlockedBy(getHasName(Items.RECOVERY_COMPASS), has(Items.RECOVERY_COMPASS))
                        .group("waymaker_recipes")
                        .save(output);

                shaped(RecipeCategory.MISC, Items.HEART_OF_THE_SEA)
                        .pattern("csc")
                        .pattern("sIs")
                        .pattern("csc")
                        .define('c', Items.PRISMARINE_CRYSTALS)
                        .define('s', Items.PRISMARINE_SHARD)
                        .define('I', Items.BLUE_ICE)
                        .unlockedBy(getHasName(Items.BLUE_ICE), has(Items.BLUE_ICE))
                        .group("waymaker_recipes")
                        .save(output);

                shaped(RecipeCategory.MISC, ModItems.HEART_OF_THE_SUN)
                        .pattern("bcb")
                        .pattern("cMc")
                        .pattern("bcb")
                        .define('b', Items.BLAZE_POWDER)
                        .define('c', Items.FIRE_CHARGE)
                        .define('M', Items.MAGMA_BLOCK)
                        .unlockedBy(getHasName(Items.MAGMA_BLOCK), has(Items.MAGMA_BLOCK))
                        .group("waymaker_recipes")
                        .save(output);

                output.accept(
                        ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("waymaker", "heart_of_the_mountain")),
                        new CenterLockedShapelessRecipe(
                                ModItems.HEART_OF_THE_MOUNTAIN,
                                Items.DEEPSLATE,
                                List.of(
                                        Items.RAW_IRON,
                                        Items.DIAMOND,
                                        Items.RAW_COPPER,
                                        Items.LAPIS_LAZULI,
                                        Items.AMETHYST_SHARD,
                                        Items.EMERALD,
                                        Items.COAL,
                                        Items.RAW_GOLD
                                )
                        ),
                        output.advancement()
                                .addCriterion("has_deepslate", has(Items.DEEPSLATE))
                                .build(Identifier.fromNamespaceAndPath("waymaker", "recipes/misc/heart_of_the_mountain"))
                );

                shaped(RecipeCategory.MISC, ModItems.HEART_OF_THE_SKY)
                        .pattern("pfp")
                        .pattern("fWf")
                        .pattern("pfp")
                        .define('p', Items.PHANTOM_MEMBRANE)
                        .define('f', Items.FEATHER)
                        .define('W', Items.WIND_CHARGE)
                        .unlockedBy(getHasName(Items.WIND_CHARGE), has(Items.WIND_CHARGE))
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