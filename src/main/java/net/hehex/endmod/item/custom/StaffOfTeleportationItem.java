package net.hehex.endmod.item.custom;

import net.hehex.endmod.entity.custom.StaffPearlEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class StaffOfTeleportationItem extends Item {
    public StaffOfTeleportationItem(Settings settings) {
        super(settings);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDER_PEARL_THROW, SoundCategory.NEUTRAL, 0.3f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
        user.getItemCooldownManager().set(this, 200);
        if (!world.isClient) {
            StaffPearlEntity staff_of_teleportation = new StaffPearlEntity(world, user);
            staff_of_teleportation.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 2.5f, 0f);
            world.spawnEntity(staff_of_teleportation);
        }

        return TypedActionResult.success(itemStack, world.isClient());

    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.endmod.staff_of_teleportation"));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
