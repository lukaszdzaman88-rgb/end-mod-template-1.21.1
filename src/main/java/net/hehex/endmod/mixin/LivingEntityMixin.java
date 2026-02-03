package net.hehex.endmod.mixin;

import net.hehex.endmod.item.custom.AssassinWeapon;
import net.hehex.endmod.util.AssassinPlayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    // Wstrzykujemy się do swingHand w LivingEntity (bo PlayerEntity to dziedziczy)
    @Inject(method = "swingHand(Lnet/minecraft/util/Hand;)V", at = @At("HEAD"))
    private void onSwingCheck(Hand hand, CallbackInfo ci) {
        // Sprawdzamy, czy "to coś co macha ręką" jest Graczem (i Assasinem)
        if ((Object) this instanceof PlayerEntity player && player instanceof AssassinPlayer assassinPlayer) {

            if (player.getWorld().isClient) return; // Tylko serwer

            // Jeśli machamy ręką, ale flaga isAttacking() jest FALSE, to znaczy że nie trafiliśmy w cel (pudło w powietrze).
            if (!assassinPlayer.isAttacking()) {
                if (player.getMainHandStack().getItem() instanceof AssassinWeapon) {
                    assassinPlayer.setStealth(0); // Kara za pudło
                }
            }
        }
    }
}