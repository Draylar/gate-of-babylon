package draylar.gateofbabylon.entity;

import com.google.common.collect.Lists;
import draylar.gateofbabylon.impl.AreaEffectCloudTicker;
import draylar.gateofbabylon.mixin.AreaEffectCloudEntityAccessor;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Predicate;

public class DragonSlashBreathEntity extends AreaEffectCloudEntity {

   public DragonSlashBreathEntity(World world, double x, double y, double z) {
      super(world, x, y, z);
   }
   
   @Override
   public void tick() {
      ((AreaEffectCloudTicker)this).superTick();
      boolean bl = this.isWaiting();
      float f = this.getRadius();
      if (this.world.isClient) {
         ParticleEffect particleEffect = this.getParticleType();
         float h;
         float j;
         float k;
         int m;
         int n;
         int o;
         if (bl) {
            if (this.random.nextBoolean()) {
               for(int i = 0; i < 2; ++i) {
                  float g = this.random.nextFloat() * 6.2831855F;
                  h = MathHelper.sqrt(this.random.nextFloat()) * 0.2F;
                  j = MathHelper.cos(g) * h;
                  k = MathHelper.sin(g) * h;
                  if (particleEffect.getType() == ParticleTypes.ENTITY_EFFECT) {
                     int l = this.random.nextBoolean() ? 16777215 : this.getColor();
                     m = l >> 16 & 255;
                     n = l >> 8 & 255;
                     o = l & 255;
                     this.world.addImportantParticle(particleEffect, this.getX() + (double)j, this.getY(), this.getZ() + (double)k, (double)((float)m / 255.0F), (double)((float)n / 255.0F), (double)((float)o / 255.0F));
                  } else {
                     this.world.addImportantParticle(particleEffect, this.getX() + (double)j, this.getY(), this.getZ() + (double)k, 0.0D, 0.0D, 0.0D);
                  }
               }
            }
         } else {
            float p = 3.1415927F * f * f;

            for(int q = 0; (float)q < p; ++q) {
               h = this.random.nextFloat() * 6.2831855F;
               j = MathHelper.sqrt(this.random.nextFloat()) * f;
               k = MathHelper.cos(h) * j;
               float u = MathHelper.sin(h) * j;
               if (particleEffect.getType() == ParticleTypes.ENTITY_EFFECT) {
                  m = this.getColor();
                  n = m >> 16 & 255;
                  o = m >> 8 & 255;
                  int y = m & 255;
                  this.world.addImportantParticle(particleEffect, this.getX() + (double)k, this.getY(), this.getZ() + (double)u, (double)((float)n / 255.0F), (double)((float)o / 255.0F), (double)((float)y / 255.0F));
               } else {
                  this.world.addImportantParticle(particleEffect, this.getX() + (double)k, this.getY(), this.getZ() + (double)u, (0.5D - this.random.nextDouble()) * 0.15D, 0.009999999776482582D, (0.5D - this.random.nextDouble()) * 0.15D);
               }
            }
         }
      } else {
         if (this.age >= ((AreaEffectCloudEntityAccessor)this).getWaitTime() + ((AreaEffectCloudEntityAccessor)this).getDuration()) {
            this.remove(RemovalReason.DISCARDED);
            return;
         }

         boolean bl2 = this.age < ((AreaEffectCloudEntityAccessor)this).getWaitTime();
         if (bl != bl2) {
            this.setWaiting(bl2);
         }

         if (bl2) {
            return;
         }

         if (((AreaEffectCloudEntityAccessor)this).getRadiusGrowth() != 0.0F) {
            f += ((AreaEffectCloudEntityAccessor)this).getRadiusGrowth();
            if (f < 0.5F) {
               this.remove(RemovalReason.DISCARDED);
               return;
            }

            this.setRadius(f);
         }

         if (this.age % 5 == 0) {
            Iterator iterator = ((AreaEffectCloudEntityAccessor)this).getAffectedEntities().entrySet().iterator();

            while(iterator.hasNext()) {
               Entry<Entity, Integer> entry = (Entry)iterator.next();
               if (this.age >= (Integer)entry.getValue()) {
                  iterator.remove();
               }
            }

            List<StatusEffectInstance> list = Lists.newArrayList();
            Iterator var22 = ((AreaEffectCloudEntityAccessor)this).getPotion().getEffects().iterator();

            while(var22.hasNext()) {
               StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var22.next();
               list.add(new StatusEffectInstance(statusEffectInstance.getEffectType(), statusEffectInstance.getDuration() / 4, statusEffectInstance.getAmplifier(), statusEffectInstance.isAmbient(), statusEffectInstance.shouldShowParticles()));
            }

            list.addAll(((AreaEffectCloudEntityAccessor)this).getEffects());
            if (list.isEmpty()) {
               ((AreaEffectCloudEntityAccessor)this).getAffectedEntities().clear();
            } else {
               // Triggers for entities that aren't tameable, or that aren't tamed, or that aren't owned by the owner of the breath
               Predicate<MobEntity> mobCheck = (entity) -> !(entity instanceof TameableEntity) || !((TameableEntity) entity).isTamed() || !((TameableEntity) entity).getOwnerUuid().equals(this.getOwner().getUuid());
               List<MobEntity> list2 = this.world.getEntitiesByClass(MobEntity.class, this.getBoundingBox(), mobCheck);
               if (!list2.isEmpty()) {
                  Iterator var25 = list2.iterator();

                  while(true) {
                     LivingEntity livingEntity;
                     double z;
                     do {
                        do {
                           do {
                              if (!var25.hasNext()) {
                                 return;
                              }

                              livingEntity = (LivingEntity)var25.next();
                           } while(((AreaEffectCloudEntityAccessor)this).getAffectedEntities().containsKey(livingEntity));
                        } while(!livingEntity.isAffectedBySplashPotions());

                        double d = livingEntity.getX() - this.getX();
                        double e = livingEntity.getZ() - this.getZ();
                        z = d * d + e * e;
                     } while(z > (double)(f * f));

                     ((AreaEffectCloudEntityAccessor)this).getAffectedEntities().put(livingEntity, this.age + ((AreaEffectCloudEntityAccessor)this).getReapplicationDelay());
                     Iterator var14 = list.iterator();

                     while(var14.hasNext()) {
                        StatusEffectInstance statusEffectInstance2 = (StatusEffectInstance)var14.next();
                        if (statusEffectInstance2.getEffectType().isInstant()) {
                           statusEffectInstance2.getEffectType().applyInstantEffect(this, this.getOwner(), livingEntity, statusEffectInstance2.getAmplifier(), 0.5D);
                        } else {
                           livingEntity.addStatusEffect(new StatusEffectInstance(statusEffectInstance2));
                        }
                     }

                     if (((AreaEffectCloudEntityAccessor)this).getRadiusOnUse() != 0.0F) {
                        f += ((AreaEffectCloudEntityAccessor)this).getRadiusOnUse();
                        if (f < 0.5F) {
                           this.remove(RemovalReason.DISCARDED);
                           return;
                        }

                        this.setRadius(f);
                     }

                     if (((AreaEffectCloudEntityAccessor)this).getDurationOnUse() != 0) {
                        ((AreaEffectCloudEntityAccessor)this).setDuration(((AreaEffectCloudEntityAccessor)this).getDuration() + ((AreaEffectCloudEntityAccessor)this).getDurationOnUse());
                        if (((AreaEffectCloudEntityAccessor)this).getDuration() <= 0) {
                           this.remove(RemovalReason.DISCARDED);
                           return;
                        }
                     }
                  }
               }
            }
         }
      }

   }
}