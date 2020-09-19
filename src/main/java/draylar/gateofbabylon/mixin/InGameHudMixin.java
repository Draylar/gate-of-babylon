package draylar.gateofbabylon.mixin;

import draylar.gateofbabylon.GateOfBabylon;
import draylar.gateofbabylon.GateOfBabylonClient;
import draylar.gateofbabylon.item.KatanaItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Shadow @Final private MinecraftClient client;
    @Shadow private int scaledHeight;
    @Shadow private int scaledWidth;
    private static final Identifier KATANA_FOCUS = GateOfBabylon.id("textures/katana_zone.png");

    @Inject(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F")
    )
    private void yeet(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if(this.client.player.getMainHandStack().getItem() instanceof KatanaItem) {
            this.renderKatanaOverlay(matrices);
        }
    }

    @Unique
    private void renderKatanaOverlay(MatrixStack stack) {
        GateOfBabylonClient.renderKatanaOverlay(stack);
    }
}
