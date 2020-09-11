package draylar.gateofbabylon;

import draylar.gateofbabylon.registry.GOBBlocks;
import draylar.gateofbabylon.registry.GOBEnchantments;
import draylar.gateofbabylon.registry.GOBEntities;
import draylar.gateofbabylon.registry.GOBItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class GateOfBabylon implements ModInitializer {

    public static final ItemGroup GROUP = FabricItemGroupBuilder.build(id("group"), () -> new ItemStack(GOBItems.DIAMOND_SPEAR));

    public static Identifier id(String name) {
        return new Identifier("gateofbabylon", name);
    }

    @Override
    public void onInitialize() {
        GOBItems.init();
        GOBEnchantments.init();
        GOBEntities.init();
        GOBBlocks.init();
    }
}
