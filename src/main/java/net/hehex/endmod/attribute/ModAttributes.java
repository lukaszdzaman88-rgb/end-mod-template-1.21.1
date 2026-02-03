package net.hehex.endmod.attribute;

import net.minecraft.entity.EntityType;


import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModAttributes {

    // Tworzymy atrybut
    public static final RegistryEntry<EntityAttribute> GENERIC_RANGED_DAMAGE = register(
            "ranged_damage",
            new ClampedEntityAttribute("attribute.name.generic.ranged_damage", 0.0, 0.0, 1024.0).setTracked(true)
    );
    public static final RegistryEntry<EntityAttribute> MAX_STEALTH = register("max_stealth",
            new ClampedEntityAttribute("attribute.name.generic.endmod.max_stealth", 100.0, 0.0, 1024.0));

    public static final RegistryEntry<EntityAttribute> STEALTH_REGEN = register("stealth_regen",
            new ClampedEntityAttribute("attribute.name.generic.endmod.stealth_regen", 1.0, 0.0, 1024.0));

    public static final RegistryEntry<EntityAttribute> MAX_MANA = register("max_mana",
            new ClampedEntityAttribute("attribute.endmod.max_mana", 100.0, 0.0, 10000.0).setTracked(true));

    // Bazowy magic damage: 0
    public static final RegistryEntry<EntityAttribute> MAGIC_DAMAGE = register("magic_damage",
            new ClampedEntityAttribute("attribute.endmod.magic_damage", 0.0, 0.0, 1024.0).setTracked(true));


    private static RegistryEntry<EntityAttribute> register(String id, EntityAttribute attribute) {
        // Pamiętaj, aby podmienić "endmod" na Twoje prawdziwe MOD_ID jeśli jest inne
        return Registry.registerReference(Registries.ATTRIBUTE, Identifier.of("endmod", id), attribute);
    }

    public static void registerAttributes() {

    }
}
