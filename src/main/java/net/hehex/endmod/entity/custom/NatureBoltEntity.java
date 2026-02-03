package net.hehex.endmod.entity.custom;

import net.hehex.endmod.attribute.ModAttributes;
import net.hehex.endmod.entity.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes; // Użyjemy domyślnych lub Twoich customowych
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class NatureBoltEntity extends PersistentProjectileEntity {

    public NatureBoltEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public NatureBoltEntity(World world, LivingEntity owner) {
        super(ModEntities.NATURE_BOLT, owner, world, ItemStack.EMPTY, null);
    }

    @Override
    public void tick() {
        super.tick();

        // --- TO SPRAWIA, ŻE POCISK JEST Z PARTICLI ---
        if (this.getWorld().isClient) {
            // Generuje 3 particle w obecnej pozycji entity
            for(int i = 0; i < 3; i++) {
                this.getWorld().addParticle(
                        ParticleTypes.COMPOSTER, // Zielony particle (możesz użyć HAPPY_VILLAGER lub własnego)
                        this.getX() + (this.random.nextDouble() - 0.5) * 0.2,
                        this.getY() + (this.random.nextDouble() - 0.5) * 0.2,
                        this.getZ() + (this.random.nextDouble() - 0.5) * 0.2,
                        0, 0, 0 // Prędkość particla
                );
            }
        }

        // Opcjonalnie: zniknij po określonym czasie (np. 5 sekund), żeby nie latał w nieskończoność
        if (this.age > 100) {
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        // Logika obrażeń taka sama jak wcześniej
        if (entityHitResult.getEntity() instanceof LivingEntity target) {
            float damage = 7.0f;
            if (this.getOwner() instanceof PlayerEntity player) {
                damage += (float) player.getAttributeValue(ModAttributes.MAGIC_DAMAGE);
            }
            target.damage(this.getDamageSources().magic(), damage);

            // Efekt przy trafieniu
            if (this.getWorld().isClient) {
                for(int i=0; i<10; i++)
                    this.getWorld().addParticle(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 0,0,0);
            }
        }
        this.discard(); // Usuń entity po trafieniu
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.discard(); // Usuń entity po uderzeniu w blok
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return ItemStack.EMPTY;
    }

    // Ważne: Żeby pocisk był niewidzialny (tylko particle), musisz zarejestrować dla niego
    // pusty Renderer albo po prostu nie przypisywać modelu, ale najlepiej użyć EmptyEntityRenderer.
}