package draylar.gateofbabylon.mixin;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;

@Mixin(AreaEffectCloudEntity.class)
public interface AreaEffectCloudEntityAccessor {
    @Accessor("waitTime")
    public int getWaitTime();

    @Accessor("duration")
    public int getDuration();

    @Accessor("duration")
    public void setDuration(int duration);

    @Accessor("radiusGrowth")
    public float getRadiusGrowth();

    @Accessor("affectedEntities")
    public Map<Entity, Integer> getAffectedEntities();

    @Accessor("potion")
    public Potion getPotion();

    @Accessor("effects")
    public List<StatusEffectInstance> getEffects();

    @Accessor("reapplicationDelay")
    public int getReapplicationDelay();

    @Accessor("radiusOnUse")
    public float getRadiusOnUse();

    @Accessor("durationOnUse")
    public int getDurationOnUse();
}
