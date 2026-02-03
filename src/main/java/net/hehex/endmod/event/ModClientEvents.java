package net.hehex.endmod.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.hehex.endmod.attribute.ModAttributes;
import net.hehex.endmod.item.custom.AssassinWeapon;
import net.hehex.endmod.util.AssassinPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class ModClientEvents {

    // Zmienna do śledzenia, czy napis był wyświetlony w poprzedniej klatce
    private static boolean wasStealthReady = false;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if (client.player instanceof AssassinPlayer assassinPlayer) {
                ItemStack mainHandStack = client.player.getMainHandStack();

                // Sprawdzamy czy to broń Assasina
                if (mainHandStack.getItem() instanceof AssassinWeapon) {

                    if (client.player.getAttributes().hasAttribute(ModAttributes.MAX_STEALTH)) {
                        float currentStealth = assassinPlayer.getStealth();
                        float maxStealth = (float) client.player.getAttributeValue(ModAttributes.MAX_STEALTH);

                        boolean isReady = currentStealth >= maxStealth - 0.9f;

                        if (isReady) {
                            // Wyświetl napis "READY"
                            client.player.sendMessage(
                                    Text.literal("★ STEALTH STRIKE READY ★").formatted(Formatting.GOLD, Formatting.BOLD),
                                    true
                            );
                            wasStealthReady = true; // Zapamiętaj stan
                        } else {
                            // === KLUCZOWA ZMIANA ===
                            // Jeśli WŁAŚNIE przestaliśmy być gotowi (np. po ataku), natychmiast wyczyść pasek
                            if (wasStealthReady) {
                                client.player.sendMessage(Text.empty(), true); // Pusta wiadomość czyści Action Bar
                                wasStealthReady = false;
                            }
                        }
                    }
                } else {
                    // Jeśli zmieniliśmy broń na inną, też wyczyść napis
                    if (wasStealthReady) {
                        client.player.sendMessage(Text.empty(), true);
                        wasStealthReady = false;
                    }
                }
            }
        });
    }
}