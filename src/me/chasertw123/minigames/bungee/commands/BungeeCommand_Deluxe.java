package me.chasertw123.minigames.bungee.commands;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import me.chasertw123.minigames.bungee.Main;
import me.chasertw123.minigames.bungee.packets.ParadisePacket;
import me.chasertw123.minigames.bungee.user.User;
import me.chasertw123.minigames.shared.database.Database;
import me.chasertw123.minigames.shared.rank.RankType;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.Document;

import java.util.concurrent.TimeUnit;

public class BungeeCommand_Deluxe extends BungeeCommand{

    public BungeeCommand_Deluxe() {
        super("Deluxe");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        if(commandSender instanceof ProxiedPlayer)
            if (User.get((ProxiedPlayer) commandSender).getRank().getRankType() != RankType.UPPERSTAFF) {
                commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You do not have permission to do that!"));
                return;
            }

        if (args.length != 2) {
            commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Incorrect arguments: /deluxe <username> <days>"));
            return;
        }

        int days;
        try {
            days = Integer.parseInt(args[1]);
        } catch (Exception e) {
            commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + args[1] + "is not a number!"));
            return;
        }

        String username = args[0];

        int count = 0;

        for(Document d : Main.getInstance().getDatabase().getMongoCollection(Database.Collection.USERS)
                .find(Filters.eq("lastknownusername", username)))
            count++;

        if(count == 0) {
            commandSender.sendMessage(TextComponent.fromLegacyText("That user does not exist."));

            return;
        }

        long time = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days);

        BasicDBObject updateDeluxe = new BasicDBObject("$set", new BasicDBObject("deluxe", time));
        Main.getInstance().getDatabase().getMongoCollection(Database.Collection.USERS)
                .updateOne(Filters.eq("lastknownusername", username), updateDeluxe);

        commandSender.sendMessage(ChatColor.GREEN + "Set " + username + " to deluxe.");

        // Check if they are online, and if so send the data to the server they're on
        if(BungeeCord.getInstance().getPlayer(username) != null) {
            new ParadisePacket("SetDeluxe")
                    .addLine(username)
                    .addLine(time + "")
                    .queueForPlayer(username);
        }
    }

}
