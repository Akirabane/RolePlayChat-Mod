package com.dualchat.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public final class FontConfig {
    private FontConfig() {}

    public record FontEntry(String name, String description, ResourceLocation id) {}

    public static final List<FontEntry> FONTS = List.of(
        new FontEntry("Fraktur",       "Gothique noire médiévale",   new ResourceLocation("dualchat", "medieval")),
        new FontEntry("Almendra",      "Lettres de parchemin",       new ResourceLocation("dualchat", "almendra")),
        new FontEntry("Unciale",       "Manuscrit celtique",         new ResourceLocation("dualchat", "uncial")),
        new FontEntry("Metamorphous",  "Grimoire des anciens",       new ResourceLocation("dualchat", "metamorphous")),
        new FontEntry("Pirata",        "Écriture des corsaires",     new ResourceLocation("dualchat", "pirataone"))
    );

    private static final Path CONFIG_FILE = FMLPaths.CONFIGDIR.get().resolve("dualchat_font.txt");

    private static ResourceLocation current = FONTS.get(0).id();
    private static Font installedFont = null;

    public static ResourceLocation getCurrent() {
        return current;
    }

    public static FontEntry getCurrentEntry() {
        return FONTS.stream().filter(e -> e.id().equals(current)).findFirst().orElse(FONTS.get(0));
    }

    public static void setCurrent(ResourceLocation font) {
        current = font;
        save();
        ClientChannelState.refreshCurrentChannel();
    }

    /**
     * Wraps Minecraft.font so every lookup of minecraft:default is redirected
     * to the currently selected font at render time. Called once at startup,
     * and re-called whenever mc.font is replaced (e.g. after F3+T reload).
     */
    public static void installFontRedirect() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;
        try {
            Field mcFontField = Minecraft.class.getDeclaredField("font");
            mcFontField.setAccessible(true);
            Font mcFont = (Font) mcFontField.get(mc);
            if (mcFont == null) return;

            Field fontsField = Font.class.getDeclaredField("fonts");
            fontsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Function<ResourceLocation, FontSet> original =
                (Function<ResourceLocation, FontSet>) fontsField.get(mcFont);

            Field fishyField = Font.class.getDeclaredField("filterFishyGlyphs");
            fishyField.setAccessible(true);
            boolean fishy = fishyField.getBoolean(mcFont);

            ResourceLocation defaultId = new ResourceLocation("minecraft", "default");
            Font redirected = new Font(
                loc -> defaultId.equals(loc) ? original.apply(current) : original.apply(loc),
                fishy
            );
            mcFontField.set(mc, redirected);
            installedFont = redirected;
        } catch (Exception ignored) {}
    }

    /** Called every tick to re-install the redirect after a resource reload. */
    public static void ensureFontRedirect() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;
        try {
            Field mcFontField = Minecraft.class.getDeclaredField("font");
            mcFontField.setAccessible(true);
            Font mcFont = (Font) mcFontField.get(mc);
            if (mcFont != null && mcFont != installedFont) {
                installFontRedirect();
            }
        } catch (Exception ignored) {}
    }

    public static void load() {
        try {
            if (Files.exists(CONFIG_FILE)) {
                String saved = Files.readString(CONFIG_FILE).trim();
                ResourceLocation rl = ResourceLocation.tryParse(saved);
                if (rl != null && FONTS.stream().anyMatch(e -> e.id().equals(rl))) {
                    current = rl;
                }
            }
        } catch (IOException ignored) {}
    }

    private static void save() {
        try {
            Files.writeString(CONFIG_FILE, current.toString());
        } catch (IOException ignored) {}
    }
}
