package draylar.gateofbabylon.entity;

import draylar.gateofbabylon.item.YoyoItem;
import draylar.gateofbabylon.mixin.BlockSoundGroupAccessor;
import draylar.gateofbabylon.registry.GOBEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class YoyoEntity extends Entity {

    private static final TrackedData<Optional<UUID>> OWNER = DataTracker.registerData(YoyoEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<ItemStack> STACK = DataTracker.registerData(YoyoEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    public YoyoEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Environment(EnvType.CLIENT)
    public YoyoEntity(World world, double x, double y, double z) {
        super(GOBEntities.YOYO, world);
        this.updatePosition(x, y, z);
        this.updateTrackedPosition(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();

        lastRenderX = prevX;
        lastRenderY = prevY;
        lastRenderZ = prevZ;

        // :)
        if(!getWorld().isClient) {
            if(dataTracker.get(OWNER).isPresent()) {
                PlayerEntity owner = getWorld().getPlayerByUuid(dataTracker.get(OWNER).get());

                if (owner != null) {
                    HitResult ray = owner.raycast(5, 0, false);
                    Vec3d targetPos = ray.getPos();
                    Vec3d thisPos = getPos();

                    double distance = targetPos.distanceTo(thisPos);
                    Vec3d difference = targetPos.subtract(thisPos).normalize().multiply(Math.min(distance, 1));

                    setVelocity(difference);
                    velocityDirty = true;
                    velocityModified = true;
                }
            }

            move(MovementType.SELF, getVelocity());
        }

        // collision
        if(!getWorld().isClient) {
            getWorld().getEntitiesByClass(LivingEntity.class, new Box(getX() - .25f, getY() - .25f, getZ() - .25f, getX() + .25f, getY() + .25f, getZ() + .25f), entity -> true).forEach(this::onCollision);

            // calculate distance between player and yoyo
            if(getOwner().isPresent()) {
                PlayerEntity owner = getWorld().getPlayerByUuid(getOwner().get());

                if(owner != null) {
                    Vec3d rotationVector = owner.getRotationVector();
                    Vec3d yoyoPosition = getPos();
                    Vec3d target = yoyoPosition.add(rotationVector);

                    BlockPos p = BlockPos.ofFloored(target);
                    BlockState blockState = getWorld().getBlockState(p);
                    if(!blockState.isAir()) {
                        getWorld().playSound(null, getX(), getY(), getZ(), ((BlockSoundGroupAccessor) blockState.getSoundGroup()).getHitSound(), SoundCategory.PLAYERS, 0.5f, 1.0f);
                    }

                    if(blockState.isReplaceable()) {
                        getWorld().breakBlock(p, true);
                    }
                }
            }
        }

        lastRenderX = getX();
        lastRenderY = getY();
        lastRenderZ = getZ();
    }

    @Override
    public void initDataTracker() {
        dataTracker.startTracking(OWNER, Optional.empty());
        dataTracker.startTracking(STACK, ItemStack.EMPTY);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    public void setOwner(@NotNull PlayerEntity player) {
        dataTracker.set(OWNER, Optional.of(player.getUuid()));
    }

    @NotNull
    public Optional<UUID> getOwner() {
        return dataTracker.get(OWNER);
    }

    public void onCollision(LivingEntity entity) {
        ItemStack stack = getStack();

        // do not collide with other
        if(getOwner().isPresent() && entity.getUuid().equals(getOwner().get())) {
            return;
        }

        if(stack.getItem() instanceof YoyoItem) {
            getWorld().playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, SoundCategory.PLAYERS, 0.5f, 1.0f);
            float attackDamage = ((YoyoItem) stack.getItem()).getMaterial().getAttackDamage() + EnchantmentHelper.getAttackDamage(stack, entity.getGroup());

            if(getOwner().isPresent() && getWorld().getPlayerByUuid(getOwner().get()) != null) {
                entity.damage(getDamageSources().playerAttack(getWorld().getPlayerByUuid(getOwner().get())), attackDamage);

                // damage yoyo
                stack.damage(1, random, (ServerPlayerEntity) getWorld().getPlayerByUuid(getOwner().get()));
            } else {
                entity.damage(getDamageSources().generic(), attackDamage);
            }

            // Apply fire aspect
            int level = EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, getStack());
            if (level > 0) {
                entity.setOnFireFor(4 * level);
            }

            // knock back
            entity.setVelocity(getVelocity());
        }
    }

    public void setStack(ItemStack stack) {
        dataTracker.set(STACK, stack);
    }

    public ItemStack getStack() {
        return dataTracker.get(STACK);
    }

    public void retract() {

    }

    public void deploy() {

    }
}
