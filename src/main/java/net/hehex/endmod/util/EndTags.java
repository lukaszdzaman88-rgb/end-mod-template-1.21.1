package net.hehex.endmod.util;

import net.hehex.endmod.EndMod;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class EndTags {
    public static class Blocks {
        public static final TagKey<Block> INCORRECT_FOR_DIAMOND_TOOL = createTag("incorrect_for_diamond_tool");
        public static final TagKey<Block> INCORRECT_FOR_NETHERITE_TOOL = createTag("incorrect_for_netherite_tool");

        public static final TagKey<Block> NEEDS_ENDERITE_TOOL = createTag("needs_enderite_tool");
        public static final TagKey<Block> INCORRECT_FOR_ENDERITE_TOOL = createTag("incorrect_for_enderite_tool");


        public static final TagKey<Block> ADVANCED_BOOKSHELVES = createTag("advanced_bookshelves");


        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(EndMod.MOD_ID, name));
        }
    }
}
