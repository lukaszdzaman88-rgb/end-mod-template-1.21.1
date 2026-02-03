package net.hehex.endmod.mixin;

import net.hehex.endmod.attribute.ModAttributes;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerAttributesMixin {

    @Inject(method = "createPlayerAttributes", at = @At("RETURN"))
    private static void addCustomAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        // Pobieramy builder atrybutów gracza i dodajemy nasze
        // Dzięki temu gra "myśli", że to są domyślne atrybuty Minecrafta
        cir.getReturnValue()
                .add(ModAttributes.MAX_STEALTH)
                .add(ModAttributes.STEALTH_REGEN);
    }
}
