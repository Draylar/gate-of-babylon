package draylar.gateofbabylon.mixin;

import draylar.gateofbabylon.api.DoubleAttackHelper;
import draylar.gateofbabylon.item.CustomShieldItem;
import draylar.gateofbabylon.item.HaladieItem;
import draylar.gateofbabylon.registry.GOBItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.TimerTask;
import java.util.function.Consumer;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Shadow public abstract void incrementStat(Stat<?> stat);

    @Shadow public abstract ItemCooldownManager getItemCooldownManager();

    @Shadow public abstract void attack(Entity target);

    private PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "damageShield",
            at = @At("HEAD"),
            cancellable = true
    )
    private void damageCustomShield(float amount, CallbackInfo ci) {
        if (this.activeItemStack.getItem() instanceof CustomShieldItem) {

            // Increment 'used' stat for the current shield item on server
            if (!this.world.isClient) {
                this.incrementStat(Stats.USED.getOrCreateStat(this.activeItemStack.getItem()));
            }

            // Only reduce shield durability if the incoming damage is greater than 3
            if (amount >= 3.0F) {
                int trueDamage = 1 + MathHelper.floor(amount);
                Hand activeHand = this.getActiveHand();
                this.activeItemStack.damage(trueDamage, this, playerEntity -> playerEntity.sendToolBreakStatus(activeHand)); // Damage held stack

                // Play FX
                if (this.activeItemStack.isEmpty()) {
                    if (activeHand == Hand.MAIN_HAND) {
                        this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    } else {
                        this.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                    }

                    this.activeItemStack = ItemStack.EMPTY;
                    this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);
                }
            }
        }
    }

    @Inject(
            method = "disableShield",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ItemCooldownManager;set(Lnet/minecraft/item/Item;I)V")
    )
    public void disableShield(boolean sprinting, CallbackInfo ci) {
        this.getItemCooldownManager().set(GOBItems.STONE_SHIELD, 100);
        this.getItemCooldownManager().set(GOBItems.IRON_SHIELD, 100);
        this.getItemCooldownManager().set(GOBItems.GOLDEN_SHIELD, 100);
        this.getItemCooldownManager().set(GOBItems.DIAMOND_SHIELD, 100);
        this.getItemCooldownManager().set(GOBItems.NETHERITE_SHIELD, 100);
    }

    @Unique
    private boolean gob_hasHaladieAttacked = false;

    @Inject(
            method = "attack",
            at = @At("RETURN"))
    private void onAttack(Entity target, CallbackInfo ci) {
        // If we are holding a Haladie, enter double-attack logic.
        if(getMainHandStack().getItem() instanceof HaladieItem && !world.isClient) {
            // If we have NOT already attacked, reset the enemies i-frames and attack again.
            if(!gob_hasHaladieAttacked) {
                target.timeUntilRegen = 0;
                gob_hasHaladieAttacked = true;
                DoubleAttackHelper.queueDoubleAttack((ServerPlayerEntity) (Object) this, target);
                return;
            }
        }

        gob_hasHaladieAttacked = false;
    }
}
