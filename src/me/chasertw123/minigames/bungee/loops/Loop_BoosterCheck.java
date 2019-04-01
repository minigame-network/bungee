package me.chasertw123.minigames.bungee.loops;

import me.chasertw123.minigames.bungee.Main;
import me.chasertw123.minigames.shared.booster.GameBooster;
import net.md_5.bungee.BungeeCord;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Scott Hiett on 8/4/2017.
 */
public class Loop_BoosterCheck {

    /**
     * Checks all the boosters to see if any of them are outdated. If so, removes them from the current arraylist.
     */
    public Loop_BoosterCheck(){
        BungeeCord.getInstance().getScheduler().schedule(Main.getInstance(), () -> {

            if (Main.getInstance().getBoosterManager().getCurrentEventBooster() != null && Main.getInstance().getBoosterManager().getCurrentEventBooster().getEndTime() < System.currentTimeMillis())
                Main.getInstance().getBoosterManager().removeCurrentEventBooster();

            if (Main.getInstance().getBoosterManager().isAnyGameBoosterActive()) {
                List<GameBooster> toRemove = new ArrayList<>();
                for (GameBooster gameBooster : Main.getInstance().getBoosterManager().getCurrentGameBoosters())
                    if (gameBooster.getEndTime() < System.currentTimeMillis())
                        toRemove.add(gameBooster);

                for (GameBooster gameBooster : toRemove)
                    Main.getInstance().getBoosterManager().removeGameBooster(gameBooster);
            }
        }, 0, 2, TimeUnit.MINUTES);
    }

}
