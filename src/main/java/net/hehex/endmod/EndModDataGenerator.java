package net.hehex.endmod;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.hehex.endmod.datagen.*;

public class EndModDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(EndBlockTagProvider::new);
		pack.addProvider(EndItemTagProvider::new);
		pack.addProvider(EndLootTableProvider::new);
		pack.addProvider(EndModelProvider::new);
		pack.addProvider(EndRecipeProvider::new);


	}
}
