package draylar.gateofbabylon.enchantment;

import draylar.gateofbabylon.api.ValidatingEnchantment;
import draylar.gateofbabylon.item.RapierItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class LungingEnchantment extends Enchantment implements ValidatingEnchantment {

    public LungingEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.WEAPON, new EquipmentSlot[] {
                EquipmentSlot.MAINHAND
        });
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof RapierItem;
    }
}
