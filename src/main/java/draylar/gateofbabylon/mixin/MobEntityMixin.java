package draylar.gateofbabylon.mixin;

import draylar.gateofbabylon.item.CustomShieldItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {

    private MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    // Replicate Vanilla shield-disabling behavior for custom shield items (vanilla directly checks against shield item instance)
    @Inject(
            method = "disablePlayerShield",
            at = @At("HEAD")
    )
    private void disableCustomShield(PlayerEntity player, ItemStack mobStack, ItemStack playerStack, CallbackInfo ci) {
        if(!mobStack.isEmpty() && !playerStack.isEmpty() && mobStack.getItem() instanceof AxeItem && playerStack.getItem() instanceof CustomShieldItem) {
            float efficiency = 0.25F + (float) EnchantmentHelper.getEfficiency((MobEntity) (Object) this) * 0.05F;

            if (this.random.nextFloat() < efficiency) {
                player.getItemCooldownManager().set(Items.SHIELD, 100);
                this.world.sendEntityStatus(player, (byte) 30);
            }
        }
    }
}
