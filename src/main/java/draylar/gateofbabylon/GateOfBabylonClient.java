package draylar.gateofbabylon;

import com.mojang.blaze3d.systems.RenderSystem;
import draylar.gateofbabylon.client.SpearProjectileEntityRenderer;
import draylar.gateofbabylon.entity.SpearProjectileEntity;
import draylar.gateofbabylon.item.CustomBowItem;
import draylar.gateofbabylon.mixin.DrawableHelperAccessor;
import draylar.gateofbabylon.registry.GOBEntities;
import draylar.gateofbabylon.registry.GOBItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GateOfBabylonClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(GOBEntities.SPEAR, ((entityRenderDispatcher, context) -> {
            return new SpearProjectileEntityRenderer(entityRenderDispatcher, MinecraftClient.getInstance().getItemRenderer());
        }));

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
