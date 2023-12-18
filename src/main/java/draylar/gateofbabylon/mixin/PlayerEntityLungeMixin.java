package draylar.gateofbabylon.mixin;

import draylar.gateofbabylon.api.LungeManipulator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityLungeMixin extends LivingEntity implements LungeManipulator {

    @Unique private boolean gob$hasLunged = false;

    protected PlayerEntityLungeMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void gateOfBabylon$onPlayerTick(CallbackInfo ci) {
        if (gob$hasLunged && isOnGround()) {
            gob$hasLunged = false;
        }
    }

    @Unique
    @Override
    public void gateOfBabylon$setLunged() {
        gob$hasLunged = true;
    }

    @Unique
    @Override
    public boolean gateOfBabylon$canLunge() {
        return !gob$hasLunged;
    }
}
