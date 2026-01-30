package net.hehex.endmod.item.custom;


import net.hehex.endmod.entity.custom.WitherSlashEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;


public class CorruptedScytheItem extends Item {

    public CorruptedScytheItem(Settings settings) {
        super(settings);
    }

    // --- 1. ATAK WRĘCZ (LIFESTEAL TYLKO PRZY KRYTYKU) ---
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player) {

            // Definicja uderzenia krytycznego w Minecraft:
            // - Gracz spada (fallDistance > 0)
            // - Nie stoi na ziemi
            // - Nie wspina się i nie jest w wodzie
            boolean isCritical = !player.isOnGround() && player.fallDistance > 0.0f
                    && !player.isClimbing() && !player.isTouchingWater();

            if (isCritical) {
                // Leczenie: 4.0f = 2 serca
                player.heal(4.0f);

                // Efekt wizualny (serduszka)
                if (!player.getWorld().isClient) {
                    ServerWorld serverWorld = (ServerWorld) player.getWorld();
                    serverWorld.spawnParticles(ParticleTypes.HEART,
                            player.getX(), player.getEyeY() + 0.5, player.getZ(),
                            3, 0.2, 0.2, 0.2, 0.1);
                }
            }

            // Zawsze nakładamy Wither II (niezależnie od krytyka)
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 100, 1));
        }

        // Utrata wytrzymałości (1 pkt) przy uderzeniu
        stack.damage(1, attacker, EquipmentSlot.MAINHAND);

        return super.postHit(stack, target, attacker);
    }

    // --- 2. ATAK DYSTANSOWY (PRAWY PRZYCISK MYSZY) ---
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        // Sprawdzamy Cooldown
        if (user.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.fail(itemStack);
        }

        if (!world.isClient) {
            WitherSlashEntity slash = new WitherSlashEntity(world, user, itemStack);
            slash.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1.5f, 1.0f);

            // UWAGA: Nie musimy tu już sprawdzać "isCritical",
            // bo w klasie WitherSlashEntity ustawiliśmy leczenie na "zawsze".

            world.spawnEntity(slash);

            // Ustawiamy Cooldown na 2 sekundy (40 ticków)
            user.getItemCooldownManager().set(this, 40);

            // Utrata wytrzymałości (5 pkt) za strzał
            // Sprawdzamy, w której ręce jest przedmiot, aby poprawnie go uszkodzić
            itemStack.damage(5, user, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }

        return TypedActionResult.success(itemStack, world.isClient());
    }
}