package me.chasertw123.minigames.bungee.parties

import me.chasertw123.minigames.bungee.user.User
import net.md_5.bungee.api.ChatColor
import java.util.*

data class Party(var owner: UUID, var members: MutableList<UUID>) {

    fun sendPartyOverMessage() {
        getFullPartyArray().forEach {
            val usr = User.get(it)

            if(usr != null)
                usr.player.sendMessage("Sorry kids, cops are here. Party's over.")
        }
    }

    fun getFullPartyArray(): List<UUID> {
        val mem = mutableListOf<UUID>()
        mem.addAll(members)
        mem.add(owner)

        return mem
    }

    fun addMember(uuid: UUID) {
        members.add(uuid)
    }

    fun broadcastMessage(string: String) {
        getFullPartyArray().forEach { User.get(it).sendMessage(string) }
    }

}