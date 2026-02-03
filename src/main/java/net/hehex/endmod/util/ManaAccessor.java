package net.hehex.endmod.util;

public interface ManaAccessor {
    float getMana();
    void setMana(float mana);
    void addMana(float amount);
    boolean useMana(float amount);
}
