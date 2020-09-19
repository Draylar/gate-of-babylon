package draylar.gateofbabylon.item;

import draylar.gateofbabylon.registry.GOBEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

public class CustomBowItem extends BowItem {

    private final ToolMaterial material;
    private final float maxDrawTime;

    public CustomBowItem(ToolMaterial material, Settings settings, float maxDrawTime) {
        super(settings);
        this.material = material;
        this.maxDrawTime = maxDrawTime;
    }

    public float getMaxDrawTime(ItemStack bow) {
        int quickDrawLevel = EnchantmentHelper.getLevel(GOBEnchantments.QUICKDRAW, bow);
        return (float) Math.max(0, maxDrawTime - quickDrawLevel * 3.3);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)user;
            boolean skipArrowCheck = playerEntity.abilities.creativeMode || EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack arrowStack = playerEntity.getArrowType(stack);

            if (!arrowStack.isEmpty() || skipArrowCheck) {
                if (arrowStack.isEmpty()) {
                    arrowStack = new ItemStack(Items.ARROW);
                }

                int currentUseTime = this.getMaxUseTime(stack) - remainingUseTicks;
                float pullProgress = getPullProgress(stack, this, currentUseTime);

                if ((double) pullProgress >= 0.1D) {
                    boolean bl2 = skipArrowCheck && arrowStack.getItem() == Items.ARROW;

                    if (!world.isClient) {
                        ArrowItem arrowItem = (ArrowItem) (arrowStack.getItem() instanceof ArrowItem ? arrowStack.getItem() : Items.ARROW);
                        PersistentProjectileEntity arrowEntity = arrowItem.createArrow(world, arrowStack, playerEntity);
                        arrowEntity.setProperties(playerEntity, playerEntity.pitch, playerEntity.yaw, 0.0F, pullProgress * 3.0F, 1.0F);

                        // Make Arrow crit if pull progress is fully complete
                        if (pullProgress == 1.0F) {
                            arrowEntity.setCritical(true);
                        }

                        // Apply damage from power enchantment
                        int j = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
                        if (j > 0) {
                            arrowEntity.setDamage(arrowEntity.getDamage() + (double) j * 0.5D + 0.5D);
                        }

                        // Apply punch knockback
                        int k = EnchantmentHelper.getLevel(Enchantments.PUNCH, stack);
                        if (k > 0) {
                            arrowEntity.setPunch(k);
                        }

                        // Apply flame
                        if (EnchantmentHelper.getLevel(Enchantments.FLAME, stack) > 0) {
                            arrowEntity.setOnFireFor(100);
                        }

                        // Damage tool
                        stack.damage(1, playerEntity, (p) -> p.sendToolBreakStatus(playerEntity.getActiveHand()));

                        // Set arrow pickup type based on source
                        if (bl2 || playerEntity.abilities.creativeMode && (arrowStack.getItem() == Items.SPECTRAL_ARROW || arrowStack.getItem() == Items.TIPPED_ARROW)) {
                            arrowEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                        }

                        world.spawnEntity(arrowEntity);
                    }

                    world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (RANDOM.nextFloat() * 0.4F + 1.2F) + pullProgress * 0.5F);

                    // decrement source arrow stack
                    if (!bl2 && !playerEntity.abilities.creativeMode) {
                        arrowStack.decrement(1);
                        if (arrowStack.isEmpty()) {
                            playerEntity.inventory.removeOne(arrowStack);
                        }
                    }

                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                }
            }
        }
    }

    public static float getPullProgress(ItemStack stack, CustomBowItem bow, int useTicks) {
        float progress = (float) useTicks / bow.getMaxDrawTime(stack);
        progress = (progress * progress + progress * 2.0F) / 3.0F;

        if (progress > 1.0F) {
            progress = 1.0F;
        }

        return progress;
    }

    @Override
    public int getEnchantability() {
        return material.getEnchantability();
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return this.material.getRepairIngredient().test(ingredient) || super.canRepair(stack, ingredient);
    }
}
