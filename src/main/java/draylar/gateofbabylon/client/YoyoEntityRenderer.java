package draylar.gateofbabylon.client;

import draylar.gateofbabylon.entity.YoyoEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

import java.util.Optional;
import java.util.UUID;

public class YoyoEntityRenderer extends EntityRenderer<YoyoEntity> {

    public YoyoEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(YoyoEntity yoyo, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.push();

        float lerpedAge = MathHelper.lerp(tickDelta, yoyo.age - 1, yoyo.age);
        matrices.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(lerpedAge));

        // render yoyo block
        MinecraftClient.getInstance().getItemRenderer()
                .renderItem(
                        yoyo.getStack().isEmpty() ? new ItemStack(Items.DIRT) : yoyo.getStack(),
                        ModelTransformation.Mode.FIXED,
                        light,
                        OverlayTexture.DEFAULT_UV,
                        matrices,
                        vertexConsumers
                );

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

    private <E extends Entity> void renderString(Entity player, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, Entity yoyo) {
        matrixStack.push();
        Vec3d lerpedYoyoPosition = yoyo.method_30951(f);
        double d = 0;
        Vec3d lerpedPlayerPosition = player.method_29919();
        double e = Math.cos(d) * lerpedPlayerPosition.z + Math.sin(d) * lerpedPlayerPosition.x;
        double g = Math.sin(d) * lerpedPlayerPosition.z - Math.cos(d) * lerpedPlayerPosition.x;
        double h = MathHelper.lerp((double)f, player.prevX, player.getX()) + e;
        double i = MathHelper.lerp((double)f, player.prevY, player.getY()) + lerpedPlayerPosition.y;
        double j = MathHelper.lerp((double)f, player.prevZ, player.getZ()) + g;
        matrixStack.translate(e, lerpedPlayerPosition.y, g);
        float k = (float)(lerpedYoyoPosition.x - h);
        float l = (float)(lerpedYoyoPosition.y - i);
        float m = (float)(lerpedYoyoPosition.z - j);
        float n = 0.025F;
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getLeash());
        Matrix4f matrix4f = matrixStack.peek().getModel();
        float o = MathHelper.fastInverseSqrt(k * k + m * m) * 0.025F / 2.0F;
        float p = m * o;
        float q = k * o;
        BlockPos blockPos = new BlockPos(player.getCameraPosVec(f));
        BlockPos blockPos2 = new BlockPos(yoyo.getCameraPosVec(f));
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
            vertexConsumer.vertex(matrix4f, t + n, u + j - k, v - o).color(p, q, r, 1.0F).light(i).next();
        }

        vertexConsumer.vertex(matrix4f, t - n, u + k, v + o).color(p, q, r, 1.0F).light(i).next();
        if (bl) {
            vertexConsumer.vertex(matrix4f, t + n, u + j - k, v - o).color(p, q, r, 1.0F).light(i).next();
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
