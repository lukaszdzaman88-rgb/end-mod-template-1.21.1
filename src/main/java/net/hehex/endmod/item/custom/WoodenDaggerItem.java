package net.hehex.endmod.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class WoodenDaggerItem extends SwordItem implements AssassinWeapon {

    public WoodenDaggerItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public float getAssassinDamage() {
        return this.getMaterial().getAttackDamage() + 1.0f;
    }

    @Override
    public void onStealthStrike(ItemStack stack, PlayerEntity attacker, LivingEntity target) {
        attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 100, 0));
    }

    // NIE MA TU JUŻ METODY appendTooltip!
}