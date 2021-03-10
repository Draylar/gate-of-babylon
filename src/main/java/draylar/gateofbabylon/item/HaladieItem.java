package draylar.gateofbabylon.item;

import draylar.gateofbabylon.GateOfBabylon;
import draylar.gateofbabylon.api.EnchantmentHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class HaladieItem extends SwordItem implements EnchantmentHandler {

    public HaladieItem(ToolMaterial material, float effectiveDamage, float effectiveSpeed, Item.Settings settings) {
        super(material, (int) (effectiveDamage - material.getAttackDamage() - 1), -4 + effectiveSpeed, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return super.postHit(stack, target, attacker);
    }
}
