package me.chasertw123.minigames.bungee.packets;

import me.chasertw123.minigames.bungee.loops.Loop_PacketCheck;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chase on 8/31/2017.
 */
public class ParadisePacketManager {

    private List<ParadisePacket> packetQueue = new ArrayList<>(), packetUserQueue = new ArrayList<>();

    public ParadisePacketManager() {
        new Loop_PacketCheck();
    }

    public List<ParadisePacket> getPacketUserQueue() {
        return packetUserQueue;
    }

    public void registerUserPacket(ParadisePacket pp) {
        this.packetUserQueue.add(pp);
    }

    public void deleteUserPacket(ParadisePacket pp) {
        this.packetUserQueue.remove(pp);
    }

    public List<ParadisePacket> getPacketQueue(){
        return packetQueue;
    }

    public void queuePacket(ParadisePacket paradisePacket){
        this.packetQueue.add(paradisePacket);
    }

    public void deletePacket(ParadisePacket paradisePacket){
        this.packetQueue.remove(paradisePacket);
    }

}
