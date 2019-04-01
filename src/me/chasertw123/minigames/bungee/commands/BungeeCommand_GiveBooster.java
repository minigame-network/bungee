package me.chasertw123.minigames.bungee.commands;

import me.chasertw123.minigames.bungee.Main;
import me.chasertw123.minigames.bungee.user.User;
import me.chasertw123.minigames.shared.framework.ServerGameType;
import me.chasertw123.minigames.shared.rank.RankType;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Scott Hiett on 8/5/2017.
 */
public class BungeeCommand_GiveBooster extends BungeeCommand {

    public BungeeCommand_GiveBooster() {
        super("givebooster");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        if (commandSender instanceof ProxiedPlayer)
            if (User.get((ProxiedPlayer) commandSender).getRank().getRankType().getRankLevel() < RankType.UPPERSTAFF.getRankLevel()) {
                commandSender.sendMessage(TextComponent.fromLegacyText(Main.STARTER + ChatColor.RED + "You don't have permission to do that!"));
                return;
            }

        if (args.length != 3) {
            commandSender.sendMessage(TextComponent.fromLegacyText(Main.STARTER + ChatColor.RED + "Incorrect arguments. /givebooster <username> <gamemode> <amount>"));
            return;
        }

        ProxiedPlayer pp = BungeeCord.getInstance().getPlayer(args[0]);
        if (pp == null) {
            commandSender.sendMessage(TextComponent.fromLegacyText(Main.STARTER + ChatColor.RED + "Could not find that player! (Are they online?)"));
            return;
        }

        ServerGameType serverGameType = null;
        for (ServerGameType server : ServerGameType.values())
            if (args[1].equalsIgnoreCase(server.toString())) {
                serverGameType = server;
                break;
            }

        if (serverGameType == null) {
            commandSender.sendMessage(TextComponent.fromLegacyText(Main.STARTER + ChatColor.RED + args[1] + " is not in the list of current gamemodes."));
            return;
        }

        if (!isInteger(args[2])) {
            commandSender.sendMessage(TextComponent.fromLegacyText(Main.STARTER + ChatColor.RED + args[2] + " is not a number."));
            return;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        try {
            out.writeUTF("GiveGameBooster");
            out.writeUTF(pp.getName());
            out.writeUTF(serverGameType.toString());
            out.writeUTF(args[2]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        pp.getServer().sendData("MCParadise", stream.toByteArray());
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(Exception e) {
            return false;
        }

        return true;
    }
}
