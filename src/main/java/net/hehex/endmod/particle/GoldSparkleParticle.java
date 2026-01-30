package net.hehex.endmod.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class GoldSparkleParticle extends SpriteBillboardParticle {

    // Konstruktor
    protected GoldSparkleParticle(ClientWorld world, double x, double y, double z, double vx, double vy, double vz) {
        // POPRAWKA 1: W 1.21 'super' przyjmuje tylko world i pozycję (x, y, z)
        super(world, x, y, z);

        // Prędkość (velocity) musimy przypisać ręcznie, bo super jej nie przyjmuje
        this.velocityX = vx;
        this.velocityY = vy;
        this.velocityZ = vz;

        // FIZYKA
        this.velocityMultiplier = 0.96F; // Tarcie powietrza
        this.scale = 0.2F;               // Wielkość
        this.maxAge = 40;                // Długość życia (ticki)

        // POPRAWKA 2: 'ascending' nie istnieje.
        // Żeby cząsteczka nie spadała, ustawiamy brak grawitacji:
        this.gravityStrength = 1.0F;

        // KOLOR (#FFD700 - Złoty)
        this.setColor(1.0f, 0.84f, 0.0f);
    }

    @Override
    public ParticleTextureSheet getType() {
        // Typ renderowania - przezroczysty
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.fadeOut(); // Powolne znikanie
    }

    private void fadeOut() {
        float ageProgress = (float)this.age / (float)this.maxAge;
        this.alpha = 1.0f - ageProgress;
        if (this.alpha < 0) this.alpha = 0;
    }

    // POPRAWKA 3: To jest ta klasa Factory, której brakowało (dlatego miałeś błąd na czerwono w drugim pliku)
    // Musi być wewnątrz klasy GoldSparkleParticle!
    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientWorld world, double x, double y, double z, double vx, double vy, double vz) {
            GoldSparkleParticle p = new GoldSparkleParticle(world, x, y, z, vx, vy, vz);
            p.setSprite(this.spriteProvider); // Ważne: przypisanie tekstury
            return p;
        }
    }
}