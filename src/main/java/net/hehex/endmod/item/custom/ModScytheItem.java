package net.hehex.endmod.item.custom;



import net.hehex.endmod.damage.ModDamageTypes;
import net.hehex.endmod.util.ScytheBooster;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModScytheItem extends Item {

    private final float baseScytheDamage;
    private final float attackSpeed;
    private final float lifestealAmount; // NOWA ZMIENNA

    // Zaktualizowany konstruktor przyjmuje teraz lifestealAmount
    public ModScytheItem(ToolMaterial material, float attackDamage, float attackSpeed, float lifestealAmount, Settings settings) {
        super(settings.component(DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createHiddenAttributes(attackSpeed)));

        this.baseScytheDamage = attackDamage + material.getAttackDamage();
        this.attackSpeed = attackSpeed;
        this.lifestealAmount = lifestealAmount; // Zapisujemy wartość
    }

    public float getLifestealAmount() {
        return this.lifestealAmount;
    }

    private static AttributeModifiersComponent createHiddenAttributes(float speed) {
        return AttributeModifiersComponent.builder()
                .add(
                        EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(
                                Identifier.of("endmod", "base_scythe_damage_placeholder"),
                                0.0,
                                EntityAttributeModifier.Operation.ADD_VALUE
                        ),
                        AttributeModifierSlot.MAINHAND
                )
                .add(
                        EntityAttributes.GENERIC_ATTACK_SPEED,
                        new EntityAttributeModifier(
                                Identifier.of("endmod", "base_scythe_speed"),
                                speed,
                                EntityAttributeModifier.Operation.ADD_VALUE
                        ),
                        AttributeModifierSlot.MAINHAND
                )
                .build().withShowInTooltip(false);

    }

    // --- KALKULATOR BONUSÓW ---
    private double calculateTotalDamageBonus(PlayerEntity player) {
        double totalBonus = 0.0;
        for (ItemStack armorStack : player.getInventory().armor) {
            if (!armorStack.isEmpty() && armorStack.getItem() instanceof ScytheBooster booster) {
                totalBonus += booster.getScytheDamageBonus(player, armorStack);
            }
        }
        return totalBonus;
    }

    // --- TOOLTIP ---
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.modifiers.mainhand").formatted(Formatting.GRAY));

        float calculatedDamage = this.baseScytheDamage;
        boolean isBuffed = false;

        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            double bonusPercent = calculateTotalDamageBonus(player);
            if (bonusPercent > 0) {
                calculatedDamage = (float) (this.baseScytheDamage * (1.0 + bonusPercent));
                isBuffed = true;
            }
        }

        float displayDamage = 1.0f + calculatedDamage;
        String damageString = (displayDamage == (long) displayDamage) ?
                String.format("%d", (long) displayDamage) : String.format("%.1f", displayDamage);

        Formatting color = isBuffed ? Formatting.GOLD : Formatting.DARK_GREEN;

        tooltip.add(Text.literal(" ")
                .append(Text.literal(damageString))
                .append(Text.literal(" Scythe Damage"))
                .formatted(color));

        // Dodajemy info o lifestealu (opcjonalnie)
        if (this.lifestealAmount > 0) {
            tooltip.add(Text.literal(" ")
                    .append(Text.literal("On Crit: Heals " + (int)this.lifestealAmount + " HP"))
                    .formatted(Formatting.DARK_RED));
        }

        float totalSpeed = 4.0f + this.attackSpeed;
        String speedString = (totalSpeed == (long) totalSpeed) ?
                String.format("%d", (long) totalSpeed) : String.format("%.1f", totalSpeed);

        tooltip.add(Text.literal(" ")
                .append(Text.literal(speedString))
                .append(Text.literal(" "))
                .append(Text.translatable("attribute.name.generic.attack_speed"))
                .formatted(Formatting.DARK_GREEN));

        super.appendTooltip(stack, context, tooltip, type);
    }

    // --- POST HIT (Zadawanie Obrażeń) ---
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient) {
            float realDamage = this.baseScytheDamage;

            if (attacker instanceof PlayerEntity player) {
                double bonusPercent = calculateTotalDamageBonus(player);
                realDamage *= (1.0 + bonusPercent);
            }

            DamageSource reaperSource = new DamageSource(
                    attacker.getWorld().getRegistryManager()
                            .get(RegistryKeys.DAMAGE_TYPE)
                            .getEntry(ModDamageTypes.SCYTHE_DAMAGE)
                            .get(),
                    attacker
            );

            target.timeUntilRegen = 0;
            target.damage(reaperSource, realDamage);
            stack.damage(1, attacker, EquipmentSlot.MAINHAND);
        }
        return true;
    }
}