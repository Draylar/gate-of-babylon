package draylar.gateofbabylon.api;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;

import java.util.Collections;
import java.util.List;

public interface EnchantmentHandler {
    default List<EnchantmentTarget> getEnchantmentTypes() {
        return Collections.emptyList();
    }

    default boolean isInvalid(Enchantment enchantment) {
        return false;
    }

    default boolean isExplicitlyValid(Enchantment enchantment) { return false; }
}
