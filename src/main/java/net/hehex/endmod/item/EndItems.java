package net.hehex.endmod.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.hehex.endmod.EndMod;
import net.hehex.endmod.item.custom.*;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EndItems {
    //Ingredients
    public static final Item ENDERITE_INGOT = registerItem("enderite_ingot", new Item(new Item.Settings()));
    public static final Item RAW_ENDERITE = registerItem("raw_enderite", new Item(new Item.Settings()));
    public static final Item IRON_ROD = registerItem("iron_rod", new Item(new Item.Settings()));
    public static final Item DRAGON_CHARGE = registerItem("dragon_charge", new Item(new Item.Settings()));
    public static final Item DEMONIC_ESSENCE = registerItem("demonic_essence", new Item(new Item.Settings()));

//Pickaxes
    public static final Item ENDERITE_PICKAXE = registerItem("enderite_pickaxe",
        new PickaxeItem(ModToolMaterials.ENDERITE, new Item.Settings()
                .attributeModifiers(PickaxeItem.createAttributeModifiers(ModToolMaterials.ENDERITE, 5, -2.6f ))));


//Other Tools
public static final Item ENDERITE_AXE = registerItem("enderite_axe",
        new AxeItem(ModToolMaterials.ENDERITE, new Item.Settings()
                .attributeModifiers(AxeItem.createAttributeModifiers(ModToolMaterials.ENDERITE, 21, -3f ))));
    public static final Item ENDERITE_SHOVEL = registerItem("enderite_shovel",
            new ShovelItem(ModToolMaterials.ENDERITE, new Item.Settings()
                    .attributeModifiers(ShovelItem.createAttributeModifiers(ModToolMaterials.ENDERITE, 3, -2.2f ))));
    public static final Item ENDERITE_HOE = registerItem("enderite_hoe",
            new HoeItem(ModToolMaterials.ENDERITE, new Item.Settings()
                    .attributeModifiers(HoeItem.createAttributeModifiers(ModToolMaterials.ENDERITE, 1, -2.2f))));


//Utility
public static final Item STAFF_OF_TELEPORTATION = registerItem("staff_of_teleportation",
        new StaffOfTeleportationItem(new Item.Settings().maxDamage(64).maxCount(1)));
//Weapons Melee
    public static final Item DEMONIC_BLADE = registerItem("demonic_blade",
        new DemonicBladeItem(ToolMaterials.NETHERITE, new Item.Settings()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.ENDERITE, 11, -2.4f))));

    public static final Item ENDERITE_SWORD = registerItem("enderite_sword",
            new SwordItem(ModToolMaterials.ENDERITE, new Item.Settings()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.ENDERITE, 17, -2.4f))));
    //Weapons Ranger
    public static final Item WOODEN_SHORTBOW = registerItem("wooden_shortbow",
            new ModBowItem(
                    new Item.Settings().maxDamage(256),
                    14,
                    6.0f,
                    2.2f
            ));
    public static final Item BLAZE_BURNER = registerItem("blaze_burner", new Item(new Item.Settings()));

    //Weapons Reaper
    public static final Item CORRUPTED_SCYTHE = registerItem("corrupted_scythe",
            new CorruptedScytheItem(
                    ToolMaterials.NETHERITE,
                    10.0f,
                    -3.2f,
                    2.0f, // <-- TU USTAWIASZ LIFESTEAL (4.0 = 2 serca)
                    new Item.Settings().fireproof()
            ));
//Weapon Assasin
public static final Item WOODEN_DAGGER = registerItem("wooden_dagger",
        new WoodenDaggerItem(ToolMaterials.WOOD, // Zmień na materiał drewna lub stwórz nowy
                new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ToolMaterials.WOOD, 2, -1.4f))));



    public static final Item GILDED_GREATSWORD = registerItem("gilded_greatsword",
            new GildedGreatswordItem(
                    ToolMaterials.NETHERITE, new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(
                                    ToolMaterials.NETHERITE,
                                    8,
                                    -3.0f
                            ))));




public static final Item ENDERITE_HELMET = registerItem("enderite_helmet",
        new ArmorItem(EndArmorMaterials.ENDERITE_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Settings()
                .maxDamage(ArmorItem.Type.HELMET.getMaxDamage(15))));
    public static final Item ENDERITE_CHESTPLATE = registerItem("enderite_chestplate",
            new ArmorItem(EndArmorMaterials.ENDERITE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings()
                    .maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(15))));
    public static final Item ENDERITE_LEGGINGS = registerItem("enderite_leggings",
            new ArmorItem(EndArmorMaterials.ENDERITE_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings()
                    .maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(15))));
    public static final Item ENDERITE_BOOTS = registerItem("enderite_boots",
            new ArmorItem(EndArmorMaterials.ENDERITE_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Settings()
                    .maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(15))));
    public static final Item REAPER_HEADGEAR = registerItem("reaper_headgear",
            new ReaperArmorItem(EndArmorMaterials.REAPER_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Settings()
                    .maxDamage(ArmorItem.Type.HELMET.getMaxDamage(15))));
    public static final Item REAPER_CHESTPLATE = registerItem("reaper_chestplate",
            new ReaperArmorItem(EndArmorMaterials.REAPER_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings()
                    .maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(15))));
    public static final Item REAPER_LEGGINGS = registerItem("reaper_leggings",
            new ReaperArmorItem(EndArmorMaterials.REAPER_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings()
                    .maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(15))));
    public static final Item REAPER_BOOTS = registerItem("reaper_boots",
            new ReaperArmorItem(EndArmorMaterials.REAPER_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Settings()
                    .maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(15))));



    //Extras
    public static final Item WITHER_SLASH = Registry.register(
            Registries.ITEM,
            Identifier.of("endmod", "wither_slash"),
            new Item(new Item.Settings()));




    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(EndMod.MOD_ID, name), item);
    }

    public static void registerEndItems() {
        EndMod.LOGGER.info("Registering End Items for " + EndMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(ENDERITE_INGOT);
            entries.add(RAW_ENDERITE);
            entries.add(IRON_ROD);
            entries.add(DRAGON_CHARGE);
            entries.add(DEMONIC_ESSENCE);

        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(fabricItemGroupEntries -> {
           fabricItemGroupEntries.add(STAFF_OF_TELEPORTATION);
            fabricItemGroupEntries.add(ENDERITE_AXE);
            fabricItemGroupEntries.add(ENDERITE_HOE);
            fabricItemGroupEntries.add(ENDERITE_PICKAXE);
            fabricItemGroupEntries.add(ENDERITE_SHOVEL);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(EndItems.ENDERITE_HELMET);
            fabricItemGroupEntries.add(EndItems.ENDERITE_CHESTPLATE);
            fabricItemGroupEntries.add(EndItems.ENDERITE_LEGGINGS);
            fabricItemGroupEntries.add(EndItems.ENDERITE_BOOTS);
            fabricItemGroupEntries.add(EndItems.REAPER_HEADGEAR);
            fabricItemGroupEntries.add(EndItems.REAPER_CHESTPLATE);
            fabricItemGroupEntries.add(EndItems.REAPER_LEGGINGS);
            fabricItemGroupEntries.add(EndItems.REAPER_BOOTS);
            fabricItemGroupEntries.add(EndItems.DEMONIC_BLADE);
            fabricItemGroupEntries.add(EndItems.ENDERITE_SWORD);
            fabricItemGroupEntries.add(EndItems.CORRUPTED_SCYTHE);
            fabricItemGroupEntries.add(EndItems.WOODEN_SHORTBOW);
            fabricItemGroupEntries.add(EndItems.BLAZE_BURNER);
            fabricItemGroupEntries.add(EndItems.GILDED_GREATSWORD);
            fabricItemGroupEntries.add(EndItems.WOODEN_DAGGER);



        });
    }
}
