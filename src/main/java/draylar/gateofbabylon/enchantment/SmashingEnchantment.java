package draylar.gateofbabylon.enchantment;

import draylar.gateofbabylon.api.ValidatingEnchantment;
import draylar.gateofbabylon.item.WaraxeItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class SmashingEnchantment extends Enchantment implements ValidatingEnchantment {

    public SmashingEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return other != Enchantments.LOOTING;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof WaraxeItem;
    }
}
