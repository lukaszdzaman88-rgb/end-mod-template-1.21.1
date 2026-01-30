package net.hehex.endmod.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

public class StaffPearlEntity extends ThrownItemEntity {
    public StaffPearlEntity(EntityType<? extends EnderPearlEntity> entityType, World world) {
        super(entityType, world);
    }

    public StaffPearlEntity(World world, LivingEntity owner) {
        super(EntityType.ENDER_PEARL, owner, world);
    }

    protected Item getDefaultItem() {
        return Items.ENDER_PEARL;
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        entityHitResult.getEntity().damage(this.getDamageSources().thrown(this, this.getOwner()), 0.0F);
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        for(int i = 0; i < 32; ++i) {
            this.getWorld().addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0, this.getZ(), this.random.nextGaussian(), 0.0, this.random.nextGaussian());
        }

        World var3 = this.getWorld();
        if (var3 instanceof ServerWorld serverWorld) {
            if (!this.isRemoved()) {
                Entity entity = this.getOwner();
                if (entity != null && canTeleportEntityTo(entity, serverWorld)) {
                    if (entity.hasVehicle()) {
                        entity.detach();
                    }

                    if (entity instanceof ServerPlayerEntity) {
                        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
                        if (serverPlayerEntity.networkHandler.isConnectionOpen()) {
                            if (this.random.nextFloat() < 0.0F && serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
                                EndermiteEntity endermiteEntity = (EndermiteEntity)EntityType.ENDERMITE.create(serverWorld);
                                if (endermiteEntity != null) {
                                    endermiteEntity.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
                                    serverWorld.spawnEntity(endermiteEntity);
                                }
                            }

                            entity.teleportTo(new TeleportTarget(serverWorld, this.getPos(), entity.getVelocity(), entity.getYaw(), entity.getPitch(), TeleportTarget.NO_OP));
                            entity.onLanding();
                            serverPlayerEntity.clearCurrentExplosion();
                            entity.damage(this.getDamageSources().fall(), 0.0F);
                            this.playTeleportSound(serverWorld, this.getPos());
                        }
                    } else {
                        entity.teleportTo(new TeleportTarget(serverWorld, this.getPos(), entity.getVelocity(), entity.getYaw(), entity.getPitch(), TeleportTarget.NO_OP));
                        entity.onLanding();
                        this.playTeleportSound(serverWorld, this.getPos());
                    }

                    this.discard();
                    return;
                }

                this.discard();
                return;
            }
        }

    }

    private static boolean canTeleportEntityTo(Entity entity, World world) {
        if (entity.getWorld().getRegistryKey() == world.getRegistryKey()) {
            if (!(entity instanceof LivingEntity)) {
                return entity.isAlive();
            } else {
                LivingEntity livingEntity = (LivingEntity)entity;
                return livingEntity.isAlive() && !livingEntity.isSleeping();
            }
        } else {
            return entity.canUsePortals(true);
        }
    }

    public void tick() {
        Entity entity = this.getOwner();
        if (entity instanceof ServerPlayerEntity && !entity.isAlive() && this.getWorld().getGameRules().getBoolean(GameRules.ENDER_PEARLS_VANISH_ON_DEATH)) {
            this.discard();
        } else {
            super.tick();
        }

    }

    private void playTeleportSound(World world, Vec3d pos) {
        world.playSound((PlayerEntity)null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_PLAYER_TELEPORT, SoundCategory.PLAYERS);
    }

    public boolean canTeleportBetween(World from, World to) {
        if (from.getRegistryKey() == World.END) {
            Entity var4 = this.getOwner();
            if (var4 instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var4;
                return super.canTeleportBetween(from, to) && serverPlayerEntity.seenCredits;
            }
        }

        return super.canTeleportBetween(from, to);
    }

    protected void onBlockCollision(BlockState state) {
        super.onBlockCollision(state);
        if (state.isOf(Blocks.END_GATEWAY)) {
            Entity var3 = this.getOwner();
            if (var3 instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var3;
                serverPlayerEntity.onBlockCollision(state);
            }
        }

    }
}

