package net.hehex.endmod.event;


import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.hehex.endmod.item.custom.ModScytheItem;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class ModEvents {

    public static void registerModEvents() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            try {
                if (world.isClient || hand != Hand.MAIN_HAND) return ActionResult.PASS;

                ItemStack stack = player.getMainHandStack();

                // SPRAWDZAMY CZY TO JAKAKOLWIEK KOSA (ModScytheItem lub jej dziecko np. Corrupted)
                if (!stack.isEmpty() && stack.getItem() instanceof ModScytheItem scythe) {

                    // Sprawdzamy czy kosa ma w ogóle lifesteal (żeby nie leczyć przy 0)
                    if (scythe.getLifestealAmount() <= 0) return ActionResult.PASS;

                    float cooldown = player.getAttackCooldownProgress(0.5f);
                    boolean isCooldownFull = cooldown > 0.9f;
                    boolean isFalling = player.fallDistance > 0.0F;
                    boolean notOnGround = !player.isOnGround();
                    boolean safeConditions = !player.isClimbing() && !player.isTouchingWater() &&
                            !player.hasVehicle() && !player.hasStatusEffect(StatusEffects.BLINDNESS);

                    if (isCooldownFull && isFalling && notOnGround && safeConditions) {

                        // UŻYWAMY ZMIENNEJ Z PRZEDMIOTU
                        player.heal(scythe.getLifestealAmount());

                        if (world instanceof ServerWorld serverWorld) {
                            serverWorld.spawnParticles(ParticleTypes.HEART,
                                    player.getX(), player.getEyeY() + 0.5, player.getZ(),
                                    5, 0.5, 0.5, 0.5, 0.1);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return ActionResult.PASS;
        });
    }
}
