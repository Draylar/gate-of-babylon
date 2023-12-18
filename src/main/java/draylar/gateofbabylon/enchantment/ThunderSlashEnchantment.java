package draylar.gateofbabylon.enchantment;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class ThunderSlashEnchantment extends KatanaSlashEnchantment {
    
    public ThunderSlashEnchantment() {
        super(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, ParticleTypes.CLOUD, (target, source, stack) -> {
            if (target.getWorld() instanceof ServerWorld) {
                BlockPos blockPos = target.getBlockPos();
                if (target.getWorld().isSkyVisible(blockPos)) {
                    @Nullable LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(target.getWorld());
                    if(lightning != null) {
                        lightning.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
                        lightning.setChanneler(source instanceof ServerPlayerEntity ? (ServerPlayerEntity) source : null);
                        target.getWorld().spawnEntity(lightning);
                    }
                }
            }
        });
    }
}
