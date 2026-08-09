package com.daedalus.waymaker.recipe;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CenterLockedShapelessRecipe implements CraftingRecipe {
    final List<Item> requiredSurroundings;
    final Item center;
    final Item result;

    public CenterLockedShapelessRecipe(Item result, Item center, List<Item> requiredSurroundings) {
        this.result = result;
        this.center = center;
        this.requiredSurroundings = new ArrayList<>(requiredSurroundings);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() != 3 || input.height() != 3) {
            return false;
        }

        // Slot 4 is the exact center of a 3x3 crafting grid layout
        if (!input.getItem(4).is(this.center)) {
            return false;
        }

        // Gather the remaining 8 slots safely
        List<Item> actualSurroundings = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (i != 4) {
                ItemStack stack = input.getItem(i);
                if (!stack.isEmpty()) {
                    actualSurroundings.add(stack.getItem());
                }
            }
        }

        return matchLooseIngredients(actualSurroundings);
    }

    private boolean matchLooseIngredients(List<Item> actual) {
        if (actual.size() != this.requiredSurroundings.size()) {
            return false;
        }
        List<Item> checklist = new ArrayList<>(this.requiredSurroundings);

        for (Item actualItem : actual) {
            boolean matched = false;
            for (int i = 0; i < checklist.size(); i++) {
                if (actualItem == checklist.get(i)) {
                    checklist.remove(i);
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }
        return checklist.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        return new ItemStack(this.result);
    }

    public List<Item> getSurroundings() {
        return requiredSurroundings;
    }

    public Item getCenter() {
        return center;
    }

    public Item getResult() {
        return result;
    }

    @Override
    public RecipeSerializer<? extends CraftingRecipe> getSerializer() {
        return ModRecipes.CENTER_LOCKED_SHAPELESS;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public String group() {
        return "waymaker_recipes";
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public PlacementInfo placementInfo() {
        List<Optional<Ingredient>> ingredients = new ArrayList<>();
        // Center goes in slot 4 — add surroundings around it to match the 3x3 layout:
        // slots 0-3 before center, center at index 4, slots 5-8 after
        for (int i = 0; i < 4; i++) {
            ingredients.add(Optional.of(Ingredient.of(requiredSurroundings.get(i))));
        }
        ingredients.add(Optional.of(Ingredient.of(this.center)));
        for (int i = 4; i < requiredSurroundings.size(); i++) {
            ingredients.add(Optional.of(Ingredient.of(requiredSurroundings.get(i))));
        }
        return PlacementInfo.createFromOptionals(ingredients);
    }

    @Override
    public List<RecipeDisplay> display() {
        List<SlotDisplay> inputDisplays = new ArrayList<>();
        for (Item item : this.requiredSurroundings) {
            inputDisplays.add(new SlotDisplay.ItemSlotDisplay(item));
        }

        return List.of(new ShapelessCraftingRecipeDisplay(
                inputDisplays,
                new SlotDisplay.ItemSlotDisplay(this.result),
                new SlotDisplay.ItemSlotDisplay(this.center)
        ));
    }
}
