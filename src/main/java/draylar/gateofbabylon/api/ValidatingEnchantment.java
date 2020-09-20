package draylar.gateofbabylon.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Defines an {@link net.minecraft.enchantment.Enchantment} that approves items through {@link net.minecraft.enchantment.Enchantment#isAcceptableItem(ItemStack)}
 *    rather than {@link net.minecraft.enchantment.EnchantmentTarget#isAcceptableItem(Item)} in enchantment tables.
 */
public interface ValidatingEnchantment {
}
