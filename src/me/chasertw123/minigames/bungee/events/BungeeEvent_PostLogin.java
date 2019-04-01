package me.chasertw123.minigames.bungee.events;

import me.chasertw123.minigames.bungee.Main;
import me.chasertw123.minigames.bungee.packets.ParadisePacket;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chase on 7/1/2017.
 */
public class BungeeEvent_PostLogin implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent e) {
        ProxiedPlayer player = e.getPlayer();

        Main.getInstance().getUserManager().add(player);

        List<ParadisePacket> toRemove = new ArrayList<>();
        for(ParadisePacket packet : Main.getInstance().getParadisePacketManager().getPacketUserQueue()){
            if(packet.getUsername() != null && packet.getUsername().equalsIgnoreCase(e.getPlayer().getName())) {
                //Send the packet
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(stream);

                try {
                    out.writeUTF(packet.getSubChannel());
                    for(String s : packet.getData())
                        out.writeUTF(s);
                } catch (IOException ev) {
                    ev.printStackTrace();
                }

                BungeeCord.getInstance().getServerInfo(e.getPlayer().getServer().getInfo().getName()).sendData(packet.getChannel(), stream.toByteArray());

                toRemove.add(packet);
            }
        }

        for(ParadisePacket packet : toRemove)
            Main.getInstance().getParadisePacketManager().deleteUserPacket(packet);
    }

}
