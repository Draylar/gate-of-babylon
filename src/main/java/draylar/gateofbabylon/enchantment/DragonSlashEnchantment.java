package draylar.gateofbabylon.enchantment;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;

public class DragonSlashEnchantment extends KatanaSlashEnchantment {

    public DragonSlashEnchantment() {
        super(SoundEvents.ENTITY_ENDER_DRAGON_AMBIENT, ParticleTypes.WITCH);
    }
}