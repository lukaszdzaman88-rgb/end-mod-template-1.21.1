package net.hehex.endmod.item.custom;

import net.hehex.endmod.attribute.ModAttributes;
import net.hehex.endmod.util.AssassinPlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributes;
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
    float getAssassinDamage();

    static void appendTooltip(ItemStack stack, Item.TooltipContext context, TooltipType type, List<Text> lines) {
        if (!(stack.getItem() instanceof AssassinWeapon weapon)) {
            return;
        }

        float baseDamage = weapon.getAssassinDamage();
        float totalBaseDamage = baseDamage + 1.0f;

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player != null && player instanceof AssassinPlayer assassinPlayer) {
            float currentStealth = assassinPlayer.getStealth();
            float maxStealth = (float) player.getAttributeValue(ModAttributes.MAX_STEALTH);

            float multiplier = 1.0f + (currentStealth / 10.0f) * 0.25f;
            float finalDamage = totalBaseDamage * multiplier;

            lines.add(Text.empty());

            lines.add(Text.literal("Stealth: ").formatted(Formatting.AQUA)
                    .append(Text.literal(String.format("%.1f / %.1f", currentStealth, maxStealth)).formatted(Formatting.WHITE)));

            // === 1. WYŚWIETLANIE ASSASSIN DAMAGE (Fioletowe) ===
            lines.add(Text.literal(String.format(" %.1f Assassin Damage", finalDamage)).formatted(Formatting.DARK_PURPLE));

            int chunks = (int) (currentStealth / 10.0f);
            if (chunks > 0) {
                lines.add(Text.literal(" (Bonus: +" + (int)(chunks * 25) + "%)").formatted(Formatting.GRAY));
            }

            // === 2. PRZYWRÓCENIE ATTACK SPEED (Zielone) ===
            // Pobieramy modyfikatory przedmiotu
            AttributeModifiersComponent attributes = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
            double baseAttackSpeed = 4.0; // Bazowa prędkość gracza
            double speedBonus = 0.0;

            // Szukamy modyfikatora prędkości ataku dla głównej ręki
            for (var entry : attributes.modifiers()) {
                if (entry.attribute().matches(EntityAttributes.GENERIC_ATTACK_SPEED) &&
                        (entry.slot() == AttributeModifierSlot.MAINHAND || entry.slot() == AttributeModifierSlot.ANY)) {
                    speedBonus += entry.modifier().value();
                }
            }
            double finalSpeed = baseAttackSpeed + speedBonus;

            // Wyświetlamy w formacie Minecrafta: " 1.6 Attack Speed"
            lines.add(Text.literal(" ")
                    .append(Text.literal(String.format("%.1f", finalSpeed)))
                    .append(Text.literal(" Attack Speed")) // Można użyć Text.translatable("attribute.name.generic.attack_speed")
                    .formatted(Formatting.DARK_GREEN));
            // ===============================================

            if (currentStealth >= maxStealth - 0.9f) {
                lines.add(Text.literal("★ STEALTH STRIKE READY ★").formatted(Formatting.GOLD, Formatting.BOLD));
            }
        } else {
            lines.add(Text.literal(String.format(" %.1f Assassin Damage", totalBaseDamage)).formatted(Formatting.DARK_PURPLE));
        }

        lines.add(Text.translatable("tooltip.endmod.assassin_weapon").formatted(Formatting.GRAY));
    }
}