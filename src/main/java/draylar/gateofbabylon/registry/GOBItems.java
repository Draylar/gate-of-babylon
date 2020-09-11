package draylar.gateofbabylon.registry;

import draylar.gateofbabylon.GateOfBabylon;
import draylar.gateofbabylon.item.*;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/*
 * Wooden DPS: 6.4
 * Golden DPS: 6.4
 * Stone DPS: 8
 * Iron DPS: 9.6
 * Diamond DPS: 11.2
 * Netherite DPS: 12.8
 *
 * Damage: 1 + material damage + damage
 */
public class GOBItems {

    // Daggers are medium-speed weapons with medium damage.
    public static final DaggerItem WOODEN_DAGGER = register("wooden_dagger", new DaggerItem(ToolMaterials.WOOD, 3f, 2.1f));
    public static final DaggerItem STONE_DAGGER = register("stone_dagger", new DaggerItem(ToolMaterials.STONE, 4f, 2.1f));
    public static final DaggerItem IRON_DAGGER = register("iron_dagger", new DaggerItem(ToolMaterials.IRON, 5f, 2f));
    public static final DaggerItem GOLDEN_DAGGER = register("golden_dagger", new DaggerItem(ToolMaterials.GOLD,3f, 2.1f));
    public static final DaggerItem DIAMOND_DAGGER = register("diamond_dagger", new DaggerItem(ToolMaterials.DIAMOND, 6f, 2f));
    public static final DaggerItem NETHERITE_DAGGER = register("netherite_dagger", new DaggerItem(ToolMaterials.NETHERITE, 6f, 2.3f));

    // Spears are ranged weapons, similar to Tridents.
    public static final SpearItem WOODEN_SPEAR = register("wooden_spear", new SpearItem(ToolMaterials.WOOD, 3.0f, 1.4f));
    public static final SpearItem STONE_SPEAR = register("stone_spear", new SpearItem(ToolMaterials.STONE, 4.0f, 1.4f));
    public static final SpearItem IRON_SPEAR = register("iron_spear", new SpearItem(ToolMaterials.IRON, 6.0f, 1.3f));
    public static final SpearItem GOLDEN_SPEAR = register("golden_spear", new SpearItem(ToolMaterials.GOLD, 5.0f, 1.2f));
    public static final SpearItem DIAMOND_SPEAR = register("diamond_spear", new SpearItem( ToolMaterials.DIAMOND, 7.0f, 1.1f));
    public static final SpearItem NETHERITE_SPEAR = register("netherite_spear", new SpearItem(ToolMaterials.NETHERITE, 7.0f, 1.1f));

    public static final BroadswordItem WOODEN_BROADSWORD = register("wooden_broadsword", new BroadswordItem(ToolMaterials.WOOD, 6f, 1.0f));
    public static final BroadswordItem STONE_BROADSWORD = register("stone_broadsword", new BroadswordItem(ToolMaterials.STONE, 8f, 1.0f));
    public static final BroadswordItem IRON_BROADSWORD = register("iron_broadsword", new BroadswordItem(ToolMaterials.IRON, 10f, 1.0f));
    public static final BroadswordItem GOLDEN_BROADSWORD = register("golden_broadsword", new BroadswordItem(ToolMaterials.GOLD,6f, 1.0f));
    public static final BroadswordItem DIAMOND_BROADSWORD = register("diamond_broadsword", new BroadswordItem(ToolMaterials.DIAMOND, 12f, 1.0f));
    public static final BroadswordItem NETHERITE_BROADSWORD = register("netherite_broadsword", new BroadswordItem(ToolMaterials.NETHERITE, 14f, 1.0f));

    // Rapiers are close-range weapons with a very quick attack speed.
    public static final RapierItem WOODEN_RAPIER = register("wooden_rapier", new RapierItem(ToolMaterials.WOOD, 2f, 3f));
    public static final RapierItem STONE_RAPIER = register("stone_rapier", new RapierItem(ToolMaterials.STONE, 2f, 3.25f));
    public static final RapierItem IRON_RAPIER = register("iron_rapier", new RapierItem(ToolMaterials.IRON, 3f, 3f));
    public static final RapierItem GOLDEN_RAPIER = register("golden_rapier", new RapierItem(ToolMaterials.GOLD,2f, 3f));
    public static final RapierItem DIAMOND_RAPIER = register("diamond_rapier", new RapierItem(ToolMaterials.DIAMOND, 3f, 3.5f));
    public static final RapierItem NETHERITE_RAPIER = register("netherite_rapier", new RapierItem(ToolMaterials.NETHERITE, 4f, 4f));

    public static final HaladieItem WOODEN_HALADIE = register("wooden_haladie", new HaladieItem(ToolMaterials.WOOD, 2, -2.6f));
    public static final HaladieItem STONE_HALADIE = register("stone_haladie", new HaladieItem(ToolMaterials.STONE, 2, -2.6f));
    public static final HaladieItem IRON_HALADIE = register("iron_haladie", new HaladieItem(ToolMaterials.IRON, 3, -2.7f));
    public static final HaladieItem GOLDEN_HALADIE = register("golden_haladie", new HaladieItem(ToolMaterials.GOLD,4, -2.8f));
    public static final HaladieItem DIAMOND_HALADIE = register("diamond_haladie", new HaladieItem(ToolMaterials.DIAMOND, 5, -2.9f));
    public static final HaladieItem NETHERITE_HALADIE = register("netherite_haladie", new HaladieItem(ToolMaterials.NETHERITE, 5, -2.9f));

    public static final CustomShieldItem STONE_SHIELD = register("stone_shield", new CustomShieldItem(new Item.Settings().maxDamage(336).group(GateOfBabylon.GROUP)));
    public static final CustomShieldItem IRON_SHIELD = register("iron_shield", new CustomShieldItem(new Item.Settings().maxDamage(336).group(GateOfBabylon.GROUP)));
    public static final CustomShieldItem GOLDEN_SHIELD = register("golden_shield", new CustomShieldItem(new Item.Settings().maxDamage(336).group(GateOfBabylon.GROUP)));
    public static final CustomShieldItem DIAMOND_SHIELD = register("diamond_shield", new CustomShieldItem(new Item.Settings().maxDamage(336).group(GateOfBabylon.GROUP)));
    public static final CustomShieldItem NETHERITE_SHIELD = register("netherite_shield", new CustomShieldItem(new Item.Settings().maxDamage(336).group(GateOfBabylon.GROUP)));

    public static final Item EXTENDED_STICK = register("extended_stick", new Item(new Item.Settings().group(GateOfBabylon.GROUP)));

    private static <T extends Item> T register(String name, T item) {
        return Registry.register(Registry.ITEM, GateOfBabylon.id(name), item);
    }

    public static void init() {
        FabricModelPredicateProviderRegistry.register(STONE_SHIELD, new Identifier("blocking"), (stack, world, entity)
                -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);

        FabricModelPredicateProviderRegistry.register(IRON_SHIELD, new Identifier("blocking"), (stack, world, entity)
                -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);

        FabricModelPredicateProviderRegistry.register(GOLDEN_SHIELD, new Identifier("blocking"), (stack, world, entity)
                -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);

        FabricModelPredicateProviderRegistry.register(DIAMOND_SHIELD, new Identifier("blocking"), (stack, world, entity)
                -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);
    }

    private GOBItems() {
        // NO-OP
    }
}
