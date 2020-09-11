package draylar.gateofbabylon.client;

import draylar.gateofbabylon.entity.SpearProjectileEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class SpearProjectileEntityRenderer extends EntityRenderer<SpearProjectileEntity> {

    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean lit;

    public SpearProjectileEntityRenderer(EntityRenderDispatcher dispatcher, ItemRenderer itemRenderer, float scale, boolean lit) {
        super(dispatcher);
        this.itemRenderer = itemRenderer;
        this.scale = scale;
        this.lit = lit;
    }

    public SpearProjectileEntityRenderer(EntityRenderDispatcher dispatcher, ItemRenderer itemRenderer) {
        this(dispatcher, itemRenderer, 1.0F, false);
    }

    @Override
    protected int getBlockLight(SpearProjectileEntity entity, BlockPos blockPos) {
        return this.lit ? 15 : super.getBlockLight(entity, blockPos);
    }

    @Override
    public void render(SpearProjectileEntity entity, float yaw, float tickDelta, MatrixStack stack, VertexConsumerProvider vertexConsumers, int light) {
        stack.push();
        this.itemRenderer.renderItem(entity.getStack(), ModelTransformation.Mode.FIXED, light, OverlayTexture.DEFAULT_UV, stack, vertexConsumers);
        stack.pop();
        super.render(entity, yaw, tickDelta, stack, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(SpearProjectileEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}
