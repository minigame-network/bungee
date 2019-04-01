package me.chasertw123.minigames.bungee.commands;

import me.chasertw123.minigames.bungee.Main;
import me.chasertw123.minigames.bungee.user.User;
import me.chasertw123.minigames.shared.booster.EventBooster;
import me.chasertw123.minigames.shared.rank.RankType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by Scott Hiett on 8/4/2017.
 */
public class BungeeCommand_CreateEvent extends BungeeCommand {

    public BungeeCommand_CreateEvent() {
        super("setevent");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(TextComponent.fromLegacyText("This command cannot be ran from the console!"));
            return;
        }

        if(args.length < 3){
            commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Correct Usage: /setevent <multiplier> <hours> <reason>"));
            return;
        }

        ProxiedPlayer pp = (ProxiedPlayer) commandSender;
        User pap = User.get(pp);

        if (pap.getRank().getRankType().getRankLevel() < RankType.UPPERSTAFF.getRankLevel()) {
            pp.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "I don't think so!"));
            return;
        }

        if (!isInteger(args[0])) {
            commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Invalid Multiplier!"));
            return;
        }

        if (!isInteger(args[1])) {
            commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Invalid Time Duration!"));
            return;
        }

        int multiplier = Integer.parseInt(args[0]);
        int hours = Integer.parseInt(args[1]);

        String desc = "";
        for(String s : args)
            if(!(s.equals(args[0]) || s.equals(args[1])))
                desc = desc + s + " ";

        desc = desc.substring(0, desc.length() - 1);

        Main.getInstance().getBoosterManager().setCurrentEventBooster(new EventBooster(Main.getInstance().getDatabase(),
                System.currentTimeMillis() + (hours * 3600000), multiplier, desc));

        pp.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Created Event Booster!"));
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
