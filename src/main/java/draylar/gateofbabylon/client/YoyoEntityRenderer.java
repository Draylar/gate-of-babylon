package draylar.gateofbabylon.client;

import draylar.gateofbabylon.GateOfBabylonClient;
import draylar.gateofbabylon.entity.YoyoEntity;
import draylar.gateofbabylon.registry.GOBItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.LightType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class YoyoEntityRenderer extends EntityRenderer<YoyoEntity> {

    private static final Map<Item, ModelIdentifier> ITEM_TO_MODEL = new HashMap<>();

    static {
        ITEM_TO_MODEL.put(GOBItems.WOODEN_YOYO, GateOfBabylonClient.WOODEN_YOYO_MODEL);
        ITEM_TO_MODEL.put(GOBItems.STONE_YOYO, GateOfBabylonClient.STONE_YOYO_MODEL);
        ITEM_TO_MODEL.put(GOBItems.IRON_YOYO, GateOfBabylonClient.IRON_YOYO_MODEL);
        ITEM_TO_MODEL.put(GOBItems.GOLDEN_YOYO, GateOfBabylonClient.GOLDEN_YOYO_MODEL);
        ITEM_TO_MODEL.put(GOBItems.DIAMOND_YOYO, GateOfBabylonClient.DIAMOND_YOYO_MODEL);
        ITEM_TO_MODEL.put(GOBItems.NETHERITE_YOYO, GateOfBabylonClient.NETHERITE_YOYO_MODEL);
    }

    public YoyoEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(YoyoEntity yoyo, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        // push new translations
        matrices.push();

        float lerpedAge = MathHelper.lerp(tickDelta, yoyo.age - 1, yoyo.age);

        // render yoyo block
        matrices.push();

        matrices.translate(0, .15, 0);
        matrices.multiply(dispatcher.getRotation());
//        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90));
        matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(lerpedAge));

        BakedModel model = MinecraftClient.getInstance().getBakedModelManager().getModel(ITEM_TO_MODEL.get(yoyo.getStack().getItem()));

        if(model != null) {
            MatrixStack.Entry entry = matrices.peek();
            VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getSolid());
            model.getQuads(null, null, yoyo.world.random).forEach(quad -> {
                consumer.quad(entry, quad, 1.0f, 1.0f, 1.0f, light, OverlayTexture.DEFAULT_UV);
            });
        }

        matrices.pop();

        // render string
        Optional<UUID> owner = yoyo.getOwner();

        if(owner.isPresent()) {
            PlayerEntity player = yoyo.world.getPlayerByUuid(owner.get());

            if (player != null) {
                renderString(yoyo, tickDelta, matrices, vertexConsumers, player);
            }
        }

        matrices.pop();
    }

    private <E extends Entity> void renderString(Entity player, float delta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, Entity yoyo) {
        matrixStack.push();
        Vec3d lerpedYoyoPosition = yoyo.getLerpedPos(delta);
        double d = 0;
        Vec3d lerpedPlayerPosition = player.getLerpedPos(delta);
        double e = Math.cos(d) * lerpedPlayerPosition.z + Math.sin(d) * lerpedPlayerPosition.x;
        double g = Math.sin(d) * lerpedPlayerPosition.z - Math.cos(d) * lerpedPlayerPosition.x;
        double h = MathHelper.lerp(delta, player.prevX, player.getX()) + e;
        double i = MathHelper.lerp(delta, player.prevY, player.getY()) + lerpedPlayerPosition.y;
        double j = MathHelper.lerp(delta, player.prevZ, player.getZ()) + g;
        matrixStack.translate(e, lerpedPlayerPosition.y, g);
        float k = (float)(lerpedYoyoPosition.x - h);
        float l = (float)(lerpedYoyoPosition.y - i);
        float m = (float)(lerpedYoyoPosition.z - j);
        float n = 0.025F;
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getLeash());
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        float o = MathHelper.fastInverseSqrt(k * k + m * m) * 0.025F / 2.0F;
        float p = m * o;
        float q = k * o;
        BlockPos blockPos = new BlockPos(player.getCameraPosVec(delta));
        BlockPos blockPos2 = new BlockPos(yoyo.getCameraPosVec(delta));
        int r = getYoyoBlockLight(player, blockPos);
        int s = getYoyoBlockLight(yoyo, blockPos2);
        int t = player.world.getLightLevel(LightType.SKY, blockPos);
        int u = player.world.getLightLevel(LightType.SKY, blockPos2);
        renderSide(vertexConsumer, matrix4f, k, l, m, r, s, t, u, 0.025F, 0.025F, p, q);
        renderSide(vertexConsumer, matrix4f, k, l, m, r, s, t, u, 0.025F, 0.0F, p, q);
        matrixStack.pop();
    }

    public static void renderSide(VertexConsumer vertexConsumer, Matrix4f matrix4f, float f, float g, float h, int i, int j, int k, int l, float m, float n, float o, float p) {
        for(int r = 0; r < 24; ++r) {
            float s = (float)r / 23.0F;
            int t = (int)MathHelper.lerp(s, (float)i, (float)j);
            int u = (int)MathHelper.lerp(s, (float)k, (float)l);
            int v = LightmapTextureManager.pack(t, u);
            addVertexPair(vertexConsumer, matrix4f, v, f, g, h, m, n, 24, r, false, o, p);
            addVertexPair(vertexConsumer, matrix4f, v, f, g, h, m, n, 24, r + 1, true, o, p);
        }

    }

    public static void addVertexPair(VertexConsumer vertexConsumer, Matrix4f matrix4f, int i, float f, float g, float h, float j, float k, int l, int m, boolean bl, float n, float o) {
        float p = 0.5F;
        float q = 0.4F;
        float r = 0.3F;
        if (m % 2 == 0) {
            p *= 0.7F;
            q *= 0.7F;
            r *= 0.7F;
        }

        float s = (float)m / (float)l;
        float t = f * s;
        float u = g > 0.0F ? g * s * s : g - g * (1.0F - s) * (1.0F - s);
        float v = h * s;
        if (!bl) {
            vertexConsumer.vertex(matrix4f, t + n, u + j - k, v - o).color(1.0f, 1.0f, 1.0f, 1.0F).light(i).next();
        }

        vertexConsumer.vertex(matrix4f, t - n, u + k, v + o).color(1.0f, 1.0f, 1.0f, 1.0F).light(i).next();
        if (bl) {
            vertexConsumer.vertex(matrix4f, t + n, u + j - k, v - o).color(1.0f, 1.0f, 1.0f, 1.0F).light(i).next();
        }

    }

    public int getYoyoBlockLight(Entity entity, BlockPos blockPos) {
        return entity.isOnFire() ? 15 : entity.world.getLightLevel(LightType.BLOCK, blockPos);
    }

    @Override
    public Identifier getTexture(YoyoEntity entity) {
        return null;
    }
}
