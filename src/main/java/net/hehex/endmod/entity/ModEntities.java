package net.hehex.endmod.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.hehex.endmod.EndMod;
import net.hehex.endmod.entity.custom.StaffPearlEntity;
import net.hehex.endmod.entity.custom.WitherSlashEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<WitherSlashEntity> WITHER_SLASH_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("modid", "wither_slash"), // ZMIANA: Identifier.of zamiast new
            EntityType.Builder.<WitherSlashEntity>create(WitherSlashEntity::new, SpawnGroup.MISC)
                    .dimensions(2.0f, 0.5f) // ZMIANA: Bezpośrednie wartości zamiast EntityDimensions.fixed
                    .build()
    );



    public static void registerModEntities() {
        EndMod.LOGGER.info("Registering entities for" + EndMod.MOD_ID);
    }
}
