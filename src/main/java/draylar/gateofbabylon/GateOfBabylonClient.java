package draylar.gateofbabylon;

import draylar.gateofbabylon.client.BoomerangEntityRenderer;
import draylar.gateofbabylon.client.SpearProjectileEntityRenderer;
import draylar.gateofbabylon.client.YoyoEntityRenderer;
import draylar.gateofbabylon.entity.BoomerangEntity;
import draylar.gateofbabylon.entity.SpearProjectileEntity;
import draylar.gateofbabylon.entity.YoyoEntity;
import draylar.gateofbabylon.item.CustomBowItem;
import draylar.gateofbabylon.registry.GOBEntities;
import draylar.gateofbabylon.registry.GOBItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GateOfBabylonClient implements ClientModInitializer {

    public static final ModelIdentifier diamond = new ModelIdentifier(GateOfBabylon.id("world_diamond_yoyo"), "inventory");
    public static final ModelIdentifier netherite = new ModelIdentifier(GateOfBabylon.id("world_netherite_yoyo"), "inventory");
    public static final ModelIdentifier golden = new ModelIdentifier(GateOfBabylon.id("world_golden_yoyo"), "inventory");
    public static final ModelIdentifier iron = new ModelIdentifier(GateOfBabylon.id("world_iron_yoyo"), "inventory");
    public static final ModelIdentifier stone = new ModelIdentifier(GateOfBabylon.id("world_stone_yoyo"), "inventory");
    public static final ModelIdentifier wooden = new ModelIdentifier(GateOfBabylon.id("world_wooden_yoyo"), "inventory");

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(GOBEntities.SPEAR, dispatcher -> new SpearProjectileEntityRenderer(dispatcher, MinecraftClient.getInstance().getItemRenderer()));
        EntityRendererRegistry.INSTANCE.register(GOBEntities.YOYO, YoyoEntityRenderer::new);
        EntityRendererRegistry.INSTANCE.register(GOBEntities.BOOMERANG, BoomerangEntityRenderer::new);

        registerBowPredicates(GOBItems.STONE_BOW);
        registerBowPredicates(GOBItems.IRON_BOW);
        registerBowPredicates(GOBItems.GOLDEN_BOW);
        registerBowPredicates(GOBItems.DIAMOND_BOW);
        registerBowPredicates(GOBItems.NETHERITE_BOW);

        FabricModelPredicateProviderRegistry.register(GOBItems.STONE_SHIELD, new Identifier("blocking"), (stack, world, entity, seed)
                -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);

        FabricModelPredicateProviderRegistry.register(GOBItems.IRON_SHIELD, new Identifier("blocking"), (stack, world, entity, seed)
                -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);

        FabricModelPredicateProviderRegistry.register(GOBItems.GOLDEN_SHIELD, new Identifier("blocking"), (stack, world, entity, seed)
                -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);

        FabricModelPredicateProviderRegistry.register(GOBItems.DIAMOND_SHIELD, new Identifier("blocking"), (stack, world, entity, seed)
                -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);

        FabricModelPredicateProviderRegistry.register(GOBItems.NETHERITE_SHIELD, new Identifier("blocking"), (stack, world, entity, seed)
                -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);

        // register models
        ModelLoadingRegistry.INSTANCE.registerAppender(((resourceManager, consumer) -> {
            consumer.accept(diamond);
            consumer.accept(netherite);
            consumer.accept(golden);
            consumer.accept(iron);
            consumer.accept(stone);
            consumer.accept(wooden);
        }));
    }

    public static void registerBowPredicates(CustomBowItem bow) {
        FabricModelPredicateProviderRegistry.register(bow, new Identifier("pull"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) {
                return 0.0F;
            } else {
                return livingEntity.getActiveItem() != itemStack ? 0.0F : (float)(itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / bow.getMaxDrawTime(itemStack);
            }
        });


        FabricModelPredicateProviderRegistry.register(bow, new Identifier("pulling"), (itemStack, clientWorld, livingEntity, seed) -> {
            return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
        });
    }
}
