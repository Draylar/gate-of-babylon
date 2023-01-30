package draylar.gateofbabylon.entity;

import draylar.gateofbabylon.item.BoomerangItem;
import draylar.gateofbabylon.mixin.AbstractButtonBlockAccessor;
import draylar.gateofbabylon.mixin.BlockSoundGroupAccessor;
import draylar.gateofbabylon.registry.GOBDamageSources;
import draylar.gateofbabylon.registry.GOBEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeverBlock;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class BoomerangEntity extends Entity {

    private static final String OWNER_KEY = "Owner";
    private static final TrackedData<Optional<UUID>> OWNER = DataTracker.registerData(BoomerangEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<ItemStack> STACK = DataTracker.registerData(BoomerangEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private int lastLeverAge = 0;

    // Data for temporary boomerangs (dispensers or other mechanics that shoot a Boomerang which only retracts once)
    private boolean isTemporary = false;
    private boolean hasTemporaryReturned = false;
    private Vec3d temporaryOrigin = Vec3d.ZERO;

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

                    if(owner != null) {
                        Vec3d ownerPos = owner.getPos();
                        ownerPos = ownerPos.multiply(1, 0, 1).add(0, owner.getEyeY() - .2, 0);
                        Vec3d thisPos = getPos();
                        Vec3d difference = ownerPos.subtract(thisPos);
                        setVelocity(difference.normalize());
                    } else {
                        remove(RemovalReason.DISCARDED);
                    }
                } else if (isTemporary) {
                    if(!hasTemporaryReturned) {
                        Vec3d thisPos = getPos();
                        Vec3d difference = temporaryOrigin.subtract(thisPos);
                        setVelocity(difference.normalize());
                        hasTemporaryReturned = true;
                    } else {
                        remove(RemovalReason.DISCARDED);
                    }
                } else {
                    remove(RemovalReason.DISCARDED);
                }
            }

            // delete after 10 seconds to prevent glitche
            if(age > 200) {
                remove(RemovalReason.DISCARDED);
            }
        }

        // collision
        if (!world.isClient) {
            world.getEntitiesByClass(LivingEntity.class, new Box(getX() - .4f, getY() - .05f, getZ() - .4f, getX() + .4f, getY() + .05f, getZ() + .4f), entity -> true).forEach(this::onCollision);

            BlockPos insidePos = getBlockPos();
            BlockPos towardsPos = new BlockPos(getPos().add(getVelocity().normalize()));
            BlockState insideState = world.getBlockState(getBlockPos());
            BlockState towardsState = world.getBlockState(towardsPos);

            // Play collision sounds based on the block the Boomerang is flying into.
            if (!towardsState.isAir() && towardsState.getFluidState().isEmpty()) {
                world.playSound(null, getX(), getY(), getZ(), ((BlockSoundGroupAccessor) towardsState.getSoundGroup()).getHitSound(), SoundCategory.PLAYERS, 0.5f, 1.0f);
            }

            // If the boomerang is inside a button, press it.
            if(insideState.getBlock() instanceof AbstractButtonBlock button) {
                if (!insideState.get(AbstractButtonBlock.POWERED)) {
                    button.powerOn(insideState, world, insidePos);
                    world.playSound(null, insidePos, ((AbstractButtonBlockAccessor) button).callGetClickSound(true), SoundCategory.BLOCKS, 0.3F, 0.6F);
                    world.emitGameEvent(this, GameEvent.BLOCK_ACTIVATE, insidePos);
                }
            }

            // Flip levers!
            int timeSinceLastLever = age - lastLeverAge;
            if((lastLeverAge == 0 || timeSinceLastLever >= 20) && insideState.getBlock() instanceof LeverBlock lever) {
                lever.togglePower(insideState, world, insidePos);
                float f = insideState.get(LeverBlock.POWERED) ? 0.6F : 0.5F;
                world.playSound(null, insidePos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, f);
                world.emitGameEvent(this, insideState.get(LeverBlock.POWERED) ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, insidePos);
                lastLeverAge = age;
            }

            // If the boomerang is inside a replaceable block (such as grass), break it.
            if (insideState.getMaterial().isReplaceable() && !insideState.isAir() && insideState.getFluidState().isEmpty()) {
                world.breakBlock(insidePos, true);
            }
        }
    }

    public void onCollision(LivingEntity entity) {
        ItemStack stack = getStack();

        if(getOwner().isPresent() && entity.getUuid().equals(getOwner().get()) && age > 3) {
            world.playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.25f, 1.0f);
            remove(RemovalReason.DISCARDED);
            return;
        } else if (getOwner().isPresent() && entity.getUuid().equals(getOwner().get())) {
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
            boolean dmg;
            if(getOwner().isPresent() && world.getPlayerByUuid(getOwner().get()) != null) {
                dmg = entity.damage(GOBDamageSources.createBoomerangSource(world.getPlayerByUuid(getOwner().get())), attackDamage);

                // damage boomerang stack
                stack.damage(1, random, (ServerPlayerEntity) world.getPlayerByUuid(getOwner().get()));
            } else {
                dmg = entity.damage(DamageSource.GENERIC, attackDamage);
            }

            // Apply fire aspect
            int level = EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, getStack());
            if (level > 0) {
                entity.setOnFireFor(4 * level);
            }

            // do not interact when hitting the source player
            if(getOwner().isEmpty() || !entity.getUuid().equals(getOwner().get())) {
                int piercing = EnchantmentHelper.getLevel(Enchantments.PIERCING, getStack());

                // knock back
                if(piercing == 0 && dmg) {
                    entity.setVelocity(getVelocity());
                }

                // if we hit an entity and the boomerang does not have piercing, return back
                if (piercing == 0) {
                    PlayerEntity owner = getOwner().isEmpty() ? null : world.getPlayerByUuid(getOwner().get());

                    if(owner != null) {
                        Vec3d ownerPos = owner.getPos();
                        ownerPos = ownerPos.multiply(1, 0, 1).add(0, owner.getEyeY() - .2, 0);
                        Vec3d thisPos = getPos();
                        Vec3d difference = ownerPos.subtract(thisPos);
                        setVelocity(difference.normalize());
                    } else {
                        remove(RemovalReason.DISCARDED);
                    }
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
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
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

    public void setTemporary() {
        isTemporary = true;
        temporaryOrigin = getPos();
    }
}
