package draylar.gateofbabylon.item;

import draylar.gateofbabylon.api.EnchantmentHandler;
import draylar.gateofbabylon.api.ProjectileManipulator;
import draylar.gateofbabylon.registry.GOBEnchantments;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomBowItem extends BowItem implements EnchantmentHandler {

    private final ToolMaterial material;
    private final float maxDrawTime;
    private final ParticleEffect type;
    private final double damageModifier;

    public CustomBowItem(ToolMaterial material, Settings settings, float maxDrawTime, double damageModifier) {
        super(settings);
        this.material = material;
        this.maxDrawTime = maxDrawTime;
        this.damageModifier = damageModifier;
        type = null;
    }

    public CustomBowItem(ToolMaterial material, Settings settings, float maxDrawTime, double damageModifier, ParticleEffect particles) {
        super(settings);
        this.material = material;
        this.maxDrawTime = maxDrawTime;
        this.damageModifier = damageModifier;
        type = particles;
    }

    public float getMaxDrawTime(ItemStack bow) {
        int quickDrawLevel = EnchantmentHelper.getLevel(GOBEnchantments.QUICKDRAW, bow);
        return (float) Math.max(0, maxDrawTime - quickDrawLevel * 3.3);
    }

    public ParticleEffect getArrowParticles() {
        return type;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)user;
            boolean skipArrowCheck = playerEntity.getAbilities().creativeMode || EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0;
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
                        arrowEntity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, pullProgress * 3.0F, 1.0F);
                        ((ProjectileManipulator) arrowEntity).setOrigin(stack);

                        // Make Arrow crit if pull progress is fully complete
                        if (pullProgress == 1.0F) {
                            arrowEntity.setCritical(true);
                        }

                        // Apply damage from power enchantment
                        int j = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
                        if (j > 0) {
                            arrowEntity.setDamage(arrowEntity.getDamage() + (double) j * 0.5D + 0.5D);
                        }

                        // apply damage multiplier
                        arrowEntity.setDamage(arrowEntity.getDamage() * damageModifier);

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
                        if (bl2 || playerEntity.getAbilities().creativeMode && (arrowStack.getItem() == Items.SPECTRAL_ARROW || arrowStack.getItem() == Items.TIPPED_ARROW)) {
                            arrowEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                        }

                        world.spawnEntity(arrowEntity);
                    }

                    world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.random.nextFloat() * 0.4F + 1.2F) + pullProgress * 0.5F);

                    // decrement source arrow stack
                    if (!bl2 && !playerEntity.getAbilities().creativeMode) {
                        arrowStack.decrement(1);
                        if (arrowStack.isEmpty()) {
                            playerEntity.getInventory().removeOne(arrowStack);
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

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(LiteralText.EMPTY);
        tooltip.add(new TranslatableText("gateofbabylon.bow_stats").formatted(Formatting.GRAY));
        tooltip.add(new LiteralText(" ").append(new TranslatableText("gateofbabylon.bow_damage", damageModifier).formatted(Formatting.DARK_GREEN)));
        tooltip.add(new LiteralText(" ").append(new TranslatableText("gateofbabylon.bow_draw_speed", (double) maxDrawTime / 20).formatted(Formatting.DARK_GREEN)));
    }
}
