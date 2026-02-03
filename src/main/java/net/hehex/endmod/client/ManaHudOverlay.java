package net.hehex.endmod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.hehex.endmod.EndMod;
import net.hehex.endmod.attribute.ModAttributes;
import net.hehex.endmod.util.ManaAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class ManaHudOverlay implements HudRenderCallback {
    // Musisz stworzyć teksturę mana_bar.png (np. 9x9 pikseli ikonka kuli many)
    private static final Identifier MANA_ICON = Identifier.of(EndMod.MOD_ID, "textures/gui/mana_icon.png");
    private static final Identifier MANA_ICON_EMPTY = Identifier.of(EndMod.MOD_ID, "textures/gui/mana_icon_empty.png");

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && !client.player.isSpectator() && !client.player.isCreative()) {
            int width = client.getWindow().getScaledWidth();
            int height = client.getWindow().getScaledHeight();

            ManaAccessor manaAcc = (ManaAccessor) client.player;
            float currentMana = manaAcc.getMana();
            float maxMana = (float) client.player.getAttributeValue(ModAttributes.MAX_MANA);

            int x = width - 10; // Prawy róg z marginesem
            int y = 10;         // Górny róg z marginesem

            // Renderowanie paska (proste ikonki jedna pod drugą lub obok siebie)
            // Tutaj przykład paska tekstowego lub ikonkowego. Zróbmy pasek ikon jak serca.
            // Przyjmijmy że jedna ikona to 10 many.

            int totalIcons = (int) Math.ceil(maxMana / 10.0f);
            int fullIcons = (int) (currentMana / 10.0f);

            RenderSystem.enableBlend();

            // Renderujemy od prawej do lewej
            for (int i = 0; i < totalIcons; i++) {
                int offset = i * 8; // Odstęp 8 pixeli

                if (i < fullIcons) {
                    drawContext.drawTexture(MANA_ICON, x - offset, y, 0, 0, 9, 9, 9, 9);
                } else {
                    drawContext.drawTexture(MANA_ICON_EMPTY, x - offset, y, 0, 0, 9, 9, 9, 9);
                }
            }

            RenderSystem.disableBlend();
        }
    }
}