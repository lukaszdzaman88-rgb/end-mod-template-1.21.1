package net.hehex.endmod.mixin;

import net.hehex.endmod.attribute.ModAttributes;
import net.hehex.endmod.item.custom.AssassinWeapon;
import net.hehex.endmod.util.AssassinPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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

    @Unique
    private static final TrackedData<Float> CURRENT_STEALTH = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);

    @Unique
    private ItemStack lastMainHandStack = ItemStack.EMPTY;

    // Flaga, czy atakujemy (używana, by nie resetować stealtha za wcześnie)
    @Unique
    private boolean isProcessingAttack = false;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    protected void initStealthData(DataTracker.Builder builder, CallbackInfo ci) {
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

    // Implementacja nowych metod z interfejsu
    @Override
    public boolean isAttacking() {
        return this.isProcessingAttack;
    }

    @Override
    public void setAttacking(boolean attacking) {
        this.isProcessingAttack = attacking;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickStealth(CallbackInfo ci) {
        if (this.getWorld().isClient) return;

        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack currentStack = player.getMainHandStack();

        if (!ItemStack.areItemsEqual(currentStack, lastMainHandStack)) {
            this.setStealth(0);
            lastMainHandStack = currentStack.copy();
            return;
        }

        if (currentStack.getItem() instanceof AssassinWeapon) {
            if (player.getAttributes().hasAttribute(ModAttributes.MAX_STEALTH)) {
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
            if (this.getStealth() > 0) {
                this.setStealth(0);
            }
        }
    }

    @ModifyVariable(method = "attack", at = @At(value = "STORE", ordinal = 0), name = "f")
    private float modifyDamageWithStealth(float damage) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack stack = player.getMainHandStack();

        if (stack.getItem() instanceof AssassinWeapon) {
            float stealth = this.getStealth();
            if (stealth > 0) {
                float multiplier = 1.0f + (stealth / 10.0f) * 0.25f;
                return damage * multiplier;
            }
        }
        return damage;
    }

    // === LOGIKA ATAKU ===

    @Inject(method = "attack", at = @At("HEAD"))
    private void startAttack(Entity target, CallbackInfo ci) {
        this.isProcessingAttack = true; // Zabezpieczamy stealth przed resetem
    }

    // NAPRAWIONY CRASH: Zmieniono LivingEntity na Entity w celu (target)
    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private void applyStealthEffect(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack stack = player.getMainHandStack();

        if (stack.getItem() instanceof AssassinWeapon assassinWeapon) {
            if (!player.getAttributes().hasAttribute(ModAttributes.MAX_STEALTH)) return;

            float stealth = this.getStealth();
            float maxStealth = (float) player.getAttributeValue(ModAttributes.MAX_STEALTH);

            if (stealth >= maxStealth - 0.9f) {
                if (target instanceof LivingEntity livingTarget && !player.getWorld().isClient) {
                    assassinWeapon.onStealthStrike(stack, player, livingTarget);
                }
                player.getWorld().sendEntityStatus(target, (byte) 4);
            }
        }
    }

    @Inject(method = "attack", at = @At("TAIL"))
    private void endAttack(Entity target, CallbackInfo ci) {
        // Atak zakończony sukcesem - resetujemy stealth
        if (this.getMainHandStack().getItem() instanceof AssassinWeapon) {
            this.setStealth(0);
        }
        this.isProcessingAttack = false;
    }

    // USUNIĘTO swingHand Z TEGO PLIKU ABY UNIKNĄĆ OSTRZEŻEŃ!
}