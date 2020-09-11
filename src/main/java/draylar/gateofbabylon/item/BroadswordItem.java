package draylar.gateofbabylon.item;

import draylar.gateofbabylon.GateOfBabylon;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class BroadswordItem extends SwordItem {

    public BroadswordItem(ToolMaterial material, int attackDamage, float attackSpeed) {
        super(material, attackDamage, attackSpeed, new Item.Settings().group(GateOfBabylon.GROUP).maxCount(1));
    }

    public BroadswordItem(ToolMaterial material, float effectiveDamage, float effectiveSpeed) {
        super(material, (int) (effectiveDamage - material.getAttackDamage()), -4 + effectiveSpeed, new Item.Settings().group(GateOfBabylon.GROUP).maxCount(1));
    }
}
