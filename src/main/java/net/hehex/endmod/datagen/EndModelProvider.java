package net.hehex.endmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.hehex.endmod.block.EndBlocks;
import net.hehex.endmod.item.EndItems;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.ArmorItem;

public class EndModelProvider extends FabricModelProvider {
    public EndModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(EndBlocks.ENDERITE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(EndBlocks.ENDERIC_ORE);

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(EndItems.ENDERITE_INGOT, Models.GENERATED);
        itemModelGenerator.register(EndItems.RAW_ENDERITE, Models.GENERATED);
        itemModelGenerator.register(EndItems.DRAGON_CHARGE, Models.GENERATED);
        itemModelGenerator.register(EndItems.STAFF_OF_TELEPORTATION, Models.HANDHELD);
        itemModelGenerator.register(EndItems.IRON_ROD, Models.HANDHELD);
        itemModelGenerator.registerArmor(((ArmorItem) EndItems.ENDERITE_HELMET));
        itemModelGenerator.registerArmor(((ArmorItem) EndItems.ENDERITE_CHESTPLATE));
        itemModelGenerator.registerArmor(((ArmorItem) EndItems.ENDERITE_LEGGINGS));
        itemModelGenerator.registerArmor(((ArmorItem) EndItems.ENDERITE_BOOTS));
        itemModelGenerator.registerArmor(((ArmorItem) EndItems.REAPER_HEADGEAR));
        itemModelGenerator.registerArmor(((ArmorItem) EndItems.REAPER_CHESTPLATE));
        itemModelGenerator.registerArmor(((ArmorItem) EndItems.REAPER_LEGGINGS));
        itemModelGenerator.registerArmor(((ArmorItem) EndItems.REAPER_BOOTS));
        itemModelGenerator.register(EndItems.DEMONIC_BLADE, Models.HANDHELD);
        itemModelGenerator.register(EndItems.DEMONIC_ESSENCE, Models.GENERATED);
        itemModelGenerator.register(EndItems.ENDERITE_AXE, Models.HANDHELD);
        itemModelGenerator.register(EndItems.ENDERITE_HOE, Models.HANDHELD);
        itemModelGenerator.register(EndItems.ENDERITE_SHOVEL, Models.HANDHELD);
        itemModelGenerator.register(EndItems.ENDERITE_SWORD, Models.HANDHELD);
        itemModelGenerator.register(EndItems.ENDERITE_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(EndItems.GILDED_GREATSWORD, Models.HANDHELD);
        itemModelGenerator.register(EndItems.WOODEN_DAGGER, Models.HANDHELD);





    }
}
