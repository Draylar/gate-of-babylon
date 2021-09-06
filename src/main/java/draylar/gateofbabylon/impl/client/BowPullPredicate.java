package draylar.gateofbabylon.impl.client;

import draylar.gateofbabylon.item.CustomBowItem;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("ClassCanBeRecord")
public class BowPullPredicate implements UnclampedModelPredicateProvider {

    private final CustomBowItem bow;

    public BowPullPredicate(CustomBowItem bow) {
        this.bow = bow;
    }

    @Override
    public float unclampedCall(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
        if (entity == null) {
            return 0.0F;
        } else {
            return entity.getActiveItem() != stack ? 0.0F : (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / bow.getMaxDrawTime(stack);
        }
    }
}
