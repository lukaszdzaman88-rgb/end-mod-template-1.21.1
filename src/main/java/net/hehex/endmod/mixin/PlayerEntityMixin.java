package net.hehex.endmod.mixin;

import net.hehex.endmod.attribute.ModAttributes;
import net.hehex.endmod.item.custom.AssassinWeapon;
import net.hehex.endmod.util.AssassinPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements AssassinPlayer {

    @Shadow public abstract float getAttackCooldownProgress(float baseTime);
    @Shadow public abstract void resetLastAttackedTicks();

    // 1. Rejestracja zmiennej Stealth (widocznej dla klienta i serwera)
    @Unique
    private static final TrackedData<Float> CURRENT_STEALTH = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);

    @Unique
    private ItemStack lastMainHandStack = ItemStack.EMPTY;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    // 2. Inicjalizacja danych (Wersja 1.21 używa buildera)
    @Inject(method = "initDataTracker", at = @At("TAIL"))
    protected void initStealthData(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(CURRENT_STEALTH, 0.0f);
    }

    // Implementacja interfejsu AssassinPlayer
    @Override
    public float getStealth() {
        return this.dataTracker.get(CURRENT_STEALTH);
    }

    @Override
    public void setStealth(float stealth) {
        this.dataTracker.set(CURRENT_STEALTH, stealth);
    }

    // 3. Główna logika (uruchamiana co tick gry)
    @Inject(method = "tick", at = @At("TAIL"))
    private void tickStealth(CallbackInfo ci) {
        if (this.getWorld().isClient) return; // Tylko serwer oblicza stealth

        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack currentStack = player.getMainHandStack();

        // Jeśli zmieniono broń -> reset stealth
        if (!ItemStack.areItemsEqual(currentStack, lastMainHandStack)) {
            this.setStealth(0);
            lastMainHandStack = currentStack.copy();
            return;
        }

        // Jeśli trzymasz broń Assasina
        if (currentStack.getItem() instanceof AssassinWeapon) {
            // Sprawdź czy gracz ma zarejestrowane atrybuty (ważne przy crashach!)
            if (player.getAttributes().hasAttribute(ModAttributes.MAX_STEALTH)) {

                // Warunek: Cooldown ataku pełny (1.0) = gracz nie atakuje
                if (this.getAttackCooldownProgress(0.0f) >= 1.0f) {
                    float maxStealth = (float) player.getAttributeValue(ModAttributes.MAX_STEALTH);
                    float regen = (float) player.getAttributeValue(ModAttributes.STEALTH_REGEN);
                    float current = this.getStealth();

                    if (current < maxStealth) {
                        this.setStealth(Math.min(current + regen, maxStealth));
                    }
                }
            }
        } else {
            // Jeśli to nie broń assasina, zresetuj stealth
            if (this.getStealth() > 0) {
                this.setStealth(0);
            }
        }
    }

    // 4. Modyfikacja obrażeń (Bonus 25% za każde 10 stealth)
    @ModifyVariable(method = "attack", at = @At(value = "STORE", ordinal = 0), name = "f")
    private float modifyDamageWithStealth(float damage) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack stack = player.getMainHandStack();

        if (stack.getItem() instanceof AssassinWeapon) {
            float stealth = this.getStealth();
            if (stealth > 0) {
                // Wzór: Każde 10 pkt = +25%
                float multiplier = 1.0f + (stealth / 10.0f) * 0.25f;
                return damage * multiplier;
            }
        }
        return damage;
    }

    // 5. Efekt Specjalny (Stealth Strike) przy ataku
    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private void onStealthAttack(Entity target, CallbackInfo ci) {
        if (target instanceof LivingEntity livingTarget) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            ItemStack stack = player.getMainHandStack();

            if (stack.getItem() instanceof AssassinWeapon assassinWeapon) {
                // Ponowne sprawdzenie atrybutów dla bezpieczeństwa
                if (!player.getAttributes().hasAttribute(ModAttributes.MAX_STEALTH)) return;

                float stealth = this.getStealth();
                float maxStealth = (float) player.getAttributeValue(ModAttributes.MAX_STEALTH);

                // Jeśli stealth jest pełny (z marginesem błędu)
                if (stealth >= maxStealth - 0.5f) {
                    // Wywołaj specjalny efekt broni
                    assassinWeapon.onStealthStrike(stack, player, livingTarget);

                    // Efekt dźwiękowy/wizualny (krytyk)
                    player.getWorld().sendEntityStatus(target, (byte) 4);
                }

                // Reset stealth po ataku
                this.setStealth(0);
            }
        }
    }
}