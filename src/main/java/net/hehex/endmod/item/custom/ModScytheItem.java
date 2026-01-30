package net.hehex.endmod.item.custom;


import net.hehex.endmod.damage.ModDamageTypes;
import net.hehex.endmod.util.ScytheBooster;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
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
import net.minecraft.world.World;

import java.util.List;

public class ModScytheItem extends Item {

    private final float baseScytheDamage;
    private final float attackSpeed;

    // Unikalne ID dla bonusu prędkości (żeby gra wiedziała, który to modyfikator)
    private static final Identifier DYNAMIC_SPEED_ID = Identifier.of("endmod", "scythe_dynamic_speed_bonus");

    public ModScytheItem(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
        super(settings.component(DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createHiddenAttributes(attackSpeed)));

        this.baseScytheDamage = attackDamage + material.getAttackDamage();
        this.attackSpeed = attackSpeed;
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

    // --- KALKULATORY BONUSÓW ---

    private double calculateTotalDamageBonus(PlayerEntity player) {
        double totalBonus = 0.0;
        for (ItemStack armorStack : player.getInventory().armor) {
            if (!armorStack.isEmpty() && armorStack.getItem() instanceof ScytheBooster booster) {
                totalBonus += booster.getScytheDamageBonus(player, armorStack);
            }
        }
        return totalBonus;
    }

    // Nowy kalkulator dla prędkości
    private double calculateTotalSpeedBonus(PlayerEntity player) {
        double totalBonus = 0.0;
        for (ItemStack armorStack : player.getInventory().armor) {
            if (!armorStack.isEmpty() && armorStack.getItem() instanceof ScytheBooster booster) {
                totalBonus += booster.getScytheSpeedBonus(player, armorStack);
            }
        }
        return totalBonus;
    }

    // --- LOGIKA AKTUALIZACJI PRĘDKOŚCI (DZIAŁA W TLE) ---
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player) {
            EntityAttributeInstance speedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED);

            if (speedAttr != null) {
                // Jeśli gracz TRZYMA tę kosę w głównej ręce
                if (selected) {
                    double speedBonus = calculateTotalSpeedBonus(player);

                    // Jeśli bonus się zmienił lub nie jest nałożony, aktualizujemy go
                    boolean hasModifier = speedAttr.hasModifier(DYNAMIC_SPEED_ID);
                    double currentBonusValue = hasModifier ? speedAttr.getModifier(DYNAMIC_SPEED_ID).value() : 0.0;

                    // Aktualizuj tylko jeśli wartość jest inna (optymalizacja)
                    if (speedBonus > 0 && (!hasModifier || Math.abs(currentBonusValue - speedBonus) > 0.001)) {
                        // Usuń stary
                        speedAttr.removeModifier(DYNAMIC_SPEED_ID);
                        // Dodaj nowy
                        speedAttr.addTemporaryModifier(new EntityAttributeModifier(
                                DYNAMIC_SPEED_ID,
                                speedBonus,
                                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                        ));
                    }
                    // Jeśli bonus wynosi 0, ale modyfikator wisi - usuń go
                    else if (speedBonus <= 0 && hasModifier) {
                        speedAttr.removeModifier(DYNAMIC_SPEED_ID);
                    }
                }
                // Jeśli gracz PRZESTAŁ trzymać kosę (np. zmienił slot), a bonus wisi - usuwamy go natychmiast
                else if (speedAttr.hasModifier(DYNAMIC_SPEED_ID)) {
                    speedAttr.removeModifier(DYNAMIC_SPEED_ID);
                }
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    // --- TOOLTIP ---
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.modifiers.mainhand").formatted(Formatting.GRAY));

        // --- Zmienne ---
        float calculatedDamage = this.baseScytheDamage;
        boolean isDamageBuffed = false;

        float calculatedSpeed = 4.0f + this.attackSpeed; // Baza 4.0 + prędkość broni
        boolean isSpeedBuffed = false;

        // --- Pobieranie danych z Klienta ---
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            // Damage
            double dmgBonus = calculateTotalDamageBonus(player);
            if (dmgBonus > 0) {
                calculatedDamage *= (1.0 + dmgBonus);
                isDamageBuffed = true;
            }

            // Speed
            double spdBonus = calculateTotalSpeedBonus(player);
            if (spdBonus > 0) {
                // Attack speed mnożymy przez (1 + bonus), bo to modyfikator MULTIPLIED_TOTAL
                calculatedSpeed *= (1.0 + spdBonus);
                isSpeedBuffed = true;
            }
        }

        // --- Wyświetlanie DAMAGE ---
        float displayDamage = 1.0f + calculatedDamage;
        String damageString = (displayDamage == (long) displayDamage) ?
                String.format("%d", (long) displayDamage) : String.format("%.1f", displayDamage);

        tooltip.add(Text.literal(" ")
                .append(Text.literal(damageString))
                .append(Text.literal(" Scythe Damage"))
                .formatted(isDamageBuffed ? Formatting.GOLD : Formatting.DARK_GREEN));

        // --- Wyświetlanie SPEED ---
        String speedString = (calculatedSpeed == (long) calculatedSpeed) ?
                String.format("%d", (long) calculatedSpeed) : String.format("%.1f", calculatedSpeed);

        tooltip.add(Text.literal(" ")
                .append(Text.literal(speedString))
                .append(Text.literal(" "))
                .append(Text.translatable("attribute.name.generic.attack_speed"))
                .formatted(isSpeedBuffed ? Formatting.GOLD : Formatting.DARK_GREEN));

        super.appendTooltip(stack, context, tooltip, type);
    }

    // --- POST HIT (Tylko Damage) ---
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