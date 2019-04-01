package me.chasertw123.minigames.bungee.commands;

import me.chasertw123.minigames.bungee.Main;
import me.chasertw123.minigames.bungee.parties.Party;
import me.chasertw123.minigames.bungee.parties.PartyManager;
import me.chasertw123.minigames.bungee.user.User;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BungeeCommand_Party extends BungeeCommand {

    public BungeeCommand_Party() {
        super("party");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(TextComponent.fromLegacyText("Sorry, this can't be done from the console."));

            return;
        }

        ProxiedPlayer pp = (ProxiedPlayer) commandSender;
        User user = User.get(pp);
        String arg = args.length > 0 ? args[0] : ""; // Prevent NPE
        PartyManager partyManager = Main.getInstance().getPartyManager();

        if(args.length == 2) { // create, invite
            if(arg.equalsIgnoreCase("invite") || arg.equalsIgnoreCase("create")) {
                String otherUsername = args[1];

                if(BungeeCord.getInstance().getPlayer(otherUsername) == null) {
                    user.sendMessage(ChatColor.RED + "Sorry, we couldn't find that user. Make sure they're online.");

                    return;
                }

                // Send them an invite.
                user.sendMessage(ChatColor.GOLD + "Sent " + ChatColor.RED + otherUsername + ChatColor.GOLD + " a party invite.");
                User.get(BungeeCord.getInstance().getPlayer(otherUsername)).setPartyInvite(user.getUUID());
                User.get(BungeeCord.getInstance().getPlayer(otherUsername)).sendMessage(ChatColor.GOLD + "You've been " +
                        "invited to party with " + ChatColor.GREEN + user.getPlayer().getName() + ChatColor.GOLD + "!");
            }
        } else if (args.length == 1) {
            if(arg.equalsIgnoreCase("help")) {
                sendHelp(user);
            } else if (arg.equalsIgnoreCase("accept")) {
                if(!user.hasPartyInvite()) {
                    user.sendMessage(ChatColor.RED + "You can't accept an invite that doesn't exist!");
                } else {
                    User other = User.get(user.getPartyInvite());

                    if(partyManager.isInParty(other)) {
                        partyManager.getParty(other).addMember(user.getUUID());
                        partyManager.getParty(other).broadcastMessage(ChatColor.GREEN + user.getPlayer().getName()
                                + " has joined the party!");
                    } else {
                        Party party = partyManager.createParty(other.getUUID(), user.getUUID());

                        party.broadcastMessage(ChatColor.GREEN + "Welcome to the party! " + ChatColor.RED
                                + other.getPlayer().getName() + ChatColor.GREEN + " is the party owner!");
                    }
                }
            } else if (arg.equalsIgnoreCase("decline")) {
                if(!user.hasPartyInvite()) {
                    user.sendMessage(ChatColor.RED + "You can't decline an invite that doesn't exist!");
                } else {
                    // Get the other player
                    User other = User.get(user.getPartyInvite());

                    user.sendMessage(ChatColor.RED + "Declined the invite from " + ChatColor.GREEN
                            + other.getPlayer().getName() + ChatColor.RED + ".");
                    other.sendMessage(ChatColor.RED + user.getPlayer().getName() + " declined your party invite.");

                    user.setPartyInvite(null);
                }
            } else if (!partyManager.isInParty(user)) {
                // EVERYTHING BELOW REQUIRES PARTY.
                user.sendMessage(ChatColor.RED + "You aren't in a party!");
            } else if (arg.equalsIgnoreCase("leave")) {
                partyManager.removeFromParty(user);
            } else if (arg.equalsIgnoreCase("list")) {
                List<String> partyMems = new ArrayList<>();

                partyManager.getParty(user).getFullPartyArray().forEach(mem -> partyMems.add(BungeeCord.getInstance().getPlayer(mem).getName()));

                user.sendMessage(ChatColor.GOLD + "Party members: " + ChatColor.RED + partyMems.toString().replace("[", "").replace("]", ""));
            }
        } else {
            sendHelp(user);
        }
    }

    private static final String[] helpMessages = {
            "/party create <username>",
            "/party invite <username>",
            "/party leave",
            "/party accept",
            "/party decline",
            "/party list",
            "/party help"
    };

    private void sendHelp(User player) {
        Arrays.asList(helpMessages).forEach(player::sendMessage);
    }

}
