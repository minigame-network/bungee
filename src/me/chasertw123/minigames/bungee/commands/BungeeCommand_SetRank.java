package me.chasertw123.minigames.bungee.commands;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import me.chasertw123.minigames.bungee.Main;
import me.chasertw123.minigames.bungee.packets.ParadisePacket;
import me.chasertw123.minigames.bungee.user.User;
import me.chasertw123.minigames.shared.database.Database;
import me.chasertw123.minigames.shared.rank.Rank;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.Document;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Chase on 8/2/2017.
 */
public class BungeeCommand_SetRank extends BungeeCommand {

    public BungeeCommand_SetRank() {
        super("setrank");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {

            if (args.length != 2) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Correct Usage: /setrank <player> <rank>"));
                return;
            }

            String formattedRank = args[1].toUpperCase();
            Rank rank = Rank.valueOf(formattedRank);

            if (rank == null) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Invalid Rank!"));
                return;
            }

            if (!containsPlayer(args[0])) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Invalid Player!"));
                return;
            }

            updateRank(args[0], rank);
            return;
        }

        User pp = User.get((ProxiedPlayer) sender);
        if (pp.getRank().getRankType().getRankLevel() < 3) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "I don't think so!"));
            return;
        }

        if (args.length != 2) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Correct Usage: /setrank <player> <rank>"));
            return;
        }

        String formattedRank = args[1].toUpperCase();
        Rank rank = Rank.valueOf(formattedRank);
        
	    if (rank == null) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Invalid Rank!"));
            return;
        }

        if (!containsPlayer(args[0])) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Invalid Player!"));
            return;
        }

        updateRank(args[0], rank);
    }

    private synchronized boolean containsPlayer(String username) {
        int count = 0;

        for(Document d : Main.getInstance().getDatabase().getMongoCollection(Database.Collection.USERS)
                .find(Filters.eq("lastknownusername", username)))
            count++;

        return count > 0;
    }

    private synchronized void updateRank(String username, Rank rank) {

        BasicDBObject updateDeluxe = new BasicDBObject("$set", new BasicDBObject("rank", rank.toString()));
        Main.getInstance().getDatabase().getMongoCollection(Database.Collection.USERS)
                .updateOne(Filters.eq("lastknownusername", username), updateDeluxe);

        ProxiedPlayer proxiedPlayer = BungeeCord.getInstance().getPlayer(username);
        if (proxiedPlayer != null) {
            Main.getInstance().getUserManager().getParadisePlayer(proxiedPlayer).setRank(rank);

            new ParadisePacket("SetRank")
                    .addLine(username)
                    .addLine(rank.toString())
                    .queue(proxiedPlayer.getServer().getInfo().getName());
        }
    }
}
