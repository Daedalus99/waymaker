package com.daedalus.waymaker.item;

import com.daedalus.waymaker.Waymaker;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ModItemIds {
    public static final ResourceKey<Item> WAYMAKERS_RELIC = create("waymakers_relic");
    public static final ResourceKey<Item> HEART_OF_THE_SKY = create("heart_of_the_sky");
    public static final ResourceKey<Item> HEART_OF_THE_MOUNTAIN = create("heart_of_the_mountain");
    public static final ResourceKey<Item> HEART_OF_THE_SUN = create("heart_of_the_sun");
    public static final ResourceKey<Item> ELEMENTAL_COMPASS = create("elemental_compass");


    public static ResourceKey<Item> create(String name){
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Waymaker.MOD_ID, name));
    }
}
