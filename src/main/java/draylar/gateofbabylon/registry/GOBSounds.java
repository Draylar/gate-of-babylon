package draylar.gateofbabylon.registry;

import draylar.gateofbabylon.GateOfBabylon;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

public class GOBSounds {

    public static final SoundEvent KATANA_SWOOP = register("katana_swoop", SoundEvent.of(GateOfBabylon.id("katana_swoop")));

    public static SoundEvent register(String name, SoundEvent sound) {
        return Registry.register(Registries.SOUND_EVENT, GateOfBabylon.id(name), sound);
    }

    public static void init() {
        // NO-OP
    }

    private GOBSounds() {

    }
}
