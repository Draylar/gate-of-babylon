package draylar.gateofbabylon.impl.client;

import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BowPullingPredicate implements UnclampedModelPredicateProvider {

    @Override
    public float unclampedCall(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
        return entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F;
    }
}
