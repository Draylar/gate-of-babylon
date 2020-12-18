package draylar.gateofbabylon.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.InstantStatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class DragonSlashEffect extends InstantStatusEffect {

    public DragonSlashEffect() {
        super(StatusEffectType.HARMFUL, 0xcc00d9);
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.damage(DamageSource.MAGIC, 4 << amplifier);
    }
}
