package me.chasertw123.minigames.bungee.commands;

import me.chasertw123.minigames.bungee.user.User;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeCommand_Reply extends BungeeCommand {

    private static final String[] aliases = {"r"};

    public BungeeCommand_Reply() {
        super("reply", "", aliases);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(TextComponent.fromLegacyText("This can't be done from the console!"));

            return;
        }

        // Get the last person messaged.
        User user = User.get(((ProxiedPlayer) commandSender).getUniqueId());

        if(user.getLastTalkedTo() == null) {
            commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "You haven't recently messaged anyone :("));

            return;
        }

        // Get the other person's username
        String otherUsername = User.get(user.getLastTalkedTo()).getUsername();

        if(otherUsername == null) {
            commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Sorry, that player is no longer online."));

            return;
        }

        String message = "";
        for (String s : args)
            if (!s.equals(args[0]))
                message = message + s + " ";

        ProxyServer.getInstance().getPluginManager().dispatchCommand(commandSender, "msg " + otherUsername + " " + message);
    }

}
