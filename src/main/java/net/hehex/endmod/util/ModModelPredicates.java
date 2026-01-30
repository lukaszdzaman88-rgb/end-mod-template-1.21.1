package net.hehex.endmod.util;

import net.hehex.endmod.item.EndItems;
import net.hehex.endmod.item.custom.ModBowItem;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class ModModelPredicates {

    public static void registerModelPredicates() {
        // Rejestrujemy predykaty dla Twojego łuku
        registerBow(EndItems.WOODEN_SHORTBOW); // Upewnij się, że to nazwa zmiennej Twojego Wooden Shortbow
    }

    private static void registerBow(Item bow) {
        // 1. Predykat "pull" - postęp naciągania (0.0 - 1.0)
        ModelPredicateProviderRegistry.register(bow, Identifier.of("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0F;
            }
            if (entity.getActiveItem() != stack) {
                return 0.0F;
            }

            // --- POPRAWKA MATEMATYKI ---

            // 1. Obliczamy, ile ticków gracz już trzyma łuk
            int useTicks = stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft();

            // 2. Ustalamy "dzielnik". Domyślnie 20.0 (dla zwykłych przedmiotów).
            float maxDrawTime = 20.0F;

            // 3. Sprawdzamy, czy przedmiot to nasza klasa ModBowItem.
            // Jeśli tak, pobieramy jego WŁASNĄ prędkość (np. 16 ticków).
            if (stack.getItem() instanceof ModBowItem customBow) {
                maxDrawTime = (float) customBow.getDrawSpeed();
            }

            // 4. Dzielimy: (aktualny czas / maksymalny czas).
            // Dzięki temu wynik zawsze będzie od 0.0 do 1.0, niezależnie czy łuk jest szybki czy wolny.
            return (float) useTicks / maxDrawTime;
        });

        // 2. Predykat "pulling" - czy w ogóle jest naciągany
        ModelPredicateProviderRegistry.register(bow, Identifier.of("pulling"), (stack, world, entity, seed) -> {
            return entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F;
        });
    }
}