package net.hehex.endmod.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.hehex.endmod.EndMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class EndBlocks {

    public static final Block ENDERITE_BLOCK = registerBlock("enderite_block",
            new Block(AbstractBlock.Settings.create().strength(4.5f)
                    .requiresTool().sounds(BlockSoundGroup.NETHERITE)));
    public static final Block ENDERIC_ORE = registerBlock("enderic_ore",
            new Block(AbstractBlock.Settings.create().strength(3f)
                    .requiresTool().sounds(BlockSoundGroup.STONE)));




    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(EndMod.MOD_ID, name), block);
    }
    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(EndMod.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }
    public static void registerModBlocks() {
        EndMod.LOGGER.info("Registering Mod Blocks for " + EndMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.add(EndBlocks.ENDERITE_BLOCK);


        });
    }
}

