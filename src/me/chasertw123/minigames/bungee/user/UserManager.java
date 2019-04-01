package me.chasertw123.minigames.bungee.user;

import com.mongodb.client.model.Filters;
import me.chasertw123.minigames.bungee.Main;
import me.chasertw123.minigames.shared.database.Database;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.Document;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Chase on 7/25/2017.
 */
public class UserManager {

    private HashMap<UUID, User> cachedPlayers = new HashMap<>();

    /**
     * Returns a cached User. Keep in mind that the ParadisePlayers inside the Bungee plugin
     * differ from those in the Main Core. The Bungee version resembles the OfflineParadisePlayer of the main core -
     * (it doesn't contain Mongo data).
     * @param uuid The UUID of the player to load from the cache.
     * @return The instance of User that matches the UUID given.
     */
    public User getParadisePlayer(UUID uuid){
        return cachedPlayers.getOrDefault(uuid, null);
    }

    /**
     * Returns a cached User. Keep in mind that the ParadisePlayers inside the Bungee plugin
     * differ from those in the Main Core. The Bungee version resembles the OfflineParadisePlayer of the main core -
     * (it doesn't contain Mongo data).
     * @param pp The ProxiedPlayer instance of the Player.
     * @return The instance of User that matches the ProxiedPlayer's UUID.
     */
    public User getParadisePlayer(ProxiedPlayer pp){
        return getParadisePlayer(pp.getUniqueId());
    }

    /**
     * Adds a User to the cache. Keep in mind that ParadisePlayers should be removed when a player leaves the server.
     * @param player the instance of User that will be loaded or created from.
     */
    public void add(ProxiedPlayer player) {

        Document userData = Main.getInstance().getDatabase().getMongoCollection(Database.Collection.USERS).find(Filters.eq("uuid", player.getUniqueId().toString())).first();

        if (userData == null) {
            cachedPlayers.put(player.getUniqueId(), new User(player));
            cachedPlayers.get(player.getUniqueId()).saveUserData(true);

            return;
        }

        Document userInfractionsData = Main.getInstance().getDatabase().getMongoCollection(Database.Collection.INFRACTIONS).find(Filters.eq("uuid", player.getUniqueId().toString())).first();
        Document userPunishmentsData = Main.getInstance().getDatabase().getMongoCollection(Database.Collection.PUNISHMENTS).find(Filters.eq("uuid", player.getUniqueId().toString())).first();

        cachedPlayers.put(player.getUniqueId(), new User(player, userData, userInfractionsData, userPunishmentsData));
    }

    /**
     * Removes a User from cache. (Mainly used when they leave)
     * @param uuid The UUID of the ProxiedPlayer that matches the UUID of the User that should be removed.
     */
    public void remove(UUID uuid) {
        cachedPlayers.remove(uuid);
    }

}
