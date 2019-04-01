package me.chasertw123.minigames.bungee.queue.v2

import me.chasertw123.minigames.shared.framework.ServerGameType
import java.util.*

data class QueueGroup(val type: ServerGameType, var players: ArrayList<UUID>)