package me.chasertw123.minigames.bungee.parties

import me.chasertw123.minigames.bungee.user.User
import net.md_5.bungee.api.ChatColor
import java.util.*

class PartyManager {

    val parties = mutableListOf<Party>()

    fun createParty(owner: UUID, firstMember: UUID): Party {
        val list = mutableListOf<UUID>()
        list.add(firstMember)
        val party = Party(owner, list)

        parties.add(party)

        return party
    }

    fun isInParty(user: User): Boolean {
        for(party in parties)
            if(party.getFullPartyArray().contains(user.uuid))
                return true

        return false
    }

    fun isPartyLeader(user: User): Boolean {
        for(party in parties)
            if(party.owner == user.uuid)
                return true

        return false
    }

    fun getParty(user: User): Party? {
        for(party in parties)
            if(party.getFullPartyArray().contains(user.uuid))
                return party

        return null
    }

    fun removeFromParty(user: User) {
        val party = getParty(user)

        if(party == null)
            return

        if(isPartyLeader(user)) {
            // Disband the party.
            party.sendPartyOverMessage()
            parties.remove(party)
        } else {
            party.members.remove(user.uuid)
            user.sendMessage(ChatColor.RED + "You have left your former party-mates to party alone.")
        }
    }

}