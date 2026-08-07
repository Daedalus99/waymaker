package com.daedalus.waymaker.item;

import com.daedalus.waymaker.Waymaker;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModItems {
    public static final Item WAYMAKERS_RELIC = register(
            ModItemIds.WAYMAKERS_RELIC,
            Item::new,
            new Item.Properties().stacksTo(1));

    public static final Item HEART_OF_THE_SKY = register(
            ModItemIds.HEART_OF_THE_SKY,
            Item::new,
            new Item.Properties());

    public static final Item HEART_OF_THE_MOUNTAIN = register(
            ModItemIds.HEART_OF_THE_MOUNTAIN,
            Item::new,
            new Item.Properties());

    public static final Item HEART_OF_THE_SUN = register(
            ModItemIds.HEART_OF_THE_SUN,
            Item::new,
            new Item.Properties());

    public static final Item ELEMENTAL_COMPASS = register(
            ModItemIds.ELEMENTAL_COMPASS,
            Item::new,
            new Item.Properties());

    public static Item register(ResourceKey<Item> itemKey, Function<Item.Properties, Item> itemFactory, Item.Properties settings) {
        // Create the item instance.
        Item item = itemFactory.apply(settings.setId(itemKey));

        // Register the item.
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }


    public static void registerModItems() {
        Waymaker.LOGGER.info("Registering Mod Items for " + Waymaker.MOD_ID);
        initialize();
    }

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register((creativeTab) -> {
                    creativeTab.accept(ModItems.WAYMAKERS_RELIC);
                    creativeTab.accept(ModItems.ELEMENTAL_COMPASS);
                });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register((creativeTab) -> {
                    creativeTab.accept(ModItems.HEART_OF_THE_SKY);
                    creativeTab.accept(ModItems.HEART_OF_THE_MOUNTAIN);
                    creativeTab.accept(ModItems.HEART_OF_THE_SUN);
                });
    }
}