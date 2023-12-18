package draylar.gateofbabylon.client;

import draylar.gateofbabylon.entity.BoomerangEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class BoomerangEntityRenderer extends EntityRenderer<BoomerangEntity> {

    public BoomerangEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(BoomerangEntity boomerang, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        float lerpedAge = MathHelper.lerp(tickDelta, boomerang.age - 1, boomerang.age);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(lerpedAge));

        MinecraftClient.getInstance().getItemRenderer().renderItem(
                boomerang.getStack(),
                ModelTransformationMode.FIXED,
                light,
                OverlayTexture.DEFAULT_UV,
                matrices,
                vertexConsumers,
                boomerang.getWorld(),
                0
        );


        matrices.pop();
    }

    @Override
    public Identifier getTexture(BoomerangEntity entity) {
        return null;
    }
}
