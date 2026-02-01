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
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

    @Shadow protected abstract List<EnchantmentLevelEntry> generateEnchantments(ItemStack stack, int slot, int level);
    @Shadow protected abstract void broadcastChanges();

    @Unique
    private int endmod$bookshelfTier = 0;

    // Kopiujemy listę pozycji biblioteczek, ponieważ oryginał jest prywatny
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

                // Używamy naszej lokalnej listy OFFSETS i własnego sprawdzenia powietrza
                for (BlockPos offset : POWER_PROVIDER_OFFSETS) {
                    // Własna implementacja canAccessPowerProvider (sprawdzanie powietrza między stołem a półką)
                    if (world.isAir(pos.add(offset.getX() / 2, offset.getY(), offset.getZ() / 2))) {
                        BlockState state = world.getBlockState(pos.add(offset));
                        if (state.isIn(EndTags.Blocks.ADVANCED_BOOKSHELVES)) {
                            advancedShelves++;
                        } else if (state.isIn(BlockTags.ENCHANTMENT_POWER_PROVIDER)) {
                            normalShelves++;
                        }
                    }
                }

                // Logika Tieru
                this.endmod$bookshelfTier = normalShelves + (advancedShelves * 2);

                // Konfiguracja GUI - blokada slotów 2 i 3
                this.enchantmentPower[1] = 0;
                this.enchantmentPower[2] = 0;
                this.levelCost[1] = -1;
                this.levelCost[2] = -1;

                // Konfiguracja slotu 1
                if (this.endmod$bookshelfTier > 0) {
                    this.enchantmentPower[0] = 1;
                    this.levelCost[0] = 5 + (this.endmod$bookshelfTier / 2);
                } else {
                    this.enchantmentPower[0] = 0;
                    this.levelCost[0] = -1;
                }
            });
            this.broadcastChanges();
        }
    }

    @Inject(method = "onButtonClick", at = @At("HEAD"), cancellable = true)
    private void customEnchantButton(PlayerEntity player, int id, CallbackInfoReturnable<Boolean> cir) {
        if (id != 0) {
            cir.setReturnValue(false);
            return;
        }

        ItemStack itemStack = this.inventory.getStack(0);
        ItemStack lapisStack = this.inventory.getStack(1);

        int requiredLevel = 5 + (this.endmod$bookshelfTier / 2);
        int lapisCost = 5;

        if ((lapisStack.getCount() >= lapisCost || player.getAbilities().creativeMode)
                && (player.experienceLevel >= requiredLevel || player.getAbilities().creativeMode)) {

            this.context.run((world, pos) -> {
                int powerLevel = 30 + this.endmod$bookshelfTier;
                List<EnchantmentLevelEntry> list = this.generateEnchantments(itemStack, 0, powerLevel);

                if (!list.isEmpty()) {
                    player.applyEnchantmentCosts(itemStack, requiredLevel);

                    if (!player.getAbilities().creativeMode) {
                        lapisStack.decrement(lapisCost);
                        if (lapisStack.isEmpty()) {
                            this.inventory.setStack(1, ItemStack.EMPTY);
                        }
                    }

                    for (EnchantmentLevelEntry entry : list) {
                        itemStack.addEnchantment(entry.enchantment, entry.level);
                    }

                    player.incrementStat(Stats.ENCHANT_ITEM);
                    if (player instanceof net.minecraft.server.network.ServerPlayerEntity) {
                        net.minecraft.advancement.criterion.Criteria.ENCHANTED_ITEM.trigger((net.minecraft.server.network.ServerPlayerEntity)player, itemStack, requiredLevel);
                    }
                    this.inventory.markDirty();
                    this.enchantmentId[0] = player.getEnchantmentTableSeed();
                    this.onContentChanged(this.inventory);
                    world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);
                }
            });

            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(false);
        }
    }

    @ModifyVariable(method = "generateEnchantments", at = @At("RETURN"), ordinal = 0)
    private List<EnchantmentLevelEntry> filterEnchantments(List<EnchantmentLevelEntry> originalList, ItemStack stack) {
        if (stack.isOf(Items.FISHING_ROD)) {
            return originalList;
        }

        return originalList.stream()
                .map(entry -> {
                    int level = entry.level;
                    var key = entry.enchantment.getKey().orElse(null);
                    if (key == null) return entry;

                    // 1. Zablokowane
                    if (key.equals(Enchantments.INFINITY) ||
                            key.equals(Enchantments.FIRE_ASPECT) ||
                            key.equals(Enchantments.FLAME)) {
                        return null;
                    }

                    // 2. Bronie (Max 1)
                    boolean isWeaponEnchant = key.equals(Enchantments.SHARPNESS) ||
                            key.equals(Enchantments.SMITE) ||
                            key.equals(Enchantments.BANE_OF_ARTHROPODS) ||
                            key.equals(Enchantments.POWER) ||
                            key.equals(Enchantments.PUNCH);

                    if (isWeaponEnchant && level > 1) {
                        return new EnchantmentLevelEntry(entry.enchantment, 1);
                    }

                    // 3. Narzędzia (Max 3)
                    if (key.equals(Enchantments.EFFICIENCY) ||
                            key.equals(Enchantments.FORTUNE) ||
                            key.equals(Enchantments.UNBREAKING)) {
                        if (level > 3) {
                            return new EnchantmentLevelEntry(entry.enchantment, 3);
                        }
                    }

                    return entry;
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }
}