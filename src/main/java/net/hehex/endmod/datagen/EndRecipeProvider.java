package net.hehex.endmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.hehex.endmod.item.EndItems;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import org.apache.commons.compress.archivers.zip.Zip64ExtendedInformationExtraField;

import java.util.concurrent.CompletableFuture;

public class EndRecipeProvider extends FabricRecipeProvider {
    public EndRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter recipeExporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, EndItems.STAFF_OF_TELEPORTATION)
                .pattern(" EP")
                .pattern("PIE")
                .pattern("EP ")
                .input('E' , EndItems.ENDERITE_INGOT)
                .input('P' , Items.ENDER_PEARL)
                .input('I' , EndItems.IRON_ROD)
                .criterion(hasItem(EndItems.ENDERITE_INGOT), conditionsFromItem(EndItems.ENDERITE_INGOT))
                .offerTo(recipeExporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, EndItems.DRAGON_CHARGE)
                .pattern("SDS")
                .pattern("DFD")
                .pattern("SDS")
                .input('S' , Items.SHULKER_SHELL)
            .input('D' , Items.DRAGON_BREATH)
                .input('F' , Items.FIRE_CHARGE)
                .criterion(hasItem(Items.DRAGON_BREATH), conditionsFromItem(Items.DRAGON_BREATH))
                .offerTo(recipeExporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, EndItems.IRON_ROD)
                .pattern("  I")
                .pattern("  I")
                .input('I', Items.IRON_INGOT)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(recipeExporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, EndItems.DEMONIC_BLADE)
                .pattern("  N")
                .pattern("DN ")
                .pattern("ID ")
                .input('N', Items.NETHERITE_INGOT)
                .input('D', EndItems.DEMONIC_ESSENCE)
                .input('I', EndItems.IRON_ROD)
                .criterion(hasItem(EndItems.DEMONIC_ESSENCE), conditionsFromItem(EndItems.DEMONIC_ESSENCE))
                .offerTo(recipeExporter);









    }
}
