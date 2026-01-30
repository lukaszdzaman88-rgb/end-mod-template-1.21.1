package net.hehex.endmod.damage;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModDamageTypes {
    // Rejestrujemy KLUCZ, który wskazuje na nasz plik JSON
    public static final RegistryKey<DamageType> SCYTHE_DAMAGE = RegistryKey.of(
            RegistryKeys.DAMAGE_TYPE,
            Identifier.of("endmod", "scythe_damage")
    );

    public static void initialize() {
        // Ta metoda może pozostać pusta, służy tylko do załadowania klasy
        // Rejestracja JSON odbywa się automatycznie przez silnik gry
    }
}
