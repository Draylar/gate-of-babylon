package draylar.gateofbabylon.item;

import draylar.gateofbabylon.GateOfBabylon;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class HaladieItem extends SwordItem {

    public HaladieItem(ToolMaterial material, int attackDamage, float attackSpeed) {
        super(material, attackDamage, attackSpeed, new Item.Settings().group(GateOfBabylon.GROUP));
    }

    public HaladieItem(ToolMaterial material, float effectiveDamage, float effectiveSpeed) {
        super(material, (int) (effectiveDamage - material.getAttackDamage() - 1), -4 + effectiveSpeed, new Item.Settings().group(GateOfBabylon.GROUP).maxCount(1));
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return super.postHit(stack, target, attacker);
    }
}
