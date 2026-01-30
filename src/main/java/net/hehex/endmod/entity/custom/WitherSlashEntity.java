package net.hehex.endmod.entity.custom;


import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.FlyingItemEntity; // Ważne: FlyingItemEntity (nie ItemSupplier)
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory; // <--- NOWY IMPORT
import net.minecraft.sound.SoundEvents;   // <--- NOWY IMPORT
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

// Importy Twoich przedmiotów/encji
import net.hehex.endmod.item.EndItems;
import net.hehex.endmod.entity.ModEntities; // Zmień na EndEntities jeśli tak masz w kodzie

public class WitherSlashEntity extends PersistentProjectileEntity implements FlyingItemEntity {

    public WitherSlashEntity(EntityType<? extends WitherSlashEntity> type, World world) {
        super(type, world);
        this.setNoGravity(true);
        this.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
    }

    public WitherSlashEntity(World world, LivingEntity owner, ItemStack stack) {
        super(ModEntities.WITHER_SLASH_ENTITY, owner, world, stack, null);
        this.setNoGravity(true);
        this.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
    }

    @Override
    public ItemStack getStack() {
        return new ItemStack(EndItems.WITHER_SLASH);
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(EndItems.WITHER_SLASH);
    }

    @Override
    public void tick() {
        super.tick();

        // Cząsteczki dymu za pociskiem
        if (this.getWorld().isClient) {
            for (int i = 0; i < 2; i++) {
                this.getWorld().addParticle(
                        ParticleTypes.SMOKE,
                        this.getX() + (this.random.nextDouble() - 0.5) * 0.5,
                        this.getY() + (this.random.nextDouble() - 0.5) * 0.5,
                        this.getZ() + (this.random.nextDouble() - 0.5) * 0.5,
                        0, 0, 0
                );
            }
        }

        if (!this.getWorld().isClient && this.age > 40) {
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (entityHitResult.getEntity() instanceof LivingEntity target && this.getOwner() instanceof PlayerEntity player) {

            // 1. Obrażenia i Efekt
            target.damage(this.getDamageSources().playerAttack(player), 12.0f);
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 160, 1));

            // 2. DŹWIĘK (NOWOŚĆ)
            // playSound(null = słyszą wszyscy, x, y, z, Dźwięk, Kategoria, Głośność, Ton)
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ENTITY_WITHER_BREAK_BLOCK, // Charakterystyczny trzask Withera
                    SoundCategory.PLAYERS, 1.0f, 1.0f);

            // 3. LIFESTEAL (Zawsze)
            player.heal(5.0f);

            // 4. Efekt wizualny serduszek
            if (!this.getWorld().isClient) {
                ((ServerWorld) this.getWorld()).spawnParticles(ParticleTypes.HEART,
                        player.getX(), player.getEyeY() + 0.5, player.getZ(),
                        3, 0.2, 0.2, 0.2, 0.1);
            }

            // 5. Usuwanie pocisku
            if (!this.getWorld().isClient) {
                this.discard();
            }
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        if (!this.getWorld().isClient) {
            // Opcjonalnie: Tu też możesz dodać dźwięk uderzenia w ścianę
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ENTITY_WITHER_BREAK_BLOCK,
                    SoundCategory.PLAYERS, 0.5f, 1.0f);

            this.discard();
        }
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        return false;
    }
}