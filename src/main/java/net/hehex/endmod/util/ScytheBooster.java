package net.hehex.endmod.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface ScytheBooster {
    /**
     * Zwraca procentowy bonus do obrażeń (np. 0.05 dla 5%).
     */
    double getScytheDamageBonus(PlayerEntity player, ItemStack stack);

    /**
     * Zwraca procentowy bonus do szybkości ataku.
     */
    double getScytheSpeedBonus(PlayerEntity player, ItemStack stack);
}