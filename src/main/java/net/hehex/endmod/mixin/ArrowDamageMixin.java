package net.hehex.endmod.mixin;

import net.hehex.endmod.attribute.ModAttributes;
import net.hehex.endmod.item.custom.ModBowItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public abstract class ArrowDamageMixin {

    @Shadow public abstract void setDamage(double damage);

    @Inject(method = "setOwner", at = @At("TAIL"))
    private void modifyDamageBasedOnAttribute(Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity shooter) {

            if (ModAttributes.GENERIC_RANGED_DAMAGE == null) return;

            // 1. Statystyki z pancerza/efektów
            double attributeValue = shooter.getAttributeValue(ModAttributes.GENERIC_RANGED_DAMAGE);

            ItemStack heldItem = shooter.getMainHandStack();
            double baseWeaponDamage = 0.0;

            // 2. SPRAWDZANIE BRONI

            // A. Jeśli to Twój customowy łuk -> pobierz obrażenia z klasy!
            if (heldItem.getItem() instanceof ModBowItem customBow) {
                baseWeaponDamage = customBow.getRangedDamage();
            }
            // B. Jeśli to waniliowy łuk -> wpisane na sztywno
            else if (heldItem.isOf(Items.BOW)) {
                baseWeaponDamage = 8.0;
            }
            // C. Jeśli to kusza -> wpisane na sztywno
            else if (heldItem.isOf(Items.CROSSBOW)) {
                baseWeaponDamage = 10.0;
            }

            // 3. Obliczenia
            double finalRangedDamage = baseWeaponDamage + attributeValue;

            if (finalRangedDamage > 0) {
                this.setDamage(finalRangedDamage / 3.0);
            }
        }
    }
}