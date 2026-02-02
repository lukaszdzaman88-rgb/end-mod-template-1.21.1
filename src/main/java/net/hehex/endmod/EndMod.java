package net.hehex.endmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntityAttributeModificationEvents;
import net.minecraft.entity.EntityType;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.hehex.endmod.attribute.ModAttributes;
import net.hehex.endmod.block.EndBlocks;
import net.hehex.endmod.entity.ModEntities;
import net.hehex.endmod.event.ModEvents;
import net.hehex.endmod.item.EndItems;
import net.hehex.endmod.particle.ModParticles;
import net.hehex.endmod.util.ModModelPredicates;
import net.hehex.endmod.util.ModTooltips;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndMod implements ModInitializer {
	public static final String MOD_ID = "endmod";


	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	@Override
	public void onInitialize() {
		EndItems.registerEndItems();
		EndBlocks.registerModBlocks();
		ModEntities.registerModEntities();
		ModModelPredicates.registerModelPredicates();
		ModParticles.registerParticles();
		ModEvents.registerModEvents();
		ModAttributes.registerAttributes();

		EntityAttributeModificationEvents.MODIFY_ENTITY_ATTRIBUTES.register((type, builder) -> {
			if (type == EntityType.PLAYER) {
				builder.add(ModAttributes.MAX_STEALTH);
				builder.add(ModAttributes.STEALTH_REGEN);
			}
		});






	}
}