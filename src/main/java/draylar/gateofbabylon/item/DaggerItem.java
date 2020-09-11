package draylar.gateofbabylon.item;

import draylar.gateofbabylon.GateOfBabylon;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class DaggerItem extends SwordItem {

    public DaggerItem(ToolMaterial material, int attackDamage, float functionalSpeed) {
        super(material, attackDamage, -4 + functionalSpeed, new Item.Settings().group(GateOfBabylon.GROUP).maxCount(1));
    }

    public DaggerItem(ToolMaterial material, float effectiveDamage, float effectiveSpeed) {
        super(material, (int) (effectiveDamage - material.getAttackDamage() - 1), -4 + effectiveSpeed, new Item.Settings().group(GateOfBabylon.GROUP).maxCount(1));
    }
}
