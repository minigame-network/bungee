package me.chasertw123.minigames.bungee.packets;

import me.chasertw123.minigames.bungee.Main;
import net.md_5.bungee.BungeeCord;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Scott on 8/7/2017.
 *
 * Paradise Packet only exists on the server since it contains the one-way write data. (From the Bungee API perspective)
 */
public class ParadisePacket {

    private String channel, subChannel, server, username = null;
    private List<String> data;

    public ParadisePacket(String subChannel) {
        this("MCParadise", subChannel);
    }

    public ParadisePacket(String channel, String subChannel){
        this.channel = channel;
        this.subChannel = subChannel;
        this.data = new ArrayList<>();
    }

    public List<String> getData() {
        return data;
    }

    public String getChannel() {
        return channel;
    }

    public String getSubChannel() {
        return subChannel;
    }

    public ParadisePacket addLine(String data) {
        this.data.add(data);

        return this;
    }

    public String getUsername(){
        return username;
    }

    public String getServer() {
        return server;
    }

    public void queue(String server) {
        this.server = server;

        if (BungeeCord.getInstance().getServerInfo(server).getPlayers().size() > 0) {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(stream);

            try {
                out.writeUTF(subChannel);
                for(String s : data)
                    out.writeUTF(s);
            } catch (IOException e) {
                e.printStackTrace();
            }

            BungeeCord.getInstance().getServerInfo(server).sendData(channel, stream.toByteArray()); //send the packet to the server now!
        }

        else
            Main.getInstance().getParadisePacketManager().queuePacket(this); // add to queue
    }

    public void queueForPlayer(String name) {
        if (BungeeCord.getInstance().getPlayer(name) != null) {
            queue(BungeeCord.getInstance().getPlayer(name).getServer().getInfo().getName()); // they are online!
            return;
        }

        Main.getInstance().getParadisePacketManager().registerUserPacket(this); // add to queue
    }

}
