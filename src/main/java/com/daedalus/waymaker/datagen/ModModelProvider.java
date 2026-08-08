package com.daedalus.waymaker.datagen;

import com.daedalus.waymaker.item.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngle;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngleState;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricPackOutput output){
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators){

    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators){
        itemModelGenerators.generateFlatItem(ModItems.WAYMAKERS_RELIC, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.HEART_OF_THE_SKY, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.HEART_OF_THE_MOUNTAIN, ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.HEART_OF_THE_SUN, ModelTemplates.FLAT_ITEM);
        generateElementalCompassItem(itemModelGenerators, ModItems.ELEMENTAL_COMPASS);
    }

    private List<RangeSelectItemModel.Entry> createEightFrameCompassModels(final ItemModelGenerators itemModelGenerators, final Item compass) {
        List<RangeSelectItemModel.Entry> overrides = new ArrayList<>();
        // Frame _4 is the base (index 4 = N, the "pointing" frame, halfway through 8)
        ItemModel.Unbaked base = ItemModelUtils.plainModel(
                itemModelGenerators.createFlatItemModel(compass, "_4", ModelTemplates.FLAT_ITEM)
        );
        overrides.add(ItemModelUtils.override(base, 0.0F));

        for (int i = 1; i < 8; i++) {
            int textureIndex = Mth.positiveModulo(i - 4, 8);
            ItemModel.Unbaked overrideModel = ItemModelUtils.plainModel(
                    itemModelGenerators.createFlatItemModel(compass, String.format(Locale.ROOT, "_%d", textureIndex), ModelTemplates.FLAT_ITEM)
            );
            overrides.add(ItemModelUtils.override(overrideModel, i - 0.5F));
        }

        overrides.add(ItemModelUtils.override(base, 7.5F));
        return overrides;
    }

    public final void generateElementalCompassItem(final ItemModelGenerators itemModelGenerators, final Item compass) {
        List<RangeSelectItemModel.Entry> overrides = createEightFrameCompassModels(itemModelGenerators, compass);
        itemModelGenerators.itemModelOutput
                .accept(compass, ItemModelUtils.rangeSelect(new CompassAngle(true, CompassAngleState.CompassTarget.SPAWN), 8.0F, overrides));
    }
}
