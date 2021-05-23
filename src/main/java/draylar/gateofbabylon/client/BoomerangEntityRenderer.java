package draylar.gateofbabylon.client;

import draylar.gateofbabylon.entity.BoomerangEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class BoomerangEntityRenderer extends EntityRenderer<BoomerangEntity> {

    public BoomerangEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(BoomerangEntity boomerang, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        float lerpedAge = MathHelper.lerp(tickDelta, boomerang.age - 1, boomerang.age);
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90));
        matrices.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(lerpedAge));

        MinecraftClient.getInstance().getItemRenderer().renderItem(
                boomerang.getStack(),
                ModelTransformation.Mode.FIXED,
                light,
                OverlayTexture.DEFAULT_UV,
                matrices,
                vertexConsumers
        );


        matrices.pop();
    }

    @Override
    public Identifier getTexture(BoomerangEntity entity) {
        return null;
    }
}
