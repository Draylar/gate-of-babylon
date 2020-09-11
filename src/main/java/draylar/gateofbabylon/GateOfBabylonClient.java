package draylar.gateofbabylon;

import draylar.gateofbabylon.client.SpearProjectileEntityRenderer;
import draylar.gateofbabylon.entity.SpearProjectileEntity;
import draylar.gateofbabylon.registry.GOBEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;

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
    }
}
