package draylar.gateofbabylon.entity;

import draylar.gateofbabylon.registry.GOBEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SpearProjectileEntity extends PersistentProjectileEntity {

    private static final TrackedData<Byte> LOYALTY = DataTracker.registerData(SpearProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Boolean> ENCHANTED = DataTracker.registerData(SpearProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<ItemStack> STACK = DataTracker.registerData(SpearProjectileEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    private ItemStack stack = ItemStack.EMPTY;
    private boolean dealtDamage;
    public int returnTimer;

    public SpearProjectileEntity(EntityType<? extends SpearProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.stack = new ItemStack(Items.TRIDENT);
    }

    public SpearProjectileEntity(World world, LivingEntity owner, ItemStack stack) {
        super(GOBEntities.SPEAR, owner, world);
        this.stack = stack.copy();

        this.dataTracker.set(LOYALTY, (byte) EnchantmentHelper.getLoyalty(stack));
        this.dataTracker.set(ENCHANTED, stack.hasGlint());
        this.dataTracker.set(STACK, stack);
    }

    @Environment(EnvType.CLIENT)
    public SpearProjectileEntity(World world, double x, double y, double z) {
        super(GOBEntities.SPEAR, x, y, z, world);
        this.updatePosition(x, y, z);
        this.updateTrackedPosition(x, y, z);
    }

    @Override
    public void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(LOYALTY, (byte)0);
        this.dataTracker.startTracking(ENCHANTED, false);
        this.dataTracker.startTracking(STACK, ItemStack.EMPTY);
    }

    @Override
    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        Entity entity = this.getOwner();
        if ((this.dealtDamage || this.isNoClip()) && entity != null) {
            int i = (Byte)this.dataTracker.get(LOYALTY);
            if (i > 0 && !this.isOwnerAlive()) {
                if (!this.world.isClient && this.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
                    this.dropStack(this.asItemStack(), 0.1F);
                }

                this.remove(RemovalReason.DISCARDED);
            } else if (i > 0) {
                this.setNoClip(true);
                Vec3d vec3d = new Vec3d(entity.getX() - this.getX(), entity.getEyeY() - this.getY(), entity.getZ() - this.getZ());
                this.setPos(this.getX(), this.getY() + vec3d.y * 0.015D * (double)i, this.getZ());
                if (this.world.isClient) {
                    this.lastRenderY = this.getY();
                }

                double d = 0.05D * (double)i;
                this.setVelocity(this.getVelocity().multiply(0.95D).add(vec3d.normalize().multiply(d)));
                if (this.returnTimer == 0) {
                    this.playSound(SoundEvents.ITEM_TRIDENT_RETURN, 10.0F, 1.0F);
                }

                ++this.returnTimer;
            }
        }

        super.tick();
    }

    private boolean isOwnerAlive() {
        Entity entity = this.getOwner();
        if (entity != null && entity.isAlive()) {
            return !(entity instanceof ServerPlayerEntity) || !entity.isSpectator();
        } else {
            return false;
        }
    }

    @Override
    public ItemStack asItemStack() {
        return this.stack.copy();
    }

    public ItemStack getStack() {
        return world.isClient ? dataTracker.get(STACK) : stack;
    }

    @Environment(EnvType.CLIENT)
    public boolean isEnchanted() {
        return (Boolean)this.dataTracker.get(ENCHANTED);
    }

    @Override
    public EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return this.dealtDamage ? null : super.getEntityCollision(currentPosition, nextPosition);
    }

    @Override
    public void onEntityHit(EntityHitResult entityHitResult) {
        Entity target = entityHitResult.getEntity();
        float damage = 8.0F;

        // Calculate damage bonuses for enchantments (Sharpness, Bane, Smite, etc.)
        if (target instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)target;
            damage += EnchantmentHelper.getAttackDamage(this.stack, livingEntity.getGroup());
        }

        Entity spearOwner = this.getOwner();
        DamageSource damageSource = DamageSource.trident(this, (spearOwner == null ? this : spearOwner));
        this.dealtDamage = true;
        SoundEvent hitSound = SoundEvents.ITEM_TRIDENT_HIT;
        if (target.damage(damageSource, damage)) {
            if (target.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (target instanceof LivingEntity) {
                LivingEntity livingTarget = (LivingEntity)target;
                if (spearOwner instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged(livingTarget, spearOwner);
                    EnchantmentHelper.onTargetDamaged((LivingEntity)spearOwner, livingTarget);
                }

                this.onHit(livingTarget);
            }
        }

        // apply fire aspect to targets if valid
        int fireAspectLevel = EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, stack);
        if(fireAspectLevel > 0) {
            target.setOnFireFor(fireAspectLevel * 4);
        }


        this.setVelocity(this.getVelocity().multiply(-0.01D, -0.1D, -0.01D));
        float volume = 1.0F;

        // Handle channeling
        if (this.world instanceof ServerWorld && this.world.isThundering() && EnchantmentHelper.hasChanneling(this.stack)) {
            BlockPos blockPos = target.getBlockPos();
            if (this.world.isSkyVisible(blockPos)) {
                LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(this.world);
                lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
                lightningEntity.setChanneler(spearOwner instanceof ServerPlayerEntity ? (ServerPlayerEntity)spearOwner : null);
                this.world.spawnEntity(lightningEntity);
                hitSound = SoundEvents.ITEM_TRIDENT_THUNDER;
                volume = 5.0F;
            }
        }

        this.playSound(hitSound, volume, 1.0F);
    }

    @Override
    public SoundEvent getHitSound() {
        return SoundEvents.ITEM_TRIDENT_HIT_GROUND;
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        Entity entity = this.getOwner();
        if (entity == null || entity.getUuid() == player.getUuid()) {
            super.onPlayerCollision(player);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        if (tag.contains("Stack", 10)) {
            this.stack = ItemStack.fromNbt(tag.getCompound("Stack"));
        }

        this.dealtDamage = tag.getBoolean("DealtDamage");
        this.dataTracker.set(LOYALTY, (byte)EnchantmentHelper.getLoyalty(this.stack));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        tag.put("Stack", this.stack.writeNbt(new NbtCompound()));
        tag.putBoolean("DealtDamage", this.dealtDamage);
    }

    @Override
    public void age() {
        int i = (Byte)this.dataTracker.get(LOYALTY);
        if (this.pickupType != PersistentProjectileEntity.PickupPermission.ALLOWED || i <= 0) {
            super.age();
        }

    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public float getDragInWater() {
        return 0.99F;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }
}
