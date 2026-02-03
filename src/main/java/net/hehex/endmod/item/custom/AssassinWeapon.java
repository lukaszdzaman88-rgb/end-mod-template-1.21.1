package net.hehex.endmod.item.custom;

import net.hehex.endmod.attribute.ModAttributes;
import net.hehex.endmod.util.AssassinPlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.List;

public interface AssassinWeapon {

    void onStealthStrike(ItemStack stack, PlayerEntity attacker, LivingEntity target);
    float getAssassinDamage(); // To zwraca bazowe obrażenia broni

    static void appendTooltip(ItemStack stack, Item.TooltipContext context, TooltipType type, List<Text> lines) {
        if (!(stack.getItem() instanceof AssassinWeapon weapon)) {
            return;
        }

        // Pobierz bazowe obrażenia z broni
        float baseDamage = weapon.getAssassinDamage();
        // Dodaj 1.0f bo gracz zawsze bije z siłą pięści (1) + broń
        float totalBaseDamage = baseDamage + 1.0f;

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player != null && player instanceof AssassinPlayer assassinPlayer) {
            float currentStealth = assassinPlayer.getStealth();
            float maxStealth = (float) player.getAttributeValue(ModAttributes.MAX_STEALTH);

            // Oblicz mnożnik
            float multiplier = 1.0f + (currentStealth / 10.0f) * 0.25f;
            float finalDamage = totalBaseDamage * multiplier;

            lines.add(Text.empty());

            // Pasek Stealth
            lines.add(Text.literal("Stealth: ").formatted(Formatting.AQUA)
                    .append(Text.literal(String.format("%.1f / %.1f", currentStealth, maxStealth)).formatted(Formatting.WHITE)));

            // GŁÓWNA LINIA OBRAŻEŃ (Zastępuje "Melee Damage")
            lines.add(Text.literal(String.format(" %.1f Assassin Damage", finalDamage)).formatted(Formatting.DARK_PURPLE));

            // Informacja o bonusie (opcjonalnie)
            int chunks = (int) (currentStealth / 10.0f);
            if (chunks > 0) {
                lines.add(Text.literal(" (Bonus: +" + (int)(chunks * 25) + "%)").formatted(Formatting.GRAY));
            }

            if (currentStealth >= maxStealth - 0.9f) {
                lines.add(Text.literal("★ STEALTH STRIKE READY ★").formatted(Formatting.GOLD, Formatting.BOLD));
                // Tu możesz dodać opis efektu broni, np.:
                lines.add(Text.literal("Effect: Speed I (5s)").formatted(Formatting.BLUE));
            }
        } else {
            // Fallback, gdy nie ma gracza (np. w menu kreatywnym)
            lines.add(Text.literal(String.format(" %.1f Assassin Damage", totalBaseDamage)).formatted(Formatting.DARK_PURPLE));
        }

        lines.add(Text.translatable("tooltip.endmod.assassin_weapon").formatted(Formatting.GRAY));
    }
}