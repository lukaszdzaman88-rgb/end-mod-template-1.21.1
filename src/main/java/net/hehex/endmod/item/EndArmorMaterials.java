package net.hehex.endmod.item;

import net.hehex.endmod.EndMod;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class EndArmorMaterials {
    public static final RegistryEntry<ArmorMaterial> ENDERITE_ARMOR_MATERIAL = registerArmorMaterial("enderite",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 6);
                map.put(ArmorItem.Type.LEGGINGS, 4);
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.BODY, 4);
            }), 20, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, () -> Ingredient.ofItems(EndItems.ENDERITE_INGOT),
                    List.of(new ArmorMaterial.Layer(Identifier.of(EndMod.MOD_ID, "enderite"))), 0,0
            ));
    public static final RegistryEntry<ArmorMaterial> DEMONIC_ARMOR_MATERIAL = registerArmorMaterial("demonic",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS,6);
                map.put(ArmorItem.Type.CHESTPLATE, 6);
                map.put(ArmorItem.Type.LEGGINGS, 6);
                map.put(ArmorItem.Type.HELMET, 6);
                map.put(ArmorItem.Type.BODY, 6);
            }), 20, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, () -> Ingredient.ofItems(EndItems.DEMONIC_ESSENCE),
                    List.of(new ArmorMaterial.Layer(Identifier.of(EndMod.MOD_ID, "demonic"))), 0,0
            ));
    public static final RegistryEntry<ArmorMaterial> REAPER_ARMOR_MATERIAL = registerArmorMaterial("reaper",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS,5);
                map.put(ArmorItem.Type.CHESTPLATE, 9);
                map.put(ArmorItem.Type.LEGGINGS, 7);
                map.put(ArmorItem.Type.HELMET, 5);
                map.put(ArmorItem.Type.BODY, 7);
            }), 20, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, () -> Ingredient.ofItems(Items.NETHERITE_INGOT),
                    List.of(new ArmorMaterial.Layer(Identifier.of(EndMod.MOD_ID, "reaper"))), 0,0
            ));


    public static RegistryEntry<ArmorMaterial> registerArmorMaterial(String name, Supplier<ArmorMaterial> material) {
        return Registry.registerReference(Registries.ARMOR_MATERIAL, Identifier.of(EndMod.MOD_ID, name), material.get());
    }
}
