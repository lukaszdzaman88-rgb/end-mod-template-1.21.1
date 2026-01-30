package net.hehex.endmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.hehex.endmod.entity.ModEntities;
import net.hehex.endmod.particle.GoldSparkleParticle;
import net.hehex.endmod.particle.ModParticles;
import net.hehex.endmod.util.ModTooltips;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class EndModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {


        EntityRendererRegistry.register(ModEntities.WITHER_SLASH_ENTITY, (context) -> {
            return new FlyingItemEntityRenderer<>(context, 1.5f, false);
        });

        // 4. Inne rejestracje (Też poza Tick Eventem)
        ModTooltips.init();
        ParticleFactoryRegistry.getInstance().register(ModParticles.GOLD_SPARKLE, GoldSparkleParticle.Factory::new);

    }
}