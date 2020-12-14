package draylar.gateofbabylon.registry;

import draylar.gateofbabylon.GateOfBabylon;
import draylar.gateofbabylon.enchantment.DragonSlashEnchantment;
import draylar.gateofbabylon.enchantment.KatanaSlashEnchantment;
import draylar.gateofbabylon.enchantment.ThunderSlashEnchantment;
import draylar.gateofbabylon.enchantment.LungingEnchantment;
import draylar.gateofbabylon.enchantment.QuickDrawEnchantment;
import draylar.gateofbabylon.enchantment.SmashingEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.registry.Registry;

public class GOBEnchantments {

    public static final LungingEnchantment LUNGING = register("lunging", new LungingEnchantment());
    public static final SmashingEnchantment SMASHING = register("smashing", new SmashingEnchantment());
    public static final DragonSlashEnchantment DRAGON_SLASH = register("dragon_slash", new DragonSlashEnchantment());
    public static final ThunderSlashEnchantment THUNDER_SLASH = register("thunder_slash", new ThunderSlashEnchantment());
    public static final KatanaSlashEnchantment FLAME_SLASH = register("flame_slash", new KatanaSlashEnchantment(SoundEvents.BLOCK_FIRE_AMBIENT, ParticleTypes.FLAME, (target, source, stack) -> target.setOnFireFor(6)));
    public static final KatanaSlashEnchantment VAMPIRE_SLASH = register("vampire_slash", new KatanaSlashEnchantment(SoundEvents.ENTITY_MAGMA_CUBE_SQUISH, ParticleTypes.HEART, (target, source, stack) -> source.heal(2.0f)));
    public static final QuickDrawEnchantment QUICKDRAW = register("quickdraw", new QuickDrawEnchantment());

    private static <T extends Enchantment> T register(String name, T enchantment) {
        return Registry.register(Registry.ENCHANTMENT, GateOfBabylon.id(name), enchantment);
    }

    public static void init() {
        // NO-OP
    }

    private GOBEnchantments() {
        // NO-OP
    }
}
