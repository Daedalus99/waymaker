package com.daedalus.waymaker.item;

import com.daedalus.waymaker.Waymaker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;

public class WaymakersRelicItem extends Item {
    public WaymakersRelicItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public ItemStack finishUsingItem(final ItemStack stack, final Level level, final LivingEntity entity) {
        Player player = (Player) entity;
        if (!level.isClientSide()) {
            ServerPlayer p1 = (ServerPlayer) player;
            TeleportTransition player_spawn_location = p1.findRespawnPositionAndUseSpawnBlock(
                    false,
                    TeleportTransition.DO_NOTHING
            );

            this.playsound(level, player);
            LightningBolt bolt = EntityTypes.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
            if (bolt != null) {
                bolt.snapTo(player.getX(), player.getY(), player.getZ());
                bolt.setVisualOnly(true);
                level.addFreshEntity(bolt);
            }
            player.teleport(player_spawn_location);
            this.playsound(level, player);
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity player){
        // 2 seconds
        return 45;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack){
        return ItemUseAnimation.BRUSH;
    }

    private void playsound(Level level, Player player){
        double x = player.getX(), y = player.getY(), z = player.getZ();
        level.playSound(null, x, y, z, SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

    }
}
