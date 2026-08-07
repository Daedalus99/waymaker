package com.daedalus.waymaker;

import com.daedalus.waymaker.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Waymaker implements ModInitializer {
	public static final String MOD_ID = "waymaker";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Waymaker initializing!");
		ModItems.registerModItems();
		registerLootTableModifications();
		com.daedalus.waymaker.network.CompassNetwork.registerPayloads();
		com.daedalus.waymaker.network.CompassNetwork.registerServerside();
	}

	private void registerLootTableModifications() {
		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
			if (source.isBuiltin() && BuiltInLootTables.BURIED_TREASURE.equals(key)) {
				tableBuilder.withPool(
						LootPool.lootPool()
								.setRolls(ConstantValue.exactly(1))
								.add(LootItem.lootTableItem(ModItems.HEART_OF_THE_SKY))
				);
				tableBuilder.withPool(
						LootPool.lootPool()
								.setRolls(ConstantValue.exactly(1))
								.add(LootItem.lootTableItem(ModItems.HEART_OF_THE_MOUNTAIN))
				);
				tableBuilder.withPool(
						LootPool.lootPool()
								.setRolls(ConstantValue.exactly(1))
								.add(LootItem.lootTableItem(ModItems.HEART_OF_THE_SUN))
				);
			}
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
