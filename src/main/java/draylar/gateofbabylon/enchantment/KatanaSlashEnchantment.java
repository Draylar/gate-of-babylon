package draylar.gateofbabylon.enchantment;

import draylar.gateofbabylon.api.ValidatingEnchantment;
import draylar.gateofbabylon.item.KatanaItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundEvent;

public class KatanaSlashEnchantment extends Enchantment implements ValidatingEnchantment {

    private final SoundEvent sound;
    private final ParticleEffect particle;
    protected final HitExecutor onHit;

    public KatanaSlashEnchantment(SoundEvent sound, ParticleEffect particle) {
        this(sound, particle, (entity, player, stack) -> {});
    }

    public KatanaSlashEnchantment(SoundEvent sound, ParticleEffect particle, HitExecutor onHit) {
        super(Rarity.UNCOMMON, EnchantmentTarget.WEAPON, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
        this.sound = sound;
        this.particle = particle;
        this.onHit = onHit;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof KatanaItem;
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return !(other instanceof KatanaSlashEnchantment);
    }

    public SoundEvent getSound() {
        return sound;
    }

    public ParticleEffect getParticle() {
        return particle;
    }

    public void onHit(LivingEntity target, PlayerEntity source, ItemStack stack) {
        onHit.run(target, source, stack);
    }

    public interface HitExecutor {
        void run(LivingEntity target, PlayerEntity source, ItemStack stack);
    }
}
