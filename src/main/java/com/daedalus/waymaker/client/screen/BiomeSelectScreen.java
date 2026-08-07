package com.daedalus.waymaker.client.screen;

import com.daedalus.waymaker.network.CompassNetwork;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class BiomeSelectScreen extends Screen {

    private static final int ENTRY_HEIGHT = 20;
    private static final int LIST_TOP = 50;
    private static final int LIST_BOTTOM_MARGIN = 36;
    private static final int LIST_HALF_WIDTH = 102;

    private Button selectButton;

    // keySet() returns Set<Identifier> in this MC version
    private final List<Identifier> allBiomes = new ArrayList<>();
    private List<Identifier> filteredBiomes = new ArrayList<>();

    private int selectedIndex = -1;
    private int scrollOffset = 0;

    public BiomeSelectScreen() {
        super(Component.translatable("screen.waymaker.elemental_compass"));
    }

    @Override
    protected void init() {
        var biomeRegistry = Minecraft.getInstance().level.registryAccess()
                .lookupOrThrow(Registries.BIOME);

        allBiomes.clear();
        biomeRegistry.keySet().stream()
                .sorted((a, b) -> a.toString().compareTo(b.toString()))
                .forEach(id -> allBiomes.add(id));

        EditBox searchBox = new EditBox(font, width / 2 - 100, 22, 200, 20,
                Component.translatable("screen.waymaker.elemental_compass.search"));
        searchBox.setResponder(query -> {
            selectedIndex = -1;
            scrollOffset = 0;
            refreshFilter(query);
        });
        addRenderableWidget(searchBox);

        selectButton = Button.builder(
                Component.translatable("screen.waymaker.elemental_compass.select"),
                btn -> confirm()
        ).bounds(width / 2 - 101, height - 26, 100, 20).build();
        selectButton.active = false;
        addRenderableWidget(selectButton);

        addRenderableWidget(Button.builder(
                Component.translatable("gui.cancel"),
                btn -> onClose()
        ).bounds(width / 2 + 1, height - 26, 100, 20).build());

        refreshFilter("");
    }

    private void refreshFilter(String query) {
        String lower = query.toLowerCase();
        filteredBiomes = allBiomes.stream()
                .filter(id -> id.toString().toLowerCase().contains(lower))
                .toList();
        selectButton.active = selectedIndex >= 0 && selectedIndex < filteredBiomes.size();
    }

    private int listHeight()  { return height - LIST_TOP - LIST_BOTTOM_MARGIN; }
    private int visibleRows() { return listHeight() / ENTRY_HEIGHT; }
    private int listLeft()    { return width / 2 - LIST_HALF_WIDTH; }
    private int listRight()   { return width / 2 + LIST_HALF_WIDTH; }

    private void confirm() {
        if (selectedIndex < 0 || selectedIndex >= filteredBiomes.size()) return;
        Identifier id = filteredBiomes.get(selectedIndex);
        ResourceKey<Biome> key = ResourceKey.create(Registries.BIOME, id);
        ClientPlayNetworking.send(new CompassNetwork.BiomeRequestPayload(key));
        onClose();
    }

    @Override
    public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.fill(listLeft(), LIST_TOP, listRight(), LIST_TOP + listHeight(), 0xAA000000);
    }

    @Override
    public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        int left  = listLeft();
        int right = listRight();

        graphics.enableScissor(left, LIST_TOP, right, LIST_TOP + listHeight());
        for (int i = 0; i < visibleRows(); i++) {
            int dataIndex = i + scrollOffset;
            if (dataIndex >= filteredBiomes.size()) break;

            int entryTop = LIST_TOP + i * ENTRY_HEIGHT;

            if (dataIndex == selectedIndex) {
                graphics.fill(left, entryTop, right, entryTop + ENTRY_HEIGHT, 0x88AAAAFF);
            } else if (mouseX >= left && mouseX < right
                    && mouseY >= entryTop && mouseY < entryTop + ENTRY_HEIGHT) {
                graphics.fill(left, entryTop, right, entryTop + ENTRY_HEIGHT, 0x44FFFFFF);
            }

            String biomeName = filteredBiomes.get(dataIndex).getPath().replace('_', ' ');
            graphics.text(font, Component.literal(biomeName),
                    left + 4, entryTop + (ENTRY_HEIGHT - 8) / 2, 0xFFFFFFFF);
        }
        graphics.disableScissor();

        // Scrollbar
        if (filteredBiomes.size() > visibleRows()) {
            int trackHeight = listHeight();
            int thumbHeight = Math.max(10, trackHeight * visibleRows() / filteredBiomes.size());
            int maxScroll   = Math.max(1, filteredBiomes.size() - visibleRows());
            int thumbTop    = LIST_TOP + (trackHeight - thumbHeight) * scrollOffset / maxScroll;
            graphics.fill(right + 1, LIST_TOP, right + 4, LIST_TOP + trackHeight, 0x55FFFFFF);
            graphics.fill(right + 1, thumbTop, right + 4, thumbTop + thumbHeight, 0xFFAAAAAA);
        }
    }

    @Override
    public boolean mouseClicked(final MouseButtonEvent event, final boolean double_click) {
        double mouseX = event.x();
        double mouseY = event.y();
        int left  = listLeft();
        int right = listRight();
        if (!double_click && mouseX >= left && mouseX < right
                && mouseY >= LIST_TOP && mouseY < LIST_TOP + listHeight()) {
            int dataIndex = ((int) mouseY - LIST_TOP) / ENTRY_HEIGHT + scrollOffset;
            if (dataIndex < filteredBiomes.size()) {
                selectedIndex = dataIndex;
                selectButton.active = true;
            }
            return true;
        }
        return super.mouseClicked(event, double_click);
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY,
                                 final double scrollX, final double scrollY) {
        int maxScroll = Math.max(0, filteredBiomes.size() - visibleRows());
        scrollOffset  = (int) Math.clamp(scrollOffset - scrollY, 0, maxScroll);
        return true;
    }

    @Override
    public boolean keyPressed(final KeyEvent event) {
        if (event.key() == 257 && selectButton.active) { // GLFW_KEY_ENTER
            confirm();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
