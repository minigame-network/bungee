package me.chasertw123.minigames.bungee.queue.v2

import me.chasertw123.minigames.bungee.Main
import me.chasertw123.minigames.bungee.dss.MinigameServer
import me.chasertw123.minigames.bungee.user.User
import me.chasertw123.minigames.bungee.utils.YMLConfig
import me.chasertw123.minigames.shared.database.Database
import me.chasertw123.minigames.shared.framework.ServerGameType
import net.md_5.bungee.BungeeCord

class QueueController {

    var queueLoads = mutableListOf<QueueGroup>()

    var servers = mutableListOf<ServerInstance>()
    var gameModes = mutableListOf<String>() // The gamemode list

    init {
        // Load in data from config files
        val config = YMLConfig.getConfig("config.yml")

        gameModes = config.getStringList("gamemodes")

        gameModes.forEach {
            // Get each server
            config.getStringList("servers.$it").forEach { serverItem ->
                servers.add(ServerInstance(serverItem, ServerState.RESTARTING, ServerGameType.valueOf(it.toUpperCase().replace("-", "_")), 0, 0))
            }
        }
    }

    // Queue functions
    fun addToQueue(group: QueueGroup) {
        queueLoads.add(group)

        // Check if the current server list contains a server that is of the type these players want.
        var found = false

        println("Checking ${servers.size} servers.")

        for(s in servers)
            if(s.gameType == group.type && (s.state == ServerState.LOBBY || s.state == ServerState.RESTARTING)) { // Restarting because it may be a starting dynamic server
                found = true
                break
            }

        if(found)
            return

        println("Creating a new server with the gamemode of ${group.type} because one doesn't currently exist.")
        // Create a new Minigame Server with this gamemode.
        Main.getInstance().dynamicServerSystemManager.registerServer(MinigameServer(group.type))
    }

    fun removeFromQueue(group: QueueGroup) {
        queueLoads.remove(group)
    }

    fun removeServer(serverName: String) {
        var toRemove: ServerInstance? = null

        for(s in servers) {
            if (s.name == serverName) {
                toRemove = s

                break
            }
        }

        if(toRemove != null)
            servers.remove(toRemove)
    }

    fun removeFromQueue(user: User) {
        // Get what queue load they are in.
        var queueGroup: QueueGroup? = null

        for(gq in queueLoads)
            for(uuid in gq.players)
                if(user.uuid == uuid)
                    queueGroup = gq

        if(queueGroup == null)
            return

        if(queueGroup.players.size - 1 <= 0)
            removeFromQueue(queueGroup)
        else
            queueGroup.players.remove(user.uuid)
    }

    fun isInQueue(user: User): Boolean {
        for(qg in queueLoads)
            for(uuid in qg.players)
                if(user.uuid == uuid)
                    return true

        return false
    }

    fun processQueue() {
        // Download all of the server information
        val toDelete = mutableListOf<ServerInstance>()

        for(d in Main.getInstance().database.getMongoCollection(Database.Collection.SERVER_STATUS).find()) {
            // Find the server
            servers.filter { it.name.equals(d.getString("servername"), true) }.forEach {
                it.maxPlayers = d.getInteger("maxplayers")
                it.playerCount = d.getInteger("currentplayers")
                it.state = ServerState.fromId(d.getInteger("status"))

                // Delete this server
                if(it.state == ServerState.DELETE)
                    toDelete.add(it)
            }
        }

        toDelete.forEach {
            Main.getInstance().dynamicServerSystemManager.deleteServer(it.name)
            servers.remove(it)
        }

        val toRemove = mutableListOf<QueueGroup>()

        // Iterate through the queue and distribute from this.
        queueLoads.forEach {
            var foundServer = false

            // Filter down to only what is joinable for the entire group, and matches their search.
            val possibleServers = servers.filter { server -> server.gameType == it.type
                    && server.maxPlayers - server.playerCount >= it.players.size && server.state === ServerState.LOBBY }

            if(possibleServers.isNotEmpty()) {
                foundServer = true

                it.players.forEach { uuid ->
                    BungeeCord.getInstance().getPlayer(uuid).connect(BungeeCord.getInstance()
                            .getServerInfo(possibleServers[0].name))
                }

                possibleServers[0].playerCount += it.players.size // Update it for next iteration.
            }

            if(foundServer)
                toRemove.add(it)
        }

        toRemove.forEach {
            queueLoads.remove(it)
        }
    }

}