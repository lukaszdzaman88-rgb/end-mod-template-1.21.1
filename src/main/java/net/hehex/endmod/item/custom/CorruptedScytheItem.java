package net.hehex.endmod.item.custom;



import net.hehex.endmod.entity.custom.WitherSlashEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class CorruptedScytheItem extends ModScytheItem {

    // Konstruktor przyjmuje lifesteal
    public CorruptedScytheItem(ToolMaterial material, float attackDamage, float attackSpeed, float lifestealAmount, Settings settings) {
        super(material, attackDamage, attackSpeed, lifestealAmount, settings);
    }

    // --- NAPRAWA DAMAGE (FIX) ---
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // 1. NAJWAŻNIEJSZE: Wywołujemy super, żeby ModScytheItem zadał "Reaper Damage"
        // Bez tego linijki kosa bije za 1 dmg (jak ręka)!
        super.postHit(stack, target, attacker);

        // 2. Dodajemy efekt Wither
        if (!attacker.getWorld().isClient) {
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 100, 1));
        }

        return true;
    }

    // --- NAPRAWA TOOLTIPU (FIX) ---
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        // Wywołujemy super, żeby pokazały się statystyki z ModScytheItem
        super.appendTooltip(stack, context, tooltip, type);
    }

    // --- STRZELANIE (Bez zmian) ---
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (user.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.fail(itemStack);
        }

        if (!world.isClient) {
            WitherSlashEntity slash = new WitherSlashEntity(world, user, itemStack);
            slash.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1.5f, 1.0f);

            if (!user.isOnGround()) {
                slash.setCritical(true);
            }

            world.spawnEntity(slash);
            user.getItemCooldownManager().set(this, 40);
            itemStack.damage(5, user, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }

        return TypedActionResult.success(itemStack, world.isClient());
    }
}