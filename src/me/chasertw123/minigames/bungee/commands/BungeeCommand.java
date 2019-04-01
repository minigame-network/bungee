package me.chasertw123.minigames.bungee.commands;

import me.chasertw123.minigames.bungee.Main;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Scott Hiett on 1/4/2018.
 */
public abstract class BungeeCommand extends Command {

    public BungeeCommand(String command) {
        super(command);

        Main.getInstance().getProxy().getPluginManager().registerCommand(Main.getInstance(), this);
    }

    public BungeeCommand(String command, String a, String[] b) {
        super(command, a , b);

        Main.getInstance().getProxy().getPluginManager().registerCommand(Main.getInstance(), this);
    }

    public void unregister() {
        Main.getInstance().getProxy().getPluginManager().unregisterCommand(this);
    }

}
