package draylar.gateofbabylon.mixin;

import net.minecraft.block.ButtonBlock;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ButtonBlock.class)
public interface AbstractButtonBlockAccessor {

    @Invoker
    SoundEvent callGetClickSound(boolean powered);
}
