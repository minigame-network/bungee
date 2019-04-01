package me.chasertw123.minigames.bungee.commands;

/**
 * Created by Scott Hiett on 1/4/2018.
 */
public class CommandManager {

    public CommandManager() {
        new BungeeCommand_Play();
        new BungeeCommand_CreateEvent();
        new BungeeCommand_GiveBooster();
        new BungeeCommand_Msg();
        new BungeeCommand_SetRank();
        new BungeeCommand_Deluxe();
        new BungeeCommand_Friend();
        new BungeeCommand_Reply();
        new BungeeCommand_Party();
    }

}
