package draylar.gateofbabylon.registry;

import draylar.gateofbabylon.GateOfBabylon;
import draylar.gateofbabylon.enchantment.LungingEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.Registry;

public class GOBEnchantments {

    public static final LungingEnchantment LUNGING = register("lunging", new LungingEnchantment());

    private static <T extends Enchantment> T register(String name, T enchantment) {
        return Registry.register(Registry.ENCHANTMENT, GateOfBabylon.id(name), enchantment);
    }

    public static void init() {
        // NO-OP
    }

    private GOBEnchantments() {
        // NO-OP
    }
}
