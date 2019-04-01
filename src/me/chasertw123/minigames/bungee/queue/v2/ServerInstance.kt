package me.chasertw123.minigames.bungee.queue.v2

import me.chasertw123.minigames.shared.framework.ServerGameType

data class ServerInstance(val name: String, var state: ServerState, val gameType: ServerGameType, var playerCount: Int, var maxPlayers: Int)