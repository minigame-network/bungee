package me.chasertw123.minigames.bungee.commands;

import me.chasertw123.minigames.bungee.Main;
import me.chasertw123.minigames.bungee.packets.ParadisePacket;
import me.chasertw123.minigames.bungee.user.User;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by Scott Hiett on 23/08/2017.
 */
public class BungeeCommand_Friend extends BungeeCommand {

    public BungeeCommand_Friend() {
        super("friend");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(TextComponent.fromLegacyText("Only players can use that command!"));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        User user = Main.getInstance().getUserManager().getParadisePlayer(player);

        if (args.length == 2) {

            String query = args[0];
            String arg = args[1];

            if(query.equalsIgnoreCase("add")) {
                ProxiedPlayer to = BungeeCord.getInstance().getPlayer(arg);

                if(to == null) {
                    player.sendMessage(TextComponent.fromLegacyText(Main.STARTER + ChatColor.RED + "That player is not online!"));

                    return;
                }

                User playerTo = User.get(to);

                if(playerTo.getFriendRequests().contains(player.getUniqueId())) {
                    player.sendMessage(TextComponent.fromLegacyText(Main.STARTER + ChatColor.RED + "You have already sent a friend request to that player!"));

                    return;
                }


                if(to.getUniqueId() == player.getUniqueId()) {
                    player.sendMessage(TextComponent.fromLegacyText(Main.STARTER + ChatColor.RED + "You can't be friends with yourself!"));

                    return;
                }

                playerTo.addFriendRequest(player.getUniqueId());
                player.sendMessage(TextComponent.fromLegacyText(Main.STARTER + "Sent a friend request to " + playerTo.getPlayer().getName() + "!"));
            }

            else if (query.equalsIgnoreCase("remove")) {
                ProxiedPlayer to = BungeeCord.getInstance().getPlayer(arg);

                if(to == null) {
                    player.sendMessage(TextComponent.fromLegacyText(Main.STARTER + ChatColor.RED + "That player is not online!"));
                    return;
                }

                new ParadisePacket("RemoveFriend").addLine(to.getName()).addLine(player.getUniqueId() + "").addLine(player.getName()).queueForPlayer(to.getName());
                new ParadisePacket("RemoveFriend").addLine(player.getName()).addLine(player.getUniqueId() + "").addLine(to.getName()).queueForPlayer(player.getName());
            }

            else if (query.equalsIgnoreCase("accept")) {
                ProxiedPlayer to = BungeeCord.getInstance().getPlayer(arg);

                if(to == null) {
                    player.sendMessage(TextComponent.fromLegacyText(Main.STARTER + ChatColor.RED + "That player is not online!"));

                    return;
                }

                if(!Main.getInstance().getUserManager().getParadisePlayer(player).getFriendRequests().contains(to.getUniqueId())) {
                    player.sendMessage(TextComponent.fromLegacyText(Main.STARTER + ChatColor.RED + "That person has not sent you a friend request!"));

                    return;
                }

                new ParadisePacket("AddFriend").addLine(to.getName()).addLine(player.getUniqueId() + "").queueForPlayer(to.getName());
                new ParadisePacket("AddFriend").addLine(player.getName()).addLine(player.getUniqueId() + "").queueForPlayer(player.getName());

                to.sendMessage(Main.STARTER + ChatColor.GREEN +  player.getName() + ChatColor.WHITE + " accepted your friend request.");
                player.sendMessage(Main.STARTER + ChatColor.WHITE + "Accepted " + ChatColor.GREEN + to.getName() + ChatColor.WHITE + "'s friend request!");

                user.removeFriendRequest(to.getUniqueId());
            }

            else if (query.equalsIgnoreCase("decline")) {
                ProxiedPlayer to = BungeeCord.getInstance().getPlayer(arg);

                if(to == null) {
                    player.sendMessage(TextComponent.fromLegacyText(Main.STARTER + ChatColor.RED + "That player is not online!"));

                    return;
                }

                if(!Main.getInstance().getUserManager().getParadisePlayer(player).getFriendRequests().contains(to.getUniqueId())) {
                    player.sendMessage(TextComponent.fromLegacyText(Main.STARTER + ChatColor.RED + "That person has not sent you a friend request!"));

                    return;
                }

                to.sendMessage(TextComponent.fromLegacyText(Main.STARTER + ChatColor.RED +  player.getName() + ChatColor.WHITE + " declined your friend request."));
                user.removeFriendRequest(to.getUniqueId());

                player.sendMessage(TextComponent.fromLegacyText(Main.STARTER + ChatColor.WHITE + "Declined " + ChatColor.RED + to.getName() + ChatColor.WHITE + "'s friend request."));
            }

        }

        else if (args.length == 1) {

            String query = args[0];

        } else {
            player.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Friend Help"));
            player.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "/friend add <username>"));
            player.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "/friend remove <username>"));
            player.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "/friend accept <username>"));
            player.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "/friend decline <username>"));
        }
    }

}
