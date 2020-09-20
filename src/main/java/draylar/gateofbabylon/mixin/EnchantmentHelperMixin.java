package draylar.gateofbabylon.mixin;

import draylar.gateofbabylon.api.ValidatingEnchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(
            method = "generateEnchantments",
            at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void generateEnchantments(Random random, ItemStack stack, int level, boolean treasureAllowed, CallbackInfoReturnable<List> cir, List list, Item item, int i, float f, List<EnchantmentLevelEntry> list2) {
        List<EnchantmentLevelEntry> newEnchantments = new ArrayList<>();

        list2.forEach(enchantmentLevelEntry -> {
            if(enchantmentLevelEntry.enchantment instanceof ValidatingEnchantment) {
                if(enchantmentLevelEntry.enchantment.isAcceptableItem(stack)) {
                    newEnchantments.add(enchantmentLevelEntry);
                }
            } else {
                newEnchantments.add(enchantmentLevelEntry);
            }
        });

        list2.clear();
        list2.addAll(newEnchantments);
    }
}
