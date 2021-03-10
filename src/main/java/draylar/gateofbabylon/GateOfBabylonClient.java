package draylar.gateofbabylon;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import draylar.gateofbabylon.client.BoomerangEntityRenderer;
import draylar.gateofbabylon.client.SpearProjectileEntityRenderer;
import draylar.gateofbabylon.client.YoyoEntityRenderer;
import draylar.gateofbabylon.entity.BoomerangEntity;
import draylar.gateofbabylon.entity.SpearProjectileEntity;
import draylar.gateofbabylon.entity.YoyoEntity;
import draylar.gateofbabylon.item.CustomBowItem;
import draylar.gateofbabylon.mixin.DrawableHelperAccessor;
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
import net.minecraft.client.model.Model;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

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
        EntityRendererRegistry.INSTANCE.register(GOBEntities.SPEAR, ((entityRenderDispatcher, context) -> {
            return new SpearProjectileEntityRenderer(entityRenderDispatcher, MinecraftClient.getInstance().getItemRenderer());
        }));

        EntityRendererRegistry.INSTANCE.register(GOBEntities.YOYO, (entityRenderDispatcher, context) -> new YoyoEntityRenderer(entityRenderDispatcher));
        EntityRendererRegistry.INSTANCE.register(GOBEntities.BOOMERANG, (entityRenderDispatcher, context) -> new BoomerangEntityRenderer(entityRenderDispatcher));

        ClientSidePacketRegistry.INSTANCE.register(SpearProjectileEntity.ENTITY_ID, (context, packet) -> {
            double x = packet.readDouble();
            double y = packet.readDouble();
            double z = packet.readDouble();

            int entityId = packet.readInt();

            context.getTaskQueue().execute(() -> {
                SpearProjectileEntity spearEntity = new SpearProjectileEntity(MinecraftClient.getInstance().world, x, y, z);
                spearEntity.setEntityId(entityId);
                MinecraftClient.getInstance().world.addEntity(entityId, spearEntity);
            });
        });

        ClientSidePacketRegistry.INSTANCE.register(YoyoEntity.SPAWN_PACKET_ID, (context, packet) -> {
            double x = packet.readDouble();
            double y = packet.readDouble();
            double z = packet.readDouble();

            int entityId = packet.readInt();

            context.getTaskQueue().execute(() -> {
                YoyoEntity yoyo = new YoyoEntity(MinecraftClient.getInstance().world, x, y, z);
                yoyo.setEntityId(entityId);
                MinecraftClient.getInstance().world.addEntity(entityId, yoyo);
            });
        });

        ClientSidePacketRegistry.INSTANCE.register(BoomerangEntity.SPAWN_PACKET_ID, (context, packet) -> {
            double x = packet.readDouble();
            double y = packet.readDouble();
            double z = packet.readDouble();

            int entityId = packet.readInt();

            context.getTaskQueue().execute(() -> {
                BoomerangEntity boomerang = new BoomerangEntity(MinecraftClient.getInstance().world, x, y, z);
                boomerang.setEntityId(entityId);
                MinecraftClient.getInstance().world.addEntity(entityId, boomerang);
            });
        });

        registerBowPredicates(GOBItems.STONE_BOW);
        registerBowPredicates(GOBItems.IRON_BOW);
        registerBowPredicates(GOBItems.GOLDEN_BOW);
        registerBowPredicates(GOBItems.DIAMOND_BOW);
        registerBowPredicates(GOBItems.NETHERITE_BOW);

        FabricModelPredicateProviderRegistry.register(GOBItems.STONE_SHIELD, new Identifier("blocking"), (stack, world, entity)
                -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);

        FabricModelPredicateProviderRegistry.register(GOBItems.IRON_SHIELD, new Identifier("blocking"), (stack, world, entity)
                -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);

        FabricModelPredicateProviderRegistry.register(GOBItems.GOLDEN_SHIELD, new Identifier("blocking"), (stack, world, entity)
                -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);

        FabricModelPredicateProviderRegistry.register(GOBItems.DIAMOND_SHIELD, new Identifier("blocking"), (stack, world, entity)
                -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);

        FabricModelPredicateProviderRegistry.register(GOBItems.NETHERITE_SHIELD, new Identifier("blocking"), (stack, world, entity)
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
        FabricModelPredicateProviderRegistry.register(bow, new Identifier("pull"), (itemStack, clientWorld, livingEntity) -> {
            if (livingEntity == null) {
                return 0.0F;
            } else {
                return livingEntity.getActiveItem() != itemStack ? 0.0F : (float)(itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / bow.getMaxDrawTime(itemStack);
            }
        });


        FabricModelPredicateProviderRegistry.register(bow, new Identifier("pulling"), (itemStack, clientWorld, livingEntity) -> {
            return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
        });
    }

    public static void renderKatanaOverlay(MatrixStack matrices) {
        MinecraftClient client = MinecraftClient.getInstance();
        int useProgress = Math.min(75, client.player.getItemUseTime());
        float progressPercentage = (float) Math.max(0.01, 1 - useProgress / 75f);

        if(useProgress > 0) {
            // top black
//            fillGradient(matrices, 0, 0, client.getWindow().getScaledWidth(), useProgress, -0xFFFFFF, -0xFFFFFF);

            // top gradient
            fillGradient(
                    matrices,
                    0, // start at left-hand side
                    -75 + useProgress, //
                    client.getWindow().getScaledWidth(),
                    client.getWindow().getScaledHeight() / 4,
                    (int) -(progressPercentage * 0xFF) << 24,
                    -0xFFFFFFFF);

            // bottom
//            fillGradient(matrices, 0, client.getWindow().getScaledHeight() - useProgress, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight(), -0xFFFFFF, -0xFFFFFF);
            fillGradient(matrices,
                    0,
                    client.getWindow().getScaledHeight() / 4 * 3 - useProgress + 75,
                    client.getWindow().getScaledWidth(),
                    client.getWindow().getScaledHeight(),
                    -0xFFFFFFFF,
                    (int) -(progressPercentage * 0xFF) << 24);
        }
    }

    public static void fillGradient(MatrixStack matrices, int xStart, int yStart, int xEnd, int yEnd, int colorStart, int colorEnd) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        DrawableHelperAccessor.callFillGradient(matrices.peek().getModel(), bufferBuilder, xStart, yStart, xEnd, yEnd, 5, colorStart, colorEnd);
        tessellator.draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }
}
