package draylar.gateofbabylon.registry;

import draylar.gateofbabylon.GateOfBabylon;
import draylar.gateofbabylon.entity.SpearProjectileEntity;
import draylar.gateofbabylon.entity.YoyoEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;

public class GOBEntities {

    public static final EntityType<SpearProjectileEntity> SPEAR = register(
            "spear",
            FabricEntityTypeBuilder
                    .<SpearProjectileEntity>create(SpawnGroup.MISC, (SpearProjectileEntity::new))
                    .trackable(128, 4)
                    .dimensions(EntityDimensions.fixed(.5f, .5f)).build());

    public static final EntityType<YoyoEntity> YOYO = register(
            "yoyo",
            FabricEntityTypeBuilder
                .<YoyoEntity>create(SpawnGroup.MISC, YoyoEntity::new)
                    .trackRangeBlocks(128)
                    .trackedUpdateRate(16)
                .dimensions(EntityDimensions.fixed(.25f, .25f)).build());

    private static <T extends Entity> EntityType<T> register(String name, EntityType<T> entity) {
        return Registry.register(Registry.ENTITY_TYPE, GateOfBabylon.id(name), entity);
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> entity) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, GateOfBabylon.id(name), entity);
    }

    public static void init() {
        // NO-OP
    }

    private GOBEntities() {
        // NO-OP
    }
}
