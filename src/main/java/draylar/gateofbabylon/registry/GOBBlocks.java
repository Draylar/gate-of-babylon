package draylar.gateofbabylon.registry;

import draylar.gateofbabylon.GateOfBabylon;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class GOBBlocks {

    private static <T extends Block> T register(String name, T block, Item.Settings settings) {
        T registeredBlock = Registry.register(Registry.BLOCK, GateOfBabylon.id(name), block);
        Registry.register(Registry.ITEM, GateOfBabylon.id(name), new BlockItem(registeredBlock, settings));
        return registeredBlock;
    }

    private static <T extends Block> T register(String name, T block) {
        return Registry.register(Registry.BLOCK, GateOfBabylon.id(name), block);
    }

    public static void init() {
        // NO-OP
    }

    private GOBBlocks() {
        // NO-OP
    }
}
