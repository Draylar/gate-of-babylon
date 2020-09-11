package draylar.gateofbabylon.item;

import draylar.gateofbabylon.GateOfBabylon;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class HaladieItem extends SwordItem {

    public HaladieItem(ToolMaterial material, int attackDamage, float attackSpeed) {
        super(material, attackDamage, attackSpeed, new Item.Settings().group(GateOfBabylon.GROUP));
    }
}
