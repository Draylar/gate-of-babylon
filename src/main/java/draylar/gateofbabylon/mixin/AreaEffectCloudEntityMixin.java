package draylar.gateofbabylon.mixin;

import org.spongepowered.asm.mixin.Mixin;

import draylar.gateofbabylon.access.AreaEffectCloudEntityAccess;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

@Mixin(AreaEffectCloudEntity.class)
public abstract class AreaEffectCloudEntityMixin extends Entity implements AreaEffectCloudEntityAccess {
    public AreaEffectCloudEntityMixin(EntityType<?> type, World world) {
        super(type, world);
        // TODO Auto-generated constructor stub
    }

    // Mixin used by interface in order to call super.tick() of AreaEffectCloudEntity
    // without calling tick() of AreaEffectCloudEntity
    public void superTick() {
        super.tick();
    }
}
