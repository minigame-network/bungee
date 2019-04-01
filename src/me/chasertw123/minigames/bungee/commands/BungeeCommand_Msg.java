package me.chasertw123.minigames.bungee.commands;

import me.chasertw123.minigames.bungee.Main;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Scott Hiett on 8/6/2017.
 */
public class BungeeCommand_Msg extends BungeeCommand {

    private static final String[] aliases = {"tell", "whisper", "message", "pm"};

    public BungeeCommand_Msg() {
        super("msg", "", aliases);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        if ((!(commandSender instanceof ProxiedPlayer))){
            commandSender.sendMessage(TextComponent.fromLegacyText("This command cannot be ran from the console!"));
            return;
        }

        if (args.length < 2){
            commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Correct Usage: /msg <username> <message>"));
            return;
        }

        ProxiedPlayer sender = (ProxiedPlayer) commandSender;
        String otherPlayer = args[0], message = "";
        for (String s : args)
            if (!s.equals(args[0]))
                message = message + s + " ";

        message = message.substring(0, message.length() - 1);

        ProxiedPlayer other = BungeeCord.getInstance().getPlayer(otherPlayer);
        if (other == null){
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Invalid Player!"));
            return;
        }

	    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.DARK_AQUA + "You " + ChatColor.WHITE + " ➜ " + ChatColor.GREEN + other.getName() + ChatColor.WHITE + ": " + message));

        // Log this
        Main.getInstance().getChatLog().logPm(sender.getUniqueId(), other.getUniqueId(), message);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        try {
            out.writeUTF("SendMessage");
            out.writeUTF(sender.getName());
            out.writeUTF(other.getName());
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        other.getServer().sendData("MCParadise", stream.toByteArray());
    }

}
