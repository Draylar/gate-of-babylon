package draylar.gateofbabylon.registry;

import draylar.gateofbabylon.GateOfBabylon;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class GOBBlocks {

    private static <T extends Block> T register(String name, T block, Item.Settings settings) {
        T registeredBlock = Registry.register(Registries.BLOCK, GateOfBabylon.id(name), block);
        Registry.register(Registries.ITEM, GateOfBabylon.id(name), new BlockItem(registeredBlock, settings));
        return registeredBlock;
    }

    private static <T extends Block> T register(String name, T block) {
        return Registry.register(Registries.BLOCK, GateOfBabylon.id(name), block);
    }

    public static void init() {
        // NO-OP
    }

    private GOBBlocks() {
        // NO-OP
    }
}
