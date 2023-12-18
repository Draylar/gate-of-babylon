package draylar.gateofbabylon;

import draylar.gateofbabylon.impl.BoomerangDispenserBehavior;
import draylar.gateofbabylon.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GateOfBabylon implements ModInitializer {

    public static final ItemGroup GROUP = FabricItemGroup.builder().displayName(Text.translatable("itemGroup.gateofbabylon.group"))
            .icon(() -> new ItemStack(GOBItems.DIAMOND_SPEAR))
            .entries((context, entries) -> {
                GOBItems.init();

                // Add GOB registry to ItemGroup
                Registries.ITEM.getEntrySet().stream().filter(entry -> entry.getKey().getValue().getNamespace().equals("gateofbabylon")).forEach(item -> {
                    entries.add(new ItemStack(item.getValue()));
                });
            })
            .build();

    @Override
    public void onInitialize() {
        GOBEffects.init();
        GOBItems.init();
        GOBEnchantments.init();
        GOBEntities.init();
        GOBBlocks.init();
        GOBSounds.init();

        Registry.register(Registries.ITEM_GROUP, id("group"), GROUP);
        DispenserBlock.registerBehavior(GOBItems.DIAMOND_BOOMERANG, new BoomerangDispenserBehavior());
    }

    public static Identifier id(String name) {
        return new Identifier("gateofbabylon", name);
    }
}
