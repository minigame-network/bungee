package me.chasertw123.minigames.bungee.events;

import me.chasertw123.minigames.bungee.Main;
import net.md_5.bungee.api.plugin.Listener;

import java.util.Arrays;

/**
 * Created by Scott Hiett on 1/4/2018.
 */
public class EventManager {

    private static final Listener[] EVENTS = {
            new BungeeEvent_Chat(),
            new BungeeEvent_LeaveServer(),
            new BungeeEvent_ChangeServer(),
            new BungeeEvent_PostLogin(),
            new BungeeEvent_PluginMessage(),
            new BungeeEvent_VotifierEvent()
    };

    public EventManager() {
        Arrays.asList(EVENTS).forEach(l -> Main.getInstance().getProxy().getPluginManager().registerListener(Main.getInstance(), l));
    }

}
