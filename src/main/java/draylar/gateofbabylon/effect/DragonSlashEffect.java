package draylar.gateofbabylon.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.InstantStatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class DragonSlashEffect extends InstantStatusEffect {

    public DragonSlashEffect() {
        super(StatusEffectCategory.HARMFUL, 0xcc00d9);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.damage(DamageSource.MAGIC, 4 << amplifier);
    }
}
