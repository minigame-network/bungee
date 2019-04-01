package me.chasertw123.minigames.bungee.events;

import me.chasertw123.minigames.bungee.Main;
import me.chasertw123.minigames.bungee.user.User;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Scott Hiett on 7/25/2017.
 */
public class BungeeEvent_LeaveServer implements Listener {

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent e){

        //Save the user
        ProxiedPlayer player = e.getPlayer();

        User user = User.get(player);

        //Check if they are in a queue
        if (Main.getInstance().getQueueController().isInQueue(user))
            Main.getInstance().getQueueController().removeFromQueue(user);

        if(Main.getInstance().getPartyManager().isInParty(user))
            Main.getInstance().getPartyManager().removeFromParty(user);

        // Save their data
        user.saveUserData(true);

        // Remove from cache
        Main.getInstance().getUserManager().remove(player.getUniqueId());
    }

}
