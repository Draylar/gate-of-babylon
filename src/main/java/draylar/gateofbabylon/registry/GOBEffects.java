package draylar.gateofbabylon.registry;

import draylar.gateofbabylon.GateOfBabylon;
import draylar.gateofbabylon.effect.DragonSlashEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.registry.Registry;

public class GOBEffects {

    public static final DragonSlashEffect DRAGON_SLASH_EFFECT = register("dragon_slash", new DragonSlashEffect());

    private static <T extends StatusEffect> T register(String name, T effect) {
        return Registry.register(Registry.STATUS_EFFECT, GateOfBabylon.id(name), effect);
    }

    public static void init() {
        // NO-OP
    }

    private GOBEffects() {
        // NO-OP
    }
}
