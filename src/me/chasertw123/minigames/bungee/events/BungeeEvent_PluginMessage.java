package me.chasertw123.minigames.bungee.events;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import me.chasertw123.minigames.bungee.Main;
import me.chasertw123.minigames.bungee.packets.ParadisePacket;
import me.chasertw123.minigames.bungee.user.User;
import me.chasertw123.minigames.shared.booster.GameBooster;
import me.chasertw123.minigames.shared.infraction.Punishment;
import me.chasertw123.minigames.shared.infraction.PunishmentTimeScale;
import me.chasertw123.minigames.shared.infraction.PunishmentType;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Chase on 7/25/2017.
 */
public class BungeeEvent_PluginMessage implements Listener {

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) {

        if (!e.getTag().equalsIgnoreCase("MCParadise"))
            return;

        ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
        String channel = in.readUTF();

        if (channel.equalsIgnoreCase("AddQueue")) {

            ProxiedPlayer proxiedPlayer = BungeeCord.getInstance().getPlayer(in.readUTF());
            if (proxiedPlayer == null)
                return;

            BungeeCord.getInstance().getPluginManager().dispatchCommand(proxiedPlayer, "play " + in.readUTF());
        }

        else if (channel.equalsIgnoreCase("GameBooster")) {

            ProxiedPlayer proxiedPlayer = BungeeCord.getInstance().getPlayer(in.readUTF());
            if (proxiedPlayer == null)
                return;

            String gameMode = in.readUTF();

            for(GameBooster gameBooster : Main.getInstance().getBoosterManager().getCurrentGameBoosters())
                if (gameBooster.getGameMode().equalsIgnoreCase(gameMode)) {
                    proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Main.STARTER + "A booster for that gamemode is already active!"));
                    return;
                }

            //create the object
            Main.getInstance().getBoosterManager().addGameBooster(new GameBooster(Main.getInstance().getDatabase(), System.currentTimeMillis() + (3600000), 2,
                    proxiedPlayer.getUniqueId(), proxiedPlayer.getName(), gameMode));
        }

        else if (channel.equalsIgnoreCase("Punishment")) {

            ProxiedPlayer proxiedPlayer = BungeeCord.getInstance().getPlayer(in.readUTF());
            if (proxiedPlayer == null)
                return;

            String type = in.readUTF();

            PunishmentType pType = PunishmentType.valueOf(type.toUpperCase());

            if(pType == PunishmentType.MUTE) {
                User.get(proxiedPlayer).addPunishment(new Punishment(pType, PunishmentTimeScale.valueOf(in.readUTF().toUpperCase()), Long.parseLong(in.readUTF())));
            } else {
                System.out.println("Banning the player " + proxiedPlayer.getDisplayName());

                String message = in.readUTF();
                proxiedPlayer.disconnect(TextComponent.fromLegacyText(message));
            }
        }

        else if (channel.equalsIgnoreCase("ModifyStat")) {


            String packetSender = in.readUTF();
            String playerToModifyName = in.readUTF();

            ProxiedPlayer playerToModify = BungeeCord.getInstance().getPlayer(playerToModifyName);
            if (playerToModify == null) {
                this.sendMessage(packetSender, ChatColor.RED + "Unable to find player: " + playerToModifyName);
                return;
            }

            String statToModify = in.readUTF();
            String amountTOModify = in.readUTF();
            String discreteModify = in.readUTF();

            new ParadisePacket("ModifyStat")
                    .addLine(playerToModify.getName())
                    .addLine(statToModify)
                    .addLine(amountTOModify)
                    .addLine(discreteModify)
                    .queue(playerToModify.getServer().getInfo().getName());

            this.sendMessage(packetSender, ChatColor.GREEN + "Set " + playerToModify.getName() + "'s " + statToModify + " stat to " + amountTOModify + ".");
        }
    }

    private void sendMessage(String reciever, String message) {
        ProxiedPlayer proxiedPlayer = BungeeCord.getInstance().getPlayer(reciever);
        if (proxiedPlayer != null)
            proxiedPlayer.sendMessage(TextComponent.fromLegacyText(message));

        else if (reciever.equalsIgnoreCase("Console"))
            System.out.println(ChatColor.stripColor(message));
    }

}
