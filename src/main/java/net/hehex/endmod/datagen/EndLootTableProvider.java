package net.hehex.endmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.hehex.endmod.block.EndBlocks;
import net.hehex.endmod.item.EndItems;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class EndLootTableProvider extends FabricBlockLootTableProvider {
    public EndLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(EndBlocks.ENDERITE_BLOCK);
        addDrop(EndBlocks.ENDERIC_ORE, oreDrops(EndBlocks.ENDERIC_ORE, EndItems.RAW_ENDERITE));
    }
}
