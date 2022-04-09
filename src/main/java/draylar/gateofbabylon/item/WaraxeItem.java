package draylar.gateofbabylon.item;

import draylar.gateofbabylon.GateOfBabylon;
import draylar.gateofbabylon.api.EnchantmentHandler;
import draylar.gateofbabylon.mixin.FallingBlockEntityAccessor;
import draylar.gateofbabylon.registry.GOBEnchantments;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WaraxeItem extends AxeItem implements EnchantmentHandler {

    public WaraxeItem(ToolMaterial material, float effectiveDamage, float effectiveSpeed, Item.Settings settings) {
        super(material, (int) (effectiveDamage - material.getAttackDamage() - 1), -4 + effectiveSpeed, settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 30;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        boolean hasSmashing = EnchantmentHelper.getLevel(GOBEnchantments.SMASHING, stack) > 0;
        int radius = hasSmashing ? 5 : 3;

        if (!world.isClient && user instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) user;

            // spawn effects
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));

                    if (distance <= radius && distance >= radius / 2f) {
                        Vec3d newPos = user.getPos().add(x, -2, z);
                        int level = 0;

                        while(!world.getBlockState(new BlockPos(newPos).up()).isAir() && level < 5) {
                            newPos = newPos.add(0, 1, 0);
                            level++;
                        }

                        if(world.getBlockState(new BlockPos(newPos).up()).isAir()) {
                            spawnEntity((ServerWorld) world, newPos.add(0, 1, 0), user, world.getBlockState(new BlockPos(newPos)));
                        }
                    }
                }
            }

            // knock back nearby entities
            world.getEntitiesByClass(LivingEntity.class, new Box(user.getBlockPos().add(-radius - 2, -1, -radius - 2), user.getBlockPos().add(radius + 2, 3, radius + 2)), entity -> entity != user).forEach(entity -> {
                // Triggers for entities that aren't tameable, or that aren't tamed, or that aren't owned by the owner of the breath
                if (!(entity instanceof TameableEntity) || !((TameableEntity) entity).isTamed() || !((TameableEntity) entity).getOwnerUuid().equals(player.getUuid())) {
                    entity.damage(DamageSource.player(player), hasSmashing ? getAttackDamage() * 1.5f : getAttackDamage());
                    entity.setVelocity(entity.getPos().subtract(player.getPos()).multiply(hasSmashing ? .6 : .5).add(0, .35, 0));
                }
            });

            player.getItemCooldownManager().set(this, 20 * 5); // 20 * 5
        }

        return super.finishUsing(stack, world, user);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    public void spawnEntity(ServerWorld world, Vec3d pos, LivingEntity source, BlockState state) {
        FallingBlockEntity spawn = FallingBlockEntityAccessor.createFallingBlockEntity(world, pos.getX(), pos.getY(), pos.getZ(), state);

        // setup velocity
        Vec3d difference = pos.subtract(source.getPos()).multiply(.1);
        spawn.addVelocity(0, .35, 0);
        spawn.addVelocity(source.getVelocity().x, source.getVelocity().y, source.getVelocity().z);
        spawn.addVelocity(difference.x, difference.y, difference.z);

        // spawn particles
        world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, state), pos.getX(), pos.getY(), pos.getZ(), 3, 0, 0, 0, .1);
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), state.getSoundGroup().getPlaceSound(), SoundCategory.PLAYERS, .25f, .5f + world.random.nextInt() * .25f);

        // setup properties
        spawn.dropItem = false;
        ((FallingBlockEntityAccessor) spawn).setDestroyedOnLanding(true);
        spawn.timeFalling = 5;

        // spawn
        world.spawnEntity(spawn);
    }
}
