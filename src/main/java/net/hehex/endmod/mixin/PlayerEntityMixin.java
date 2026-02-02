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

    // Rejestrujemy zmienną stealth jako TrackedData, żeby klient o niej wiedział (do renderowania pasków/tooltipów)
    @Unique
    private static final TrackedData<Float> CURRENT_STEALTH = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);

    @Unique
    private ItemStack lastMainHandStack = ItemStack.EMPTY;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    protected void initStealthData(DataTracker.Builder builder, CallbackInfo ci) {
        // W 1.21 initDataTracker używa buildera
        builder.add(CURRENT_STEALTH, 0.0f);
    }

    @Override
    public float getStealth() {
        return this.dataTracker.get(CURRENT_STEALTH);
    }

    @Override
    public void setStealth(float stealth) {
        this.dataTracker.set(CURRENT_STEALTH, stealth);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickStealth(CallbackInfo ci) {
        if (this.getWorld().isClient) return; // Logika tylko po stronie serwera

        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack currentStack = player.getMainHandStack();

        // Sprawdź czy gracz zmienił broń - jeśli tak, reset stealth
        if (!ItemStack.areItemsEqual(currentStack, lastMainHandStack)) {
            this.setStealth(0);
            lastMainHandStack = currentStack.copy();
            return;
        }

        // Logika generowania
        if (currentStack.getItem() instanceof AssassinWeapon) {
            // Sprawdź czy przedmiot NIE jest w offhandzie (tutaj sprawdzamy mainhand, więc jest ok,
            // ale musimy upewnić się, że to nie działa, gdy assasin jest w offhandzie a w mainhandzie co innego)
            // Kod powyżej pobiera MainHandStack, więc jeśli AssasinWeapon jest w offhandzie, ten warunek nie przejdzie.

            // Warunek: nie atakowanie. Sprawdzamy cooldown ataku.
            // Jeśli jest w pełni naładowany (1.0), to znaczy że gracz nie atakuje od jakiegoś czasu.
            if (this.getAttackCooldownProgress(0.0f) >= 1.0f) {
                float maxStealth = (float) player.getAttributeValue(ModAttributes.MAX_STEALTH);
                float regen = (float) player.getAttributeValue(ModAttributes.STEALTH_REGEN);
                float current = this.getStealth();

                if (current < maxStealth) {
                    this.setStealth(Math.min(current + regen, maxStealth));
                }
            }
        } else {
            // Jeśli trzymasz coś innego, stealth spada do 0
            if (this.getStealth() > 0) {
                this.setStealth(0);
            }
        }
    }

    // Modyfikacja obrażeń
    @ModifyVariable(method = "attack", at = @At(value = "STORE", ordinal = 0), name = "f") // 'f' to zazwyczaj baseDamage w metodzie attack
    private float modifyDamageWithStealth(float damage) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack stack = player.getMainHandStack();

        if (stack.getItem() instanceof AssassinWeapon) {
            float stealth = this.getStealth();
            if (stealth > 0) {
                // Mechanika: każde 10 stealth zwiększa obrażenia o 25% (0.25)
                // Wzór: Base * (1 + (Stealth / 10) * 0.25)
                float multiplier = 1.0f + (stealth / 10.0f) * 0.25f;
                return damage * multiplier;
            }
        }
        return damage;
    }

    // Obsługa efektu 100% stealth (Stealth Strike)
    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private void onStealthAttack(Entity target, CallbackInfo ci) {
        if (target instanceof LivingEntity livingTarget) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            ItemStack stack = player.getMainHandStack();

            if (stack.getItem() instanceof AssassinWeapon assassinWeapon) {
                float stealth = this.getStealth();
                float maxStealth = (float) player.getAttributeValue(ModAttributes.MAX_STEALTH);

                // Sprawdź czy to "Pełny Stealth Strike" (z małym marginesem błędu dla liczb zmiennoprzecinkowych)
                if (stealth >= maxStealth - 0.1f) {
                    assassinWeapon.onStealthStrike(stack, player, livingTarget);

                    // Efekt wizualny/dźwiękowy (opcjonalnie)
                    player.getWorld().sendEntityStatus(player, (byte) 42); // Przykładowy status, można dodać własne particles
                }

                // Reset stealth po ataku
                this.setStealth(0);
            }
        }
    }
}