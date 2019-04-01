package me.chasertw123.minigames.bungee.dss;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DynamicServerSystemManager {

    private List<MinigameServer> servers;

    public DynamicServerSystemManager() {
        this.servers = new ArrayList<>();
    }

    public List<MinigameServer> getServers() {
        return servers;
    }

    public void registerServer(MinigameServer minigameServer) {
        servers.add(minigameServer);
    }

    public void deleteServer(String serverId) {
        MinigameServer server = null;

        for(MinigameServer s : servers)
            if(s.getServerId().equalsIgnoreCase(serverId)) {
                server = s;

                break;
            }

        if(server == null)
            return;

        try {
            server.deleteServer();
            servers.remove(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
