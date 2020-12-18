package draylar.gateofbabylon.enchantment;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ThunderSlashEnchantment extends KatanaSlashEnchantment {
    
    public ThunderSlashEnchantment() {
        super(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, ParticleTypes.CLOUD, (target, source, stack) -> {
            if (target.world instanceof ServerWorld) {
                BlockPos blockPos = target.getBlockPos();
                if (target.world.isSkyVisible(blockPos)) {
                    LightningEntity lightningEntity = (LightningEntity)EntityType.LIGHTNING_BOLT.create(target.world);
                    lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
                    lightningEntity.setChanneler(source instanceof ServerPlayerEntity ? (ServerPlayerEntity)source : null);
                    target.world.spawnEntity(lightningEntity);
                }
            }
        });
    }
}
