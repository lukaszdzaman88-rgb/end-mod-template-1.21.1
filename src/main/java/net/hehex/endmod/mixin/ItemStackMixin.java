package net.hehex.endmod.mixin;

import net.hehex.endmod.item.custom.AssassinWeapon;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    // Ta metoda w kodzie gry odpowiada za dodawanie napisów "When in main hand: ... Damage"
    @Inject(method = "appendAttributeModifiersTooltip", at = @At("HEAD"), cancellable = true)
    private void removeAssassinAttributes(Consumer<Text> textConsumer, @Nullable PlayerEntity player, CallbackInfo ci) {
        // Jeśli przedmiot jest AssasinWeapon -> ANULUJEMY standardowe wypisywanie atrybutów
        if (this.getItem() instanceof AssassinWeapon) {
            ci.cancel();
        }
    }
}