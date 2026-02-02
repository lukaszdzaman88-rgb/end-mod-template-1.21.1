package net.hehex.endmod.mixin;

import net.hehex.endmod.util.AssassinPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements AssassinPlayer {

    @Unique
    private static final TrackedData<Float> CURRENT_STEALTH = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    protected void initStealthData(DataTracker.Builder builder, CallbackInfo ci) {
        // Sprawdzamy, czy to Gracz. Jeśli tak, dodajemy dane.
        if ((Object) this instanceof PlayerEntity) {
            builder.add(CURRENT_STEALTH, 0.0f);
        }
    }

    // Implementacja interfejsu tutaj, aby była dostępna dla Gracza
    @Override
    public float getStealth() {
        if ((Object) this instanceof PlayerEntity) {
            return this.dataTracker.get(CURRENT_STEALTH);
        }
        return 0;
    }

    @Override
    public void setStealth(float stealth) {
        if ((Object) this instanceof PlayerEntity) {
            this.dataTracker.set(CURRENT_STEALTH, stealth);
        }
    }
}
