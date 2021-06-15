package draylar.gateofbabylon.item;

import draylar.gateofbabylon.entity.YoyoEntity;
import draylar.gateofbabylon.registry.GOBEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class YoyoItem extends ToolItem {

    private final ToolMaterial material;

    public YoyoItem(Settings settings, ToolMaterial material) {
        super(material, settings);
        this.material = material;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!world.isClient) {
            // If the user already has a yoyo out, we want to retract it.
            // Otherwise, we spawn a new yoyo and send it flying out / away from the player.
            List<YoyoEntity> found = new ArrayList<>(world.getEntitiesByClass(
                    YoyoEntity.class,
                    new Box(user.getBlockPos().add(-25, -25, -25), user.getBlockPos().add(25, 25, 25)),
                    yoyo -> yoyo.isAlive() && yoyo.getOwner().isPresent() && yoyo.getOwner().get().equals(user.getUuid())));

            // Yoyo was found, remove it and stop early.
            if(!found.isEmpty()) {
                found.forEach(yoyo -> {
                    yoyo.retract();

                    // TOOD: retract instead of removing instantly?
                    yoyo.remove(Entity.RemovalReason.DISCARDED);
                    yoyo.kill();
                });

                return TypedActionResult.success(user.getStackInHand(hand));
            }

            // Yoyo was not found, spawn a new one now.
            YoyoEntity yoyo = new YoyoEntity(GOBEntities.YOYO, world);
            yoyo.setPos(user.getX(), user.getY(), user.getZ());
            yoyo.updateTrackedPosition(user.getX(), user.getY(), user.getZ());
            yoyo.requestTeleport(user.getX(), user.getY(), user.getZ());
            yoyo.setOwner(user);
            yoyo.setStack(user.getStackInHand(hand));
            world.spawnEntity(yoyo);
            yoyo.deploy();
        }

        user.setCurrentHand(hand);
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        super.onStoppedUsing(stack, world, user, remainingUseTicks);

        // Stop using yoyo
        List<YoyoEntity> found = new ArrayList<>(world.getEntitiesByClass(
                YoyoEntity.class,
                new Box(user.getBlockPos().add(-25, -25, -25), user.getBlockPos().add(25, 25, 25)),
                yoyo -> yoyo.isAlive() && yoyo.getOwner().isPresent() && yoyo.getOwner().get().equals(user.getUuid())));

        // Yoyo was found, remove it and stop early.
        if(!found.isEmpty()) {
            found.forEach(yoyo -> {
                yoyo.retract();

                // TOOD: retract instead of removing instantly?
                yoyo.remove(Entity.RemovalReason.DISCARDED);
                yoyo.kill();
            });
        }
    }

    public ToolMaterial getMaterial() {
        return material;
    }
}
