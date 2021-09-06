package draylar.gateofbabylon.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import draylar.gateofbabylon.api.EnchantmentHandler;
import draylar.gateofbabylon.entity.BoomerangEntity;
import draylar.gateofbabylon.registry.GOBEntities;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoomerangItem extends ToolItem implements EnchantmentHandler {

    private final ToolMaterial material;
    private final float attackDamage;
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public BoomerangItem(Settings settings, ToolMaterial material) {
        super(material, settings);
        this.material = material;

        this.attackDamage = 3 + material.getAttackDamage();
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", (double)this.attackDamage, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        // If the user already has a boomerang out, do not allow a new one
        List<BoomerangEntity> found = new ArrayList<>(world.getEntitiesByClass(
                BoomerangEntity.class,
                new Box(user.getBlockPos().add(-25, -25, -25), user.getBlockPos().add(25, 25, 25)),
                boomerang -> boomerang.isAlive() && boomerang.getOwner().isPresent() && boomerang.getOwner().get().equals(user.getUuid())));

        // Boomerang was found, remove it and stop early.
        if(!found.isEmpty()) {
            return TypedActionResult.fail(user.getStackInHand(hand));
        }

        if(!world.isClient) {
            BoomerangEntity boomerang = createBoomerang(user.getStackInHand(hand), world);
            boomerang.setYaw(user.getYaw());
            boomerang.setPitch(user.getPitch());
            boomerang.setVelocity(boomerang.getRotationVector());
            double y = user.getEyeY() - .2;
            boomerang.setPos(user.getX(), y, user.getZ());
            boomerang.updateTrackedPosition(user.getX(), y, user.getZ());
            boomerang.requestTeleport(user.getX(), y, user.getZ());
            boomerang.setOwner(user);
            world.spawnEntity(boomerang);
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }

    public BoomerangEntity createBoomerang(ItemStack stack, World world) {
        BoomerangEntity boomerang = new BoomerangEntity(GOBEntities.BOOMERANG, world);
        boomerang.setStack(stack);
        return boomerang;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        float attackDamage = ((BoomerangItem) stack.getItem()).getMaterial().getAttackDamage() + EnchantmentHelper.getAttackDamage(stack, null);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        if(slot.equals(EquipmentSlot.MAINHAND)) {
            return attributeModifiers;
        } else {
            return ImmutableMultimap.of();
        }
    }

    @Override
    public List<EnchantmentTarget> getEnchantmentTypes() {
        return Collections.singletonList(EnchantmentTarget.WEAPON);
    }

    @Override
    public boolean isExplicitlyValid(Enchantment enchantment) {
        return enchantment.equals(Enchantments.PIERCING);
    }
}
