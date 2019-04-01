package me.chasertw123.minigames.bungee.queue.v2

enum class ServerState(val stateId: Int) {

    LOBBY(1), INGAME(2), RESTARTING(3), DELETE(4);

    companion object {

        fun fromId(id: Int): ServerState {
            for(ss in values())
                if(ss.stateId == id)
                    return ss

            return RESTARTING // This will make it so no one can join!
        }

    }

}