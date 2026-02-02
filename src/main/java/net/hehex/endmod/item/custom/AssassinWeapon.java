package net.hehex.endmod.item.custom;

import net.hehex.endmod.attribute.ModAttributes;
import net.hehex.endmod.util.AssassinPlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType; // Upewnij się co do importu w 1.21 (może być inne package)
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.List;

public interface AssassinWeapon {

    void onStealthStrike(ItemStack stack, PlayerEntity attacker, LivingEntity target);
    float getAssassinDamage();

    // === NOWA METODA STATYCZNA ===
    // Ta metoda zawiera całą logikę wyświetlania
    static void appendTooltip(ItemStack stack, Item.TooltipContext context, TooltipType type, List<Text> lines) {
        // Sprawdzamy czy przedmiot to w ogóle AssassinWeapon
        if (!(stack.getItem() instanceof AssassinWeapon)) {
            return;
        }

        // Dodajemy stały opis
        lines.add(Text.translatable("tooltip.endmod.assassin_weapon").formatted(Formatting.GRAY));

        // Bezpieczne pobranie klienta (tylko wizualnie)
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player != null && player instanceof AssassinPlayer assassinPlayer) {
            // Opcjonalnie: pokazywać statystyki tylko gdy trzymamy broń
            // if (player.getMainHandStack() != stack) return;

            float currentStealth = assassinPlayer.getStealth();
            float maxStealth = (float) player.getAttributeValue(ModAttributes.MAX_STEALTH);

            lines.add(Text.empty());
            lines.add(Text.literal("Stealth: ").formatted(Formatting.AQUA)
                    .append(Text.literal(String.format("%.1f / %.1f", currentStealth, maxStealth)).formatted(Formatting.WHITE)));

            int chunks = (int) (currentStealth / 10.0f);
            float damageBonusPct = chunks * 25.0f;

            if (damageBonusPct > 0) {
                lines.add(Text.literal("Assassin Damage: ").formatted(Formatting.DARK_PURPLE)
                        .append(Text.literal("+" + (int)damageBonusPct + "%").formatted(Formatting.RED)));
            }

            if (currentStealth >= maxStealth - 0.1f) {
                lines.add(Text.literal("★ STEALTH STRIKE READY ★").formatted(Formatting.GOLD, Formatting.BOLD));
            }
        }
    }
}