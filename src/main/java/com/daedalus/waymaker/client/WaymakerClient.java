package com.daedalus.waymaker.client;

import com.daedalus.waymaker.network.CompassNetwork;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.chat.Component;

public class WaymakerClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(
				CompassNetwork.BiomeResultPayload.TYPE,
				(payload, context) -> {
					// ResourceKey.toString() returns "ResourceKey[registry / element]"; extract element path
				String keyStr = payload.biomeKey().toString();
				// Fallback: parse the element portion after " / "
				int sep = keyStr.lastIndexOf(" / ");
				String elementPath = sep >= 0 ? keyStr.substring(sep + 3, keyStr.length() - 1) : keyStr;
				// Strip namespace prefix if present (e.g. "minecraft:plains" -> "plains")
				int colonIdx = elementPath.indexOf(':');
				String biomeName = (colonIdx >= 0 ? elementPath.substring(colonIdx + 1) : elementPath).replace('_', ' ');
					biomeName = Character.toUpperCase(biomeName.charAt(0)) + biomeName.substring(1);

					Component message;
					if (payload.found()) {
						message = Component.translatable(
								"chat.waymaker.compass.found",
								biomeName, payload.x(), payload.z()
						);
					} else {
						message = Component.translatable(
								"chat.waymaker.compass.not_found",
								biomeName
						);
					}
					context.client().player.sendSystemMessage(message);
				}
		);
	}
}