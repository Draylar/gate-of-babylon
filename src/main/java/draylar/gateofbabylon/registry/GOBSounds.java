package draylar.gateofbabylon.registry;

import draylar.gateofbabylon.GateOfBabylon;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;

public class GOBSounds {

    public static final SoundEvent KATANA_SWOOP = register("katana_swoop", new SoundEvent(GateOfBabylon.id("katana_swoop")));

    public static SoundEvent register(String name, SoundEvent sound) {
        return Registry.register(Registry.SOUND_EVENT, GateOfBabylon.id(name), sound);
    }

    public static void init() {
        // NO-OP
    }

    private GOBSounds() {

    }
}
