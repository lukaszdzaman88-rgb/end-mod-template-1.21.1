package net.hehex.endmod.util;


import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.hehex.endmod.item.custom.ModBowItem;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ModTooltips {

    public static void init() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {

            // --- 1. AUTOMATYCZNY TOOLTIP DLA TWOICH ŁUKÓW ---
            if (stack.getItem() instanceof ModBowItem customBow) {
                lines.add(ScreenTexts.EMPTY);
                lines.add(Text.translatable("item.modifiers.mainhand").formatted(Formatting.GRAY));

                // A. Obrażenia - pobieramy z getRangedDamage()!
                // %.1f oznacza formatowanie do 1 miejsca po przecinku (np. "6.0")
                // Jeśli chcesz liczbę całkowitą (bez .0), użyj "%.0f"
                String damageText = String.format(" %.1f Ranged Damage", customBow.getRangedDamage());

                lines.add(Text.literal(damageText).formatted(Formatting.DARK_GREEN));

                // B. Szybkość naciągu
                float speedInSeconds = customBow.getDrawSpeed() / 20.0f;
                String speedText = String.format(" %.1fs Pull Speed", speedInSeconds);
                lines.add(Text.literal(speedText).formatted(Formatting.DARK_GREEN));
            }

            // --- 2. WANILIOWY ŁUK ---
            else if (stack.isOf(Items.BOW)) {
                lines.add(ScreenTexts.EMPTY);
                lines.add(Text.translatable("item.modifiers.mainhand").formatted(Formatting.GRAY));
                lines.add(Text.literal(" 8 Ranged Damage").formatted(Formatting.DARK_GREEN));
                lines.add(Text.literal(" 1.0s Pull Speed").formatted(Formatting.DARK_GREEN));
            }

            // --- 3. KUSZA ---
            else if (stack.isOf(Items.CROSSBOW)) {
                lines.add(ScreenTexts.EMPTY);
                lines.add(Text.translatable("item.modifiers.mainhand").formatted(Formatting.GRAY));
                lines.add(Text.literal(" 10 Ranged Damage").formatted(Formatting.DARK_GREEN));
                lines.add(Text.literal(" 1.25s Pull Speed").formatted(Formatting.DARK_GREEN));
            }
        });
    }
}