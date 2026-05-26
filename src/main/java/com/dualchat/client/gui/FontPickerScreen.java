package com.dualchat.client.gui;

import com.dualchat.client.FontConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class FontPickerScreen extends Screen {

    private static final int LIST_TOP    = 40;
    private static final int LIST_BOTTOM = 56;
    private static final int ITEM_H      = 36;

    private final Screen parent;
    private FontList list;

    public FontPickerScreen(@Nullable Screen parent) {
        super(Component.literal("Choix de la Police"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        list = new FontList(minecraft, width, height, LIST_TOP, height - LIST_BOTTOM, ITEM_H);
        addWidget(list);
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> onClose())
            .bounds(width / 2 - 100, height - 38, 200, 20)
            .build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        list.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(font, title, width / 2, 16, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    // ── Scrollable font list ─────────────────────────────────────────────────

    class FontList extends ObjectSelectionList<FontList.Entry> {

        FontList(Minecraft mc, int w, int h, int top, int bottom, int itemH) {
            super(mc, w, h, top, bottom, itemH);
            for (FontConfig.FontEntry fe : FontConfig.FONTS) {
                addEntry(new Entry(fe));
            }
            children().stream()
                .filter(e -> e.entry.id().equals(FontConfig.getCurrent()))
                .findFirst()
                .ifPresent(this::setSelected);
        }

        @Override protected int getScrollbarPosition() { return width - 6; }
        @Override public    int getRowWidth()           { return Math.min(width - 20, 300); }

        class Entry extends ObjectSelectionList.Entry<Entry> {

            final FontConfig.FontEntry entry;

            Entry(FontConfig.FontEntry fe) {
                this.entry = fe;
            }

            @Override
            public void render(GuiGraphics guiGraphics, int index, int top, int left, int w, int h,
                               int mx, int my, boolean hovered, float pt) {

                boolean selected = (this == getSelected());
                int textX = left + 10;
                int nameColor = selected ? 0xFFFFAA : (hovered ? 0xFFFFFF : 0xCCCCCC);

                Component nameComp = Component.literal(entry.name())
                    .withStyle(Style.EMPTY.withFont(entry.id()));
                guiGraphics.drawString(FontPickerScreen.this.font, nameComp, textX, top + 6, nameColor, false);
                guiGraphics.drawString(FontPickerScreen.this.font, entry.description(), textX + 2, top + 20, 0x888888, false);
            }

            @Override
            public boolean mouseClicked(double mx, double my, int btn) {
                setSelected(this);
                FontConfig.setCurrent(entry.id());
                return true;
            }

            @Override
            public Component getNarration() {
                return Component.literal(entry.name());
            }
        }
    }
}
