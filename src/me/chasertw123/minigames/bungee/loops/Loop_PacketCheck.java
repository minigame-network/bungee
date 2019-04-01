package me.chasertw123.minigames.bungee.loops;

import me.chasertw123.minigames.bungee.Main;
import me.chasertw123.minigames.bungee.packets.ParadisePacket;
import net.md_5.bungee.BungeeCord;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Scott Hiett on 8/7/2017.
 */
public class Loop_PacketCheck {

    /**
     * Checks and updates all queued packets. Will only send to a server if the player count is larger than 0.
     */
    public Loop_PacketCheck() {
        BungeeCord.getInstance().getScheduler().schedule(Main.getInstance(), ()  -> {

            List<ParadisePacket> toRemove = new ArrayList<>();
            for (ParadisePacket paradisePacket : Main.getInstance().getParadisePacketManager().getPacketQueue())
                if (BungeeCord.getInstance().getServerInfo(paradisePacket.getServer()).getPlayers().size() > 0) {

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(stream);

                    try {
                        out.writeUTF(paradisePacket.getSubChannel());
                        for(String s : paradisePacket.getData())
                            out.writeUTF(s);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    BungeeCord.getInstance().getServerInfo(paradisePacket.getServer()).sendData(paradisePacket.getChannel(), stream.toByteArray());
                    toRemove.add(paradisePacket);
                }

            for (ParadisePacket pp : toRemove)
                Main.getInstance().getParadisePacketManager().deletePacket(pp);

        }, 0, 1, TimeUnit.SECONDS);
    }

}
