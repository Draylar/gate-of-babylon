package draylar.gateofbabylon.registry;

import draylar.gateofbabylon.GateOfBabylon;
import draylar.gateofbabylon.client.BoomerangEntityRenderer;
import draylar.gateofbabylon.client.YoyoEntityRenderer;
import draylar.gateofbabylon.entity.BoomerangEntity;
import draylar.gateofbabylon.entity.SpearProjectileEntity;
import draylar.gateofbabylon.entity.YoyoEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class GOBEntities {

    /**
     * {@link SpearProjectileEntity}, {@link draylar.gateofbabylon.client.SpearProjectileEntityRenderer}
     */
    public static final EntityType<SpearProjectileEntity> SPEAR = register(
            "spear",
            FabricEntityTypeBuilder
                    .<SpearProjectileEntity>create(SpawnGroup.MISC, (SpearProjectileEntity::new))
                    .trackable(128, 4)
                    .dimensions(EntityDimensions.fixed(.5f, .5f)).build());

    /**
     * {@link YoyoEntity}, {@link YoyoEntityRenderer}
     */
    public static final EntityType<YoyoEntity> YOYO = register(
            "yoyo",
            FabricEntityTypeBuilder
                .<YoyoEntity>create(SpawnGroup.MISC, YoyoEntity::new)
                    .trackRangeBlocks(128)
                    .trackedUpdateRate(1)
                    .forceTrackedVelocityUpdates(true)
                .dimensions(EntityDimensions.fixed(.25f, .25f)).build());

    /**
     * {@link BoomerangEntity}, {@link BoomerangEntityRenderer}
     */
    public static final EntityType<BoomerangEntity> BOOMERANG = register(
            "boomerang",
            FabricEntityTypeBuilder
                    .<BoomerangEntity>create(SpawnGroup.MISC, BoomerangEntity::new)
                    .trackRangeBlocks(128)
                    .trackedUpdateRate(1)
                    .forceTrackedVelocityUpdates(true)
                    .dimensions(EntityDimensions.fixed(.5f, .1f)).build());

    private static <T extends Entity> EntityType<T> register(String name, EntityType<T> entity) {
        return Registry.register(Registries.ENTITY_TYPE, GateOfBabylon.id(name), entity);
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> entity) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, GateOfBabylon.id(name), entity);
    }

    public static void init() {
        // NO-OP
    }

    private GOBEntities() {
        // NO-OP
    }
}
