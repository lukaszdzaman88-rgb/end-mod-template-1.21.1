package net.hehex.endmod.util;

public interface AssassinPlayer {
    float getStealth();
    void setStealth(float stealth);

    // Nowe metody do kontroli ataku
    boolean isAttacking();
    void setAttacking(boolean attacking);
}