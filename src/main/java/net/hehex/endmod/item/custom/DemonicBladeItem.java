package net.hehex.endmod.item.custom;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class DemonicBladeItem extends SwordItem {

    private static final Identifier DAMAGE_MODIFIER_ID = Identifier.of("endmod", "demonic_zone_damage_boost");
    private static final Identifier SPEED_MODIFIER_ID = Identifier.of("endmod", "demonic_zone_speed_boost");

    private BlockPos zoneCenter = null;
    private int remainingTicks = 0;

    public DemonicBladeItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            if (user.getItemCooldownManager().isCoolingDown(this)) {
                return TypedActionResult.fail(stack);
            }
            this.zoneCenter = user.getBlockPos();
            this.remainingTicks = 300;
            user.getItemCooldownManager().set(this, 600);
            user.sendMessage(Text.literal("The sigil has been spawned"), true);

        }
        return TypedActionResult.success(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient || !(entity instanceof PlayerEntity player)) return;

        // Kluczowa zmiana: Sprawdzamy, czy gracz trzyma TEN KONKRETNY miecz w ręce
        boolean isHoldingThisSword = player.getMainHandStack() == stack;

        if (remainingTicks > 0 && zoneCenter != null) {
            remainingTicks--;

            // Logika sprawdzania obszaru
            double dx = Math.abs(player.getX() - (zoneCenter.getX() + 0.5));
            double dz = Math.abs(player.getZ() - (zoneCenter.getZ() + 0.5));
            boolean isInZone = dx <= 1.5 && dz <= 1.5;

            // BONUS DZIAŁA TYLKO JEŚLI: gracz jest w strefie ORAZ trzyma ten miecz
            if (isInZone && isHoldingThisSword) {
                applyBuffs(player);
                spawnParticles((ServerWorld) world, zoneCenter);
            } else {
                // Jeśli wyjdzie ze strefy LUB zmieni broń w ręce - zabieramy bonusy
                removeBuffs(player);
            }

            if (remainingTicks <= 0) {
                removeBuffs(player);
                zoneCenter = null;
                player.sendMessage(Text.literal("The sigil has disappeared"), true);
            }
        } else {
            removeBuffs(player);
        }
    }

    private void applyBuffs(PlayerEntity player) {
        var damageAttr = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        var speedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED);

        if (damageAttr != null && !damageAttr.hasModifier(DAMAGE_MODIFIER_ID)) {
            damageAttr.addTemporaryModifier(new EntityAttributeModifier(DAMAGE_MODIFIER_ID, 0.2, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
        if (speedAttr != null && !speedAttr.hasModifier(SPEED_MODIFIER_ID)) {
            speedAttr.addTemporaryModifier(new EntityAttributeModifier(SPEED_MODIFIER_ID, 0.2, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }

    private void removeBuffs(PlayerEntity player) {
        var damageAttr = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        var speedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED);

        if (damageAttr != null && damageAttr.hasModifier(DAMAGE_MODIFIER_ID)) {
            damageAttr.removeModifier(DAMAGE_MODIFIER_ID);
        }
        if (speedAttr != null && speedAttr.hasModifier(SPEED_MODIFIER_ID)) {
            speedAttr.removeModifier(SPEED_MODIFIER_ID);
        }
    }

    private void spawnParticles(ServerWorld world, BlockPos pos) {
        // Cząsteczki w kształcie kwadratu 3x3
        for (double i = -1.5; i <= 1.5; i += 1.5) {
            for (double j = -1.5; j <= 1.5; j += 1.5) {
                world.spawnParticles(ParticleTypes.CRIT, pos.getX() + 0.5 + i, pos.getY() + 0.1, pos.getZ() + 0.5 + j, 1, 0, 0, 0, 0);
            }
        }
    }
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.endmod.demonic_blade_shift_down"));
        } else {
        tooltip.add(Text.translatable("tooltip.endmod.demonic_blade"));
        super.appendTooltip(stack, context, tooltip, type);
    }
}}