package net.hehex.endmod.item.custom;

import net.hehex.endmod.particle.ModParticles;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

public class GildedGreatswordItem extends SwordItem {

    public GildedGreatswordItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    // Ta metoda wywołuje się za każdym razem, gdy uderzysz moba/gracza
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Wywołujemy super, aby miecz tracił wytrzymałość normalnie
        super.postHit(stack, target, attacker);

        World world = target.getWorld();

        // Logika efektów musi dziać się po stronie serwera (!isClient)
        if (!world.isClient) {
            ServerWorld serverWorld = (ServerWorld) world;

            // 1. Dźwięk "Mini Eksplozji"
            // Używamy dźwięku wybuchu, ale z wyższym "pitch" (2.0f), żeby brzmiał jak mały wybuch/petarda
            world.playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.5f, 2.0f);

            // Opcjonalnie: Dźwięk dzwonienia złota dla klimatu
            world.playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.PLAYERS, 1.0f, 1.0f);


            serverWorld.spawnParticles(
                    ModParticles.GOLD_SPARKLE, // Twoja nowa cząsteczka
                    target.getX(), target.getBodyY(0.5), target.getZ(),
                    100, 0.5, 0.5, 0.5, 0.5
            );

            List<LivingEntity> nearbyEntities = world.getEntitiesByClass(LivingEntity.class, target.getBoundingBox().expand(1.0), e -> e != attacker && e != target);
            for (LivingEntity nearby : nearbyEntities) {
                nearby.damage(attacker.getDamageSources().magic(), 3.0f); // 2.0 = 1 serce obrażeń magicznych
            }

        }

        return true;
    }
}