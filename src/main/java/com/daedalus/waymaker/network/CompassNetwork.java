package com.daedalus.waymaker.network;

import com.daedalus.waymaker.Waymaker;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public class CompassNetwork {

    // C->S: client requests nearest biome
    public record BiomeRequestPayload(ResourceKey<Biome> biomeKey) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<BiomeRequestPayload> TYPE =
                new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Waymaker.MOD_ID, "biome_request"));

        public static final StreamCodec<RegistryFriendlyByteBuf, BiomeRequestPayload> CODEC =
                StreamCodec.of(
                        (buf, payload) -> buf.writeResourceKey(payload.biomeKey()),
                        buf -> new BiomeRequestPayload(buf.readResourceKey(Registries.BIOME))
                );

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    // S->C: server sends back the result
    public record BiomeResultPayload(ResourceKey<Biome> biomeKey, boolean found, int x, int z) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<BiomeResultPayload> TYPE =
                new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Waymaker.MOD_ID, "biome_result"));

        public static final StreamCodec<RegistryFriendlyByteBuf, BiomeResultPayload> CODEC =
                StreamCodec.of(
                        (buf, payload) -> {
                            buf.writeResourceKey(payload.biomeKey());
                            buf.writeBoolean(payload.found());
                            if (payload.found()) {
                                buf.writeInt(payload.x());
                                buf.writeInt(payload.z());
                            }
                        },
                        buf -> {
                            ResourceKey<Biome> key = buf.readResourceKey(Registries.BIOME);
                            boolean found = buf.readBoolean();
                            int x = found ? buf.readInt() : 0;
                            int z = found ? buf.readInt() : 0;
                            return new BiomeResultPayload(key, found, x, z);
                        }
                );

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /** Called from the common main entrypoint. Registers both payload types (required on both sides). */
    public static void registerPayloads() {
        PayloadTypeRegistry.serverboundPlay().register(BiomeRequestPayload.TYPE, BiomeRequestPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(BiomeResultPayload.TYPE, BiomeResultPayload.CODEC);
    }

    /** Called from the common main entrypoint (server-side handler). */
    public static void registerServerside() {
        ServerPlayNetworking.registerGlobalReceiver(BiomeRequestPayload.TYPE, (payload, context) ->
                context.server().execute(() -> {
                    ServerPlayer player = context.player();
                    ServerLevel level = (ServerLevel) player.level();
                    Registry<Biome> biomeRegistry = level.registryAccess().lookupOrThrow(Registries.BIOME);

                    // Verify the biome key exists in the registry before searching
                    if (biomeRegistry.get(payload.biomeKey()).isEmpty()) {
                        ServerPlayNetworking.send(player,
                                new BiomeResultPayload(payload.biomeKey(), false, 0, 0));
                        return;
                    }

                    BlockPos playerPos = player.blockPosition();
                    var result = level.findClosestBiome3d(
                            h -> h.is(payload.biomeKey()),
                            playerPos,
                            6400,
                            32,
                            64
                    );

                    if (result == null) {
                        ServerPlayNetworking.send(player,
                                new BiomeResultPayload(payload.biomeKey(), false, 0, 0));
                    } else {
                        ServerPlayNetworking.send(player,
                                new BiomeResultPayload(payload.biomeKey(), true,
                                        result.getFirst().getX(), result.getFirst().getZ()));
                    }
                })
        );
    }
}
