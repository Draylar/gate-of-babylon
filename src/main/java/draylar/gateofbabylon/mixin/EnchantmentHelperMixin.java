package draylar.gateofbabylon.mixin;

import com.google.common.collect.Lists;
import draylar.gateofbabylon.api.EnchantmentHandler;
import draylar.gateofbabylon.api.ValidatingEnchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
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
            if (enchantmentLevelEntry.enchantment instanceof ValidatingEnchantment) {
                if (enchantmentLevelEntry.enchantment.isAcceptableItem(stack)) {
                    newEnchantments.add(enchantmentLevelEntry);
                }
            } else {
                newEnchantments.add(enchantmentLevelEntry);
            }
        });

        list2.clear();
        list2.addAll(newEnchantments);
    }

    @Inject(
            method = "getPossibleEntries",
            at = @At("HEAD"), cancellable = true)
    private static void adjustPossibleEntries(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        Item item = stack.getItem();

        // Only adjust if the stack being passed in is our weapon
        if (item instanceof EnchantmentHandler) {
            List<EnchantmentLevelEntry> entries = Lists.newArrayList();

            // Collect valid enchantments
            Registry.ENCHANTMENT.forEach(enchantment -> {

                // Items can whitelist certain enchantments to always be valid.
                if(!((EnchantmentHandler) item).isExplicitlyValid(enchantment)) {

                    // This is where our primary logic-change is.
                    // Instead of asking the type for validity, we ask the enchantment.
                    // This allows our other hook in EnchantmentMixin to run.
                    if(!enchantment.isAcceptableItem(stack)) {
                        return;
                    }

                    // Ensure the stack accepts the given enchantment.
                    if(((EnchantmentHandler) item).isInvalid(enchantment)) {
                        return;
                    }

                    // If the enchantment is not available in the general pool (Soul Speed),
                    // ignore it.
                    if(!enchantment.isAvailableForRandomSelection()) {
                        return;
                    }

                    // If the enchantment is a treasure enchantment
                    //  and we are not looking for treasure enchantments, ignore it.
                    if(enchantment.isTreasure() && !treasureAllowed) {
                        return;
                    }
                }

                // Add all valid enchantment-power entries to the list.
                for (int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
                    if (power >= enchantment.getMinPower(i) && power <= enchantment.getMaxPower(i)) {
                        entries.add(new EnchantmentLevelEntry(enchantment, i));
                        break;
                    }
                }
            });

            cir.setReturnValue(entries);
        }
    }
}
