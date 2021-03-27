package draylar.gateofbabylon.registry;

import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;

public class GOBDamageSources {

    public static EntityDamageSource createBoomerangSource(PlayerEntity player) {
        return new EntityDamageSource("boomerang", player);
    }

    public static EntityDamageSource createYoyoSource(PlayerEntity player) {
        return new EntityDamageSource("yoyo", player);
    }
}
