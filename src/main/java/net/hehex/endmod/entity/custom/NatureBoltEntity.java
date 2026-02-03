package net.hehex.endmod.entity.custom;

import net.hehex.endmod.attribute.ModAttributes;
import net.hehex.endmod.entity.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class NatureBoltEntity extends PersistentProjectileEntity {

    public NatureBoltEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public NatureBoltEntity(World world, LivingEntity owner) {
        super(ModEntities.NATURE_BOLT, owner, world, new ItemStack(net.minecraft.item.Items.WHEAT_SEEDS), null); // item stack to placeholder
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (entityHitResult.getEntity() instanceof LivingEntity target) {
            float damage = 7.0f;

            // Jeśli właściciel to gracz, doliczamy jego Magic Damage
            if (this.getOwner() instanceof PlayerEntity player) {
                damage += (float) player.getAttributeValue(ModAttributes.MAGIC_DAMAGE);
            }

            // Atakujemy typem obrażeń MAGIC
            target.damage(this.getDamageSources().magic(), damage);

            // Opcjonalnie: efekt cząsteczkowy przy trafieniu
            this.getWorld().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY(), this.getZ(), 0, 0.1, 0);
        }
        this.discard(); // Zniszcz pocisk po trafieniu
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) {
            this.getWorld().addParticle(ParticleTypes.COMPOSTER, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return ItemStack.EMPTY; // Nie upuszcza itemu
    }
}