package draylar.gateofbabylon.api;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

import java.util.Timer;
import java.util.TimerTask;

public class DoubleAttackHelper {

    public static void queueDoubleAttack(ServerPlayerEntity player, Entity target) {
        Timer timer = new Timer();

        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if(player.getServer() != null) {
                            player.getServer().execute(() -> {
                                if (target.isAlive()) {
                                    player.attack(target);
                                    player.swingHand(Hand.MAIN_HAND);
                                }
                            });
                        }
                    }
                }, 250);
    }
}
