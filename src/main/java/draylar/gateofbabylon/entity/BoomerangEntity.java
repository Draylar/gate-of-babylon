package draylar.gateofbabylon.entity;

import draylar.gateofbabylon.GateOfBabylon;
import draylar.gateofbabylon.item.BoomerangItem;
import draylar.gateofbabylon.item.YoyoItem;
import draylar.gateofbabylon.registry.GOBEntities;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class BoomerangEntity extends Entity {

    public static final Identifier SPAWN_PACKET_ID = GateOfBabylon.id("boomerang_spawn_packet");
    private static final String OWNER_KEY = "Owner";
    private static final TrackedData<Optional<UUID>> OWNER = DataTracker.registerData(YoyoEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<ItemStack> STACK = DataTracker.registerData(YoyoEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    // TODO: MAX PIERCING ENTITIES?

    public BoomerangEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Environment(EnvType.CLIENT)
    public BoomerangEntity(World world, double x, double y, double z) {
        super(GOBEntities.BOOMERANG, world);
        this.updatePosition(x, y, z);
        this.updateTrackedPosition(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();

        // :)
        if (!world.isClient) {
            if(age % 5 == 0) {
                world.playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, SoundCategory.PLAYERS, 0.5f, .5f);
            }

            velocityDirty = true;
            velocityModified = true;

            move(MovementType.SELF, getVelocity());

            // When the boomerang approaches the return time (1 second, 20 ticks), it will slow down.
            if(age % 20 >= 15) {
                int t = 20 - (age % 20);
                double modifier =  t / 5f;
                setVelocity(getVelocity().multiply(modifier));
            }

            // Every second, the boomerang will redirect back towards the player.
            if(age % 20 == 0) {
                // turn towards user every tick
                if(getOwner().isPresent()) {
                    PlayerEntity owner = world.getPlayerByUuid(getOwner().get());
                    Vec3d ownerPos = owner.getPos();
                    ownerPos = ownerPos.multiply(1, 0, 1).add(0, owner.getEyeY() - .2, 0);
                    Vec3d thisPos = getPos();
                    Vec3d difference = ownerPos.subtract(thisPos);
                    setVelocity(difference.normalize());
                } else {
                    remove();
                }
            }

            // delete after 10 seconds to prevent glitche
            if(age > 200) {
                remove();
            }
        }

        // collision
        if (!world.isClient) {
            world.getEntitiesByClass(LivingEntity.class, new Box(getX() - .4f, getY() - .05f, getZ() - .4f, getX() + .4f, getY() + .05f, getZ() + .4f), entity -> true).forEach(this::onCollision);

            // calculate distance between player and yoyo
            if (getOwner().isPresent()) {
                PlayerEntity owner = world.getPlayerByUuid(getOwner().get());

                if (owner != null) {
                    Vec3d rotationVector = owner.getRotationVector();
                    Vec3d yoyoPosition = getPos();
                    Vec3d target = yoyoPosition.add(rotationVector);

                    BlockPos p = new BlockPos(target);
                    BlockState blockState = world.getBlockState(p);
                    if (!blockState.isAir()) {
                        world.playSound(null, getX(), getY(), getZ(), blockState.getSoundGroup().getHitSound(), SoundCategory.PLAYERS, 0.5f, 1.0f);
                    }

                    if (blockState.getMaterial().isReplaceable()) {
                        world.breakBlock(p, true);
                    }
                }
            }
        }
    }

    public void onCollision(LivingEntity entity) {
        ItemStack stack = getStack();

        if(getOwner().isPresent() && entity.getUuid().equals(getOwner().get()) && age > 10) {
            world.playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.25f, 1.0f);
            remove();
            return;
        }

        if(stack.getItem() instanceof BoomerangItem) {
            world.playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, SoundCategory.PLAYERS, 0.5f, 1.0f);
            float baseDamage = ((BoomerangItem) stack.getItem()).getMaterial().getAttackDamage();

            // Check if the player is valid for attack damage calculations.
            if(getOwner().isPresent()) {
                PlayerEntity player = world.getPlayerByUuid(getOwner().get());

                // If the player is valid, overwrite the material damage with our generic attack damage attribute.
                if(player != null) {
                    baseDamage = (float) player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                }
            }

            // calculate final damage with enchantments and attack entity
            float attackDamage = baseDamage + EnchantmentHelper.getAttackDamage(stack, entity.getGroup());
            boolean dmg = entity.damage(DamageSource.GENERIC, attackDamage);

            // Apply fire aspect
            int level = EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, getStack());
            if (level > 0) {
                entity.setOnFireFor(4 * level);
            }

            // do not interact when hitting the source player
            if(!entity.getUuid().equals(getOwner().get())) {
                int piercing = EnchantmentHelper.getLevel(Enchantments.PIERCING, getStack());

                // knock back
                if(piercing == 0 && dmg) {
                    entity.setVelocity(getVelocity());
                }

                // if we hit an entity and the boomerang does not have piercing, return back
                if (piercing == 0) {
                    PlayerEntity owner = world.getPlayerByUuid(getOwner().get());
                    Vec3d ownerPos = owner.getPos();
                    ownerPos = ownerPos.multiply(1, 0, 1).add(0, owner.getEyeY() - .2, 0);
                    Vec3d thisPos = getPos();
                    Vec3d difference = ownerPos.subtract(thisPos);
                    setVelocity(difference.normalize());
                }
            }
        }
    }

    @Override
    public void initDataTracker() {
        dataTracker.startTracking(OWNER, Optional.empty());
        dataTracker.startTracking(STACK, ItemStack.EMPTY);
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {

    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {

    }

    @Override
    public Packet<?> createSpawnPacket() {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeDouble(getX());
        packet.writeDouble(getY());
        packet.writeDouble(getZ());
        packet.writeInt(getEntityId());
        return ServerPlayNetworking.createS2CPacket(SPAWN_PACKET_ID, packet);
    }

    public void setStack(ItemStack stack) {
        dataTracker.set(STACK, stack);
    }

    public ItemStack getStack() {
        return dataTracker.get(STACK);
    }

    public void setOwner(@NotNull PlayerEntity player) {
        dataTracker.set(OWNER, Optional.of(player.getUuid()));
    }

    @NotNull
    public Optional<UUID> getOwner() {
        return dataTracker.get(OWNER);
    }

    public Optional<PlayerEntity> getPlayerOwner() {
        // can we condense this
        return getOwner().isPresent() && world.getPlayerByUuid(getOwner().get()) != null ? Optional.ofNullable(world.getPlayerByUuid(getOwner().get())) : Optional.empty();
    }
}
