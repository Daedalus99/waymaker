package com.daedalus.waymaker.item;

import com.daedalus.waymaker.client.screen.BiomeSelectScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class ElementalCompassItem extends Item {

    public ElementalCompassItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            openBiomeSelectScreen();
        }
        return InteractionResult.SUCCESS;
    }

    @Environment(EnvType.CLIENT)
    private void openBiomeSelectScreen() {
        net.minecraft.client.Minecraft.getInstance().setScreenAndShow(new BiomeSelectScreen());
    }
}
