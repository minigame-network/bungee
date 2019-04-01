package me.chasertw123.minigames.bungee.events;

import net.md_5.bungee.api.plugin.Listener;

/**
 * Created by Scott Hiett on 8/7/2017.
 */
public class BungeeEvent_VotifierEvent implements Listener {

    /*
    @EventHandler
    public void votifierEvent(VotifierEvent event) {
        ProxiedPlayer pp = BungeeCord.getInstance().getPlayer(event.getVote().getUsername());

        if (pp != null) {
            new ParadisePacket("Vote")
                    .addLine(pp.getName())
                    .addLine(event.getVote().getServiceName())
                    .addLine(event.getVote().getTimeStamp())
                    .queueForPlayer(pp.getName());

            return;
        }

        new ParadisePacket("Vote")
                .addLine(event.getVote().getUsername())
                .addLine(event.getVote().getServiceName())
                .addLine(event.getVote().getTimeStamp())
                .queueForPlayer(event.getVote().getUsername());
    }
    */

}