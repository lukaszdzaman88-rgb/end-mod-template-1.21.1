package net.hehex.endmod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.hehex.endmod.EndMod; // Naprawa błedu linii 18 (brak importu)
import net.hehex.endmod.attribute.ModAttributes;
import net.hehex.endmod.util.ManaAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter; // Naprawa błedu linii 13 (nowy typ w 1.21)
import net.minecraft.util.Identifier;

public class ManaHudOverlay implements HudRenderCallback {
    // Upewnij się, że masz te pliki w assets/endmod/textures/gui/
    private static final Identifier MANA_ICON = Identifier.of(EndMod.MOD_ID, "textures/gui/mana_icon.png");
    private static final Identifier MANA_ICON_EMPTY = Identifier.of(EndMod.MOD_ID, "textures/gui/mana_icon_empty.png");

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) { // Zmiana sygnatury dla 1.21
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null && !client.player.isSpectator() && !client.player.isCreative()) {
            // Sprawdzenie czy atrybut istnieje, aby uniknąć crasha przy logowaniu
            if (!client.player.getAttributes().hasAttribute(ModAttributes.MAX_MANA)) return;

            int width = client.getWindow().getScaledWidth();
            // int height = client.getWindow().getScaledHeight(); // Nieużywane obecnie

            ManaAccessor manaAcc = (ManaAccessor) client.player;
            float currentMana = manaAcc.getMana();
            float maxMana = (float) client.player.getAttributeValue(ModAttributes.MAX_MANA);

            int x = width - 20; // Prawy róg
            int y = 40;         // Nieco w dół

            // Logika wyświetlania: 1 ikonka = 10 many
            int totalIcons = (int) Math.ceil(maxMana / 10.0f);
            int fullIcons = (int) (currentMana / 10.0f);

            int maxIconsRow = 10; // Max 10 ikon w rzędzie

            RenderSystem.enableBlend();

            for (int i = 0; i < totalIcons; i++) {
                int row = i / maxIconsRow;
                int col = i % maxIconsRow;

                // Rysowanie od prawej do lewej
                int xPos = x - (col * 8);
                int yPos = y + (row * 10);

                if (i < fullIcons) {
                    drawContext.drawTexture(MANA_ICON, xPos, yPos, 0, 0, 9, 9, 9, 9);
                } else {
                    drawContext.drawTexture(MANA_ICON_EMPTY, xPos, yPos, 0, 0, 9, 9, 9, 9);
                }
            }

            RenderSystem.disableBlend();
        }
    }
}