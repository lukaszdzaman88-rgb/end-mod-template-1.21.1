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
    @Shadow @Final public int[] enchantmentPower; // Wymagany poziom do wyświetlenia
    @Shadow @Final public int[] enchantmentId;    // Seed
    @Shadow @Final public int[] levelCost;        // Koszt lapisu/leveli (zielona cyfra)

    @Shadow protected abstract List<EnchantmentLevelEntry> generateEnchantments(ItemStack stack, int slot, int level);
    @Shadow protected abstract void broadcastChanges();

    @Unique
    private int endmod$currentPower = 0;
    @Unique
    private boolean endmod$hasAdvancedShelves = false;

    // Definiujemy offsety lokalnie, ponieważ oryginał jest prywatny
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
                    // Sprawdzamy czy pomiędzy stołem a półką jest powietrze (lub inny blok przepuszczający)
                    if (world.getBlockState(pos.add(offset.getX() / 2, offset.getY(), offset.getZ() / 2)).isIn(BlockTags.ENCHANTMENT_POWER_TRANSMITTER)) {
                        BlockState state = world.getBlockState(pos.add(offset));
                        if (state.isIn(EndTags.Blocks.ADVANCED_BOOKSHELVES)) {
                            advancedShelves++;
                        } else if (state.isIn(BlockTags.ENCHANTMENT_POWER_PROVIDER)) {
                            normalShelves++;
                        }
                    }
                }

                this.endmod$hasAdvancedShelves = (advancedShelves > 0);

                // Obliczanie "Score" biblioteczek
                // Zwykła = 1 pkt, Zaawansowana = 2 pkt (możesz zmienić balans)
                int score = normalShelves + (advancedShelves * 2);

                // Efektywny poziom mocy (w vanilli 15 półek = 30 level)
                // Przyjmujemy: poziom = score * 2
                this.endmod$currentPower = score * 2;

                // --- KONFIGURACJA SLOTÓW ---

                // Zawsze blokujemy sloty 2 i 3 (indeksy 1, 2)
                this.enchantmentPower[1] = 0;
                this.enchantmentPower[2] = 0;
                this.levelCost[1] = -1;
                this.levelCost[2] = -1;

                // Konfiguracja slotu 1 (indeks 0)
                // DZIAŁA TYLKO JEŚLI MAMY WYSTARCZAJĄCO MOCY NA MAX LEVEL (czyli 30+)
                if (this.endmod$currentPower >= 30) {
                    // Wymagany poziom (do kliknięcia) równa się aktualnej mocy (skaluje się w górę)
                    this.enchantmentPower[0] = this.endmod$currentPower;

                    // Koszt w lapisie (i poziomach do zabrania)
                    this.levelCost[0] = 5;
                } else {
                    // Za słaba biblioteka -> wyłączamy przycisk
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

        // Sprawdź czy stół ma wystarczającą moc (zabezpieczenie serwera)
        if (this.endmod$currentPower < 30) {
            cir.setReturnValue(false);
            return;
        }

        ItemStack itemStack = this.inventory.getStack(0);
        ItemStack lapisStack = this.inventory.getStack(1);

        int requiredLevel = this.endmod$currentPower; // Np. 30, 32, 40...
        int costLapis = 5;
        int costLevels = 5; // Ile poziomów zabieramy graczowi

        // Sprawdzenie warunków: Czy masz 5 lapisu ORAZ czy masz wymagany wysoki level
        if ((lapisStack.getCount() >= costLapis || player.getAbilities().creativeMode)
                && (player.experienceLevel >= requiredLevel || player.getAbilities().creativeMode)) {

            this.context.run((world, pos) -> {
                // Generujemy enchanty z pełną mocą (np. 30+)
                List<EnchantmentLevelEntry> list = this.generateEnchantments(itemStack, 0, this.endmod$currentPower);

                if (!list.isEmpty()) {
                    // Zabieramy poziomy (koszt = 5)
                    player.applyEnchantmentCosts(itemStack, costLevels);

                    // Zabieramy Lapis
                    if (!player.getAbilities().creativeMode) {
                        lapisStack.decrement(costLapis);
                        if (lapisStack.isEmpty()) {
                            this.inventory.setStack(1, ItemStack.EMPTY);
                        }
                    }

                    // Aplikujemy enchanty
                    for (EnchantmentLevelEntry entry : list) {
                        itemStack.addEnchantment(entry.enchantment, entry.level);
                    }

                    // Statystyki i efekty
                    player.incrementStat(Stats.ENCHANT_ITEM);
                    if (player instanceof net.minecraft.server.network.ServerPlayerEntity) {
                        net.minecraft.advancement.criterion.Criteria.ENCHANTED_ITEM.trigger((net.minecraft.server.network.ServerPlayerEntity)player, itemStack, costLevels);
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

    /**
     * Filtrowanie zaklęć.
     * Działa tylko, jeśli NIE używamy zaawansowanych biblioteczek (czyli endmod$hasAdvancedShelves == false).
     */
    @ModifyVariable(method = "generateEnchantments", at = @At("RETURN"), ordinal = 0)
    private List<EnchantmentLevelEntry> filterEnchantments(List<EnchantmentLevelEntry> originalList, ItemStack stack) {
        // Wędka zawsze bez zmian
        if (stack.isOf(Items.FISHING_ROD)) {
            return originalList;
        }

        // Jeśli mamy lepsze biblioteczki, zdejmujemy restrykcje (lub stosujemy inną logikę)
        if (this.endmod$hasAdvancedShelves) {
            return originalList; // Tutaj zwracamy pełną listę, bo to "lepszy" stół
        }

        // Restrykcje dla zwykłych biblioteczek (Tier 30, ale zwykły)
        return originalList.stream()
                .map(entry -> {
                    int level = entry.level;
                    var key = entry.enchantment.getKey().orElse(null);
                    if (key == null) return entry;

                    // 1. Zablokowane całkowicie
                    if (key.equals(Enchantments.INFINITY) ||
                            key.equals(Enchantments.FIRE_ASPECT) ||
                            key.equals(Enchantments.FLAME)) {
                        return null;
                    }

                    // 2. Bronie (Max poziom 1)
                    boolean isWeaponEnchant = key.equals(Enchantments.SHARPNESS) ||
                            key.equals(Enchantments.SMITE) ||
                            key.equals(Enchantments.BANE_OF_ARTHROPODS) ||
                            key.equals(Enchantments.POWER) ||
                            key.equals(Enchantments.PUNCH);

                    if (isWeaponEnchant && level > 1) {
                        return new EnchantmentLevelEntry(entry.enchantment, 1);
                    }

                    // 3. Narzędzia i inne (Max poziom 3)
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