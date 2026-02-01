package net.hehex.endmod.mixin;

import net.hehex.endmod.util.EndTags;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(EnchantmentScreenHandler.class)
public abstract class EnchantmentScreenHandlerMixin extends ScreenHandler {

    @Shadow @Final private ScreenHandlerContext context;
    @Shadow @Final private Inventory inventory;
    @Shadow @Final public int[] enchantmentPower;
    @Shadow @Final public int[] enchantmentId;
    @Shadow @Final public int[] levelCost;

    @Shadow protected abstract void broadcastChanges();

    // POPRAWKA: Używamy nazwy "method_17410" (intermediary) zamiast "generateEnchantments"
    @Invoker("method_17410")
    protected abstract List<EnchantmentLevelEntry> invokeGenerateEnchantments(ItemStack stack, int slot, int level);

    @Unique
    private int endmod$currentPower = 0;
    @Unique
    private boolean endmod$hasAdvancedShelves = false;

    @Unique
    private static final List<BlockPos> POWER_PROVIDER_OFFSETS = List.of(
            new BlockPos(-2, 0, 0), new BlockPos(-2, 0, 1), new BlockPos(-2, 0, -1),
            new BlockPos(2, 0, 0), new BlockPos(2, 0, 1), new BlockPos(2, 0, -1),
            new BlockPos(0, 0, -2), new BlockPos(1, 0, -2), new BlockPos(-1, 0, -2),
            new BlockPos(0, 0, 2), new BlockPos(1, 0, 2), new BlockPos(-1, 0, 2),
            new BlockPos(2, 1, 0), new BlockPos(2, 1, 1), new BlockPos(2, 1, -1),
            new BlockPos(-2, 1, 0), new BlockPos(-2, 1, 1), new BlockPos(-2, 1, -1),
            new BlockPos(0, 1, -2), new BlockPos(1, 1, -2), new BlockPos(-1, 1, -2),
            new BlockPos(0, 1, 2), new BlockPos(1, 1, 2), new BlockPos(-1, 1, 2),
            new BlockPos(2, 2, 0), new BlockPos(2, 2, 1), new BlockPos(2, 2, -1),
            new BlockPos(-2, 2, 0), new BlockPos(-2, 2, 1), new BlockPos(-2, 2, -1),
            new BlockPos(0, 2, -2), new BlockPos(1, 2, -2), new BlockPos(-1, 2, -2),
            new BlockPos(0, 2, 2), new BlockPos(1, 2, 2), new BlockPos(-1, 2, 2)
    );

    protected EnchantmentScreenHandlerMixin(ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "onContentChanged", at = @At("TAIL"))
    private void calculateCustomTablePower(Inventory inventory, CallbackInfo ci) {
        if (inventory == this.inventory) {
            this.context.run((world, pos) -> {
                int advancedShelves = 0;
                int normalShelves = 0;

                for (BlockPos offset : POWER_PROVIDER_OFFSETS) {
                    if (world.getBlockState(pos.add(offset.getX() / 2, offset.getY(), offset.getZ() / 2)).isIn(BlockTags.ENCHANTMENT_POWER_TRANSMITTER)) {
                        BlockState state = world.getBlockState(pos.add(offset));
                        // Upewnij się, że masz ten TAG w EndTags.java!
                        if (state.isIn(EndTags.Blocks.ADVANCED_BOOKSHELVES)) {
                            advancedShelves++;
                        } else if (state.isIn(BlockTags.ENCHANTMENT_POWER_PROVIDER)) {
                            normalShelves++;
                        }
                    }
                }

                this.endmod$hasAdvancedShelves = (advancedShelves > 0);
                int score = normalShelves + (advancedShelves * 2);
                this.endmod$currentPower = score * 2;

                this.enchantmentPower[1] = 0;
                this.enchantmentPower[2] = 0;
                this.levelCost[1] = -1;
                this.levelCost[2] = -1;

                if (this.endmod$currentPower >= 30) {
                    this.enchantmentPower[0] = this.endmod$currentPower;
                    this.levelCost[0] = 5;
                }});}}}