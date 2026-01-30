package net.hehex.endmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.hehex.endmod.block.EndBlocks;
import net.hehex.endmod.util.EndTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EndBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public EndBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(EndBlocks.ENDERITE_BLOCK)
                .add(EndBlocks.ENDERIC_ORE);

        registerMiningLevel(
                List.of(EndBlocks.ENDERIC_ORE),
                List.of("wooden", "stone", "iron", "diamond")
        );


        registerMiningLevel(
                List.of(EndBlocks.ENDERITE_BLOCK), List.of("wooden", "stone", "iron", "diamond", "netherite"));
    }


    private void registerMiningLevel(List<Block> blocks, List<String> incorrectTiers) {
        for (Block block : blocks) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(block);

        for (String tierName : incorrectTiers) {
            getOrCreateTagBuilder(TagKey.of(RegistryKeys.BLOCK,
                    Identifier.of("minecraft", "incorrect_for_" + tierName + "_tool")))
                    .add(block);
        }
    }
        }}

