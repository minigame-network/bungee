package me.chasertw123.minigames.bungee.events;

import me.chasertw123.minigames.bungee.Main;
import me.chasertw123.minigames.bungee.user.User;
import me.chasertw123.minigames.shared.database.ChatLog;
import me.chasertw123.minigames.shared.rank.RankType;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeEvent_Chat implements Listener {

    @EventHandler
    public void onChat(ChatEvent e) {
        if(!(e.getSender() instanceof ProxiedPlayer))
            return;

        ChatLog.ChatType chatType = ChatLog.ChatType.GENERAL;

        User sender = User.get((ProxiedPlayer) e.getSender());
        String message = e.getMessage();

        if(e.getMessage().startsWith("@") && sender.getRank().getRankType().getRankLevel() >= RankType.STAFF.getRankLevel()) {

            e.setCancelled(true);
            chatType = ChatLog.ChatType.STAFF;

            for(ProxiedPlayer pp : BungeeCord.getInstance().getPlayers())
                if(User.get(pp).getRank().getRankType().getRankLevel() >= RankType.STAFF.getRankLevel())
                    pp.sendMessage(TextComponent.fromLegacyText(ChatColor.DARK_AQUA + "[Staff Chat] " + ChatColor.WHITE
                            + sender.getPlayer().getName() + ": " + message));
        }

        Main.getInstance().getChatLog().logChatMessage(sender.getUUID(), chatType, message);
    }

}
