package draylar.gateofbabylon;

import draylar.gateofbabylon.client.BoomerangEntityRenderer;
import draylar.gateofbabylon.client.SpearProjectileEntityRenderer;
import draylar.gateofbabylon.client.YoyoEntityRenderer;
import draylar.gateofbabylon.impl.client.BowPullPredicate;
import draylar.gateofbabylon.impl.client.BowPullingPredicate;
import draylar.gateofbabylon.impl.client.ShieldUsePredicate;
import draylar.gateofbabylon.item.CustomBowItem;
import draylar.gateofbabylon.registry.GOBEntities;
import draylar.gateofbabylon.registry.GOBItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GateOfBabylonClient implements ClientModInitializer {

    public static final ModelIdentifier DIAMOND_YOYO_MODEL = new ModelIdentifier(GateOfBabylon.id("world_diamond_yoyo"), "inventory");
    public static final ModelIdentifier NETHERITE_YOYO_MODEL = new ModelIdentifier(GateOfBabylon.id("world_netherite_yoyo"), "inventory");
    public static final ModelIdentifier GOLDEN_YOYO_MODEL = new ModelIdentifier(GateOfBabylon.id("world_golden_yoyo"), "inventory");
    public static final ModelIdentifier IRON_YOYO_MODEL = new ModelIdentifier(GateOfBabylon.id("world_iron_yoyo"), "inventory");
    public static final ModelIdentifier STONE_YOYO_MODEL = new ModelIdentifier(GateOfBabylon.id("world_stone_yoyo"), "inventory");
    public static final ModelIdentifier WOODEN_YOYO_MODEL = new ModelIdentifier(GateOfBabylon.id("world_wooden_yoyo"), "inventory");

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(GOBEntities.SPEAR, dispatcher -> new SpearProjectileEntityRenderer(dispatcher, MinecraftClient.getInstance().getItemRenderer()));
        EntityRendererRegistry.register(GOBEntities.YOYO, YoyoEntityRenderer::new);
        EntityRendererRegistry.register(GOBEntities.BOOMERANG, BoomerangEntityRenderer::new);

        registerBowPredicates(GOBItems.STONE_BOW);
        registerBowPredicates(GOBItems.IRON_BOW);
        registerBowPredicates(GOBItems.GOLDEN_BOW);
        registerBowPredicates(GOBItems.DIAMOND_BOW);
        registerBowPredicates(GOBItems.NETHERITE_BOW);

        FabricModelPredicateProviderRegistry.register(GOBItems.STONE_SHIELD, new Identifier("blocking"), new ShieldUsePredicate());
        FabricModelPredicateProviderRegistry.register(GOBItems.IRON_SHIELD, new Identifier("blocking"), new ShieldUsePredicate());
        FabricModelPredicateProviderRegistry.register(GOBItems.GOLDEN_SHIELD, new Identifier("blocking"), new ShieldUsePredicate());
        FabricModelPredicateProviderRegistry.register(GOBItems.DIAMOND_SHIELD, new Identifier("blocking"), new ShieldUsePredicate());
        FabricModelPredicateProviderRegistry.register(GOBItems.NETHERITE_SHIELD, new Identifier("blocking"), new ShieldUsePredicate());

        // register models
        ModelLoadingRegistry.INSTANCE.registerModelProvider((resourceManager, consumer) -> {
            consumer.accept(DIAMOND_YOYO_MODEL);
            consumer.accept(NETHERITE_YOYO_MODEL);
            consumer.accept(GOLDEN_YOYO_MODEL);
            consumer.accept(IRON_YOYO_MODEL);
            consumer.accept(STONE_YOYO_MODEL);
            consumer.accept(WOODEN_YOYO_MODEL);
        });
    }

    public static void registerBowPredicates(CustomBowItem bow) {
        FabricModelPredicateProviderRegistry.register(bow, new Identifier("pull"), new BowPullPredicate(bow));
        FabricModelPredicateProviderRegistry.register(bow, new Identifier("pulling"), new BowPullingPredicate());
    }
}
