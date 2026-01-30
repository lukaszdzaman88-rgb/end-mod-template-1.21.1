package net.hehex.endmod.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

public class ModBowItem extends BowItem {

    private final int drawSpeed;
    private final float rangedDamage;
    private final float maxVelocity; // ZMIANA: Nowe pole prędkości

    // ZMIANA: Dodajemy maxVelocity do konstruktora
    public ModBowItem(Settings settings, int drawSpeed, float rangedDamage, float maxVelocity) {
        super(settings);
        this.drawSpeed = drawSpeed;
        this.rangedDamage = rangedDamage;
        this.maxVelocity = maxVelocity;
    }

    public int getDrawSpeed() {
        return this.drawSpeed;
    }

    public float getRangedDamage() {
        return this.rangedDamage;
    }

    // Getter dla tooltipów (jeśli chciałbyś wyświetlić zasięg)
    public float getMaxVelocity() {
        return this.maxVelocity;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity playerEntity)) {
            return;
        }

        ItemStack itemStack = playerEntity.getProjectileType(stack);
        if (itemStack.isEmpty() && !playerEntity.getAbilities().creativeMode) {
            return;
        }

        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
        float f = getCustomPullProgress(i);

        if (!((double)f < 0.1)) {
            ItemStack itemStack2 = itemStack.isEmpty() ? new ItemStack(Items.ARROW) : itemStack;

            PersistentProjectileEntity persistentProjectileEntity = this.createArrow(world, itemStack2, playerEntity, stack);

            // --- ZMIANA KLUCZOWA ---
            // Zamiast mnożyć razy 3.0F, mnożymy razy naszą zmienną maxVelocity
            persistentProjectileEntity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, f * this.maxVelocity, 1.0F);

            // Wyłączony krytyk (aby shortbow nie zabijał na farta)
            /*
            if (f == 1.0F) {
                persistentProjectileEntity.setCritical(true);
            }
            */

            stack.damage(1, playerEntity, LivingEntity.getSlotForHand(playerEntity.getActiveHand()));
            world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);

            if (itemStack2.getItem() instanceof ArrowItem arrowItem) {
                // fix dla createArrow
            }

            if (!playerEntity.getAbilities().creativeMode) {
                itemStack2.decrement(1);
                if (itemStack2.isEmpty()) {
                    playerEntity.getInventory().removeOne(itemStack);
                }
            }

            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            world.spawnEntity(persistentProjectileEntity);
        }
    }

    public float getCustomPullProgress(int useTicks) {
        float f = (float)useTicks / (float)this.drawSpeed;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    public PersistentProjectileEntity createArrow(World world, ItemStack stack, LivingEntity shooter, ItemStack bow) {
        ArrowItem arrowItem = (ArrowItem)(stack.getItem() instanceof ArrowItem ? stack.getItem() : Items.ARROW);
        return arrowItem.createArrow(world, stack, shooter, bow);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }
}