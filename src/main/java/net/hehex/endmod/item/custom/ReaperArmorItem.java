package net.hehex.endmod.item.custom;


import net.hehex.endmod.util.ScytheBooster;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

// IMPLEMENTUJEMY SCYTHEBOOSTER
public class ReaperArmorItem extends ArmorItem implements ScytheBooster {

    private static final Identifier BOOTS_SPEED_ID = Identifier.of("endmod", "reaper_boots_passive");

    public ReaperArmorItem(RegistryEntry<ArmorMaterial> material, Type type, Settings settings) {
        super(material, type, adjustSettings(type, settings));
    }

    // --- 1. PASYWNE BONUSY (STAŁE, NP. SZYBKOŚĆ RUCHU W BUTACH) ---
    private static Settings adjustSettings(Type type, Settings settings) {
        if (type == Type.BOOTS) {
            AttributeModifiersComponent modifiers = AttributeModifiersComponent.builder()
                    .add(
                            EntityAttributes.GENERIC_MOVEMENT_SPEED,
                            new EntityAttributeModifier(
                                    BOOTS_SPEED_ID,
                                    0.10, // +10% Speed
                                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                            ),
                            AttributeModifierSlot.FEET
                    )
                    .build();
            return settings.component(DataComponentTypes.ATTRIBUTE_MODIFIERS, modifiers);
        }
        return settings;
    }

    // --- 2. IMPLEMENTACJA BONUSÓW DLA KOSY (SCYTHEBOOSTER) ---

    @Override
    public double getScytheDamageBonus(PlayerEntity player, ItemStack stack) {
        double bonus = 0.0;

        // Bonus za Klatę (+5%)
        if (this.getType() == Type.CHESTPLATE) {
            bonus += 0.05;
        }

        // Bonus za Set (+20%) - Liczymy go tylko przy Hełmie, żeby nie dodać go 4 razy
        if (this.getType() == Type.HELMET && hasFullSet(player)) {
            bonus += 0.05;
        }

        return bonus;
    }

    @Override
    public double getScytheSpeedBonus(PlayerEntity player, ItemStack stack) {
        double bonus = 0.0;

        // Bonus za Hełm (+5%)
        if (this.getType() == Type.HELMET) {
            bonus += 0.05;

            // Bonus za Set (+10%)
            if (hasFullSet(player)) {
                bonus += 0.10;
            }
        }
        return bonus;
    }

    // --- 3. METODY POMOCNICZE ---
    private boolean hasFullSet(PlayerEntity player) {
        return isReaper(player, 0) && isReaper(player, 1) &&
                isReaper(player, 2) && isReaper(player, 3);
    }

    private boolean isReaper(PlayerEntity player, int slotIndex) {
        ItemStack stack = player.getInventory().getArmorStack(slotIndex);
        return !stack.isEmpty() && stack.getItem() instanceof ReaperArmorItem;
    }

    // --- 4. TOOLTIPY SAMEJ ZBROI ---
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (this.getType() == Type.BOOTS) tooltip.add(Text.literal("10% increased Movement Speed").formatted(Formatting.BLUE));
        if (this.getType() == Type.CHESTPLATE) tooltip.add(Text.literal("5% increased Scythe Damage").formatted(Formatting.BLUE));
        if (this.getType() == Type.HELMET) tooltip.add(Text.literal("5% increased Scythe Speed").formatted(Formatting.BLUE));

        tooltip.add(Text.literal(" "));
        tooltip.add(Text.literal("Set Bonus: 5% increased scythe damage and 10% increased scythe speed").formatted(Formatting.GOLD));


        super.appendTooltip(stack, context, tooltip, type);
    }
}

