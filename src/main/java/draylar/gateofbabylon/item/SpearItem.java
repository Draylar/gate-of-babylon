package draylar.gateofbabylon.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import draylar.gateofbabylon.GateOfBabylon;
import draylar.gateofbabylon.api.EnchantmentHandler;
import draylar.gateofbabylon.entity.SpearProjectileEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class SpearItem extends ToolItem implements EnchantmentHandler {

    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public SpearItem(ToolMaterial material, float effectiveDamage, float effectiveSpeed) {
        super(material, new Item.Settings().group(GateOfBabylon.GROUP).maxCount(1));

        effectiveDamage = effectiveDamage - 1;
        effectiveSpeed = -4 + effectiveSpeed;

        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", effectiveDamage, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", effectiveSpeed, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (itemStack.getDamage() >= itemStack.getMaxDamage() - 1) {
            return TypedActionResult.fail(itemStack);
        } else {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) user;
            int currentUseTime = this.getMaxUseTime(stack) - remainingUseTicks;

            if (currentUseTime >= 10) {
                if (!world.isClient) {
                    stack.damage(1, player, entity -> entity.sendToolBreakStatus(user.getActiveHand()));

                    // Create initial Spear entity
                    SpearProjectileEntity spearEntity = new SpearProjectileEntity(world, player, stack);
                    spearEntity.setProperties(player, player.pitch, player.yaw, 0.0F, 2.5F, 1.0F);
                    if (player.abilities.creativeMode) {
                        spearEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                    }
                    world.spawnEntity(spearEntity);

                    // Play SFX
                    world.playSoundFromEntity(null, spearEntity, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);

                    // Remove Spear from inventory after throw
                    if (!player.abilities.creativeMode) {
                        player.inventory.removeOne(stack);
                    }
                }

                player.incrementStat(Stats.USED.getOrCreateStat(this));
            }
        }
    }

    @Override
    public List<EnchantmentTarget> getEnchantmentTypes() {
        return Arrays.asList(EnchantmentTarget.WEAPON, EnchantmentTarget.TRIDENT);
    }

    @Override
    public boolean isInvalid(Enchantment enchantment) {
        return enchantment == Enchantments.SWEEPING;
    }
}