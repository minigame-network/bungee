package me.chasertw123.minigames.bungee.commands;

import me.chasertw123.minigames.bungee.Main;
import me.chasertw123.minigames.bungee.queue.v2.QueueGroup;
import me.chasertw123.minigames.bungee.user.User;
import me.chasertw123.minigames.shared.framework.ServerGameType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Scott Hiett on 8/1/2017.
 */
public class BungeeCommand_Play extends BungeeCommand {

    public BungeeCommand_Play() {
        super("play");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText("This command cannot be ran from the console!"));
            return;
        }

        ProxiedPlayer pp = (ProxiedPlayer) sender;
        if (args.length != 1) {
            pp.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Correct Usage: /play <game>"));
            pp.sendMessage(TextComponent.fromLegacyText(Main.STARTER + ChatColor.AQUA + "The current available games are " + ChatColor.WHITE
                    + Main.getInstance().getQueueController().getGameModes().toString()));

            return;
        }


        // Check if they're in a party!
        User u = User.get(pp.getUniqueId());
        if (Main.getInstance().getPartyManager().isInParty(u) && !Main.getInstance().getPartyManager().isPartyLeader(u)) {
            pp.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Hey! You aren't the leader of your party, so can't" +
                    " bring them all into that game. If you would like to play this game, either leave the party (/party leave)" +
                    " or ask your party leader to play this."));

            return;
        }

        String serverQuery = args[0];
        ServerGameType server = null;

        for(String gm : Main.getInstance().getQueueController().getGameModes())
            if(gm.equalsIgnoreCase(serverQuery))
                server = ServerGameType.find(gm.toUpperCase().replaceAll("-", "_"));

        if (server == null) {
            pp.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Invalid Game!"));
            pp.sendMessage(TextComponent.fromLegacyText(Main.STARTER + ChatColor.AQUA + "The current available games are " + ChatColor.WHITE
                    + Main.getInstance().getQueueController().getGameModes().toString()));

            return;
        }

        if (Main.getInstance().getQueueController().isInQueue(User.get(pp))) {
            pp.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You are already in the queue!"));
            return;
        }

        pp.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Joined Queue: " + ChatColor.WHITE + server + ChatColor.GREEN + "!"));

        ArrayList<UUID> usersToAdd = new ArrayList<>(); // Friggin kotlin requires it to be an ArrayList by class. That's whatever.
        usersToAdd.add(pp.getUniqueId());

        if(Main.getInstance().getPartyManager().isInParty(u)) {
            // They must be the leader. (Look up!)
            pp.sendMessage(TextComponent.fromLegacyText(ChatColor.BOLD + "" + ChatColor.GOLD + "Bringing the party with you!"));
            usersToAdd.addAll(Main.getInstance().getPartyManager().getParty(u).getMembers());
        }

        Main.getInstance().getQueueController().addToQueue(new QueueGroup(server, usersToAdd));
    }

}
