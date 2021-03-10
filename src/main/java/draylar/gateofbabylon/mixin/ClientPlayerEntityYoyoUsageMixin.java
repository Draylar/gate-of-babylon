package draylar.gateofbabylon.mixin;

import draylar.gateofbabylon.item.YoyoItem;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityYoyoUsageMixin {

    @Redirect(
            method = "tickMovement",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean cancelYoyoSpeedDecrement(ClientPlayerEntity player) {
        if(player.isUsingItem()) {
            if(player.getActiveItem().getItem() instanceof YoyoItem) {
                return false;
            } else {
                return true;
            }
        }

        return false;
    }
}
