package me.chasertw123.minigames.bungee.user;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import me.chasertw123.minigames.bungee.Main;
import me.chasertw123.minigames.bungee.commands.BungeeCommand;
import me.chasertw123.minigames.bungee.packets.ParadisePacket;
import me.chasertw123.minigames.shared.database.Database;
import me.chasertw123.minigames.shared.infraction.Infraction;
import me.chasertw123.minigames.shared.infraction.Punishment;
import me.chasertw123.minigames.shared.infraction.PunishmentTimeScale;
import me.chasertw123.minigames.shared.infraction.PunishmentType;
import me.chasertw123.minigames.shared.rank.Rank;
import me.chasertw123.minigames.shared.rank.RankType;
import me.chasertw123.minigames.shared.user.iUser;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.Document;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Chase on 7/1/2017.
 *
 * No point in changing to Proxied since the other project is not within the scope of this project.
 */
public class User implements iUser {

    private String username;
    private UUID uuid, lastTalkedTo = null, partyInvite = null;
    private long lastAccessed, deluxe;
    private Rank rank;

    private List<Infraction> infractions;
    private List<Punishment> punishments;
    private List<UUID> friendRequests = new ArrayList<>(), ignoredPlayers = new ArrayList<>();

    /**
     * Creates a new User instance only use on check if the player is not in the database
     * @param player
     */
    public User(ProxiedPlayer player) {
        this.uuid = player.getUniqueId();
        this.username = player.getName();
        this.rank = Rank.MEMBER;
        this.infractions = new ArrayList<>();
        this.punishments = new ArrayList<>();
        this.deluxe = 0;
        this.lastAccessed = System.currentTimeMillis();
    }

    @SuppressWarnings("unchecked")
    public User(ProxiedPlayer player, Document userData, Document userInfractionsData, Document userPunishmentsData) {
        this.uuid = player.getUniqueId();
        this.username = player.getName();
        this.rank = Rank.valueOf(userData.getString("rank"));
        this.deluxe = userData.getLong("deluxe");
        ((List<String>) userData.get("ignored")).forEach(obj -> this.ignoredPlayers.add(UUID.fromString(obj)));
        this.infractions = (userInfractionsData == null ? new ArrayList<>() : Infraction.serializeInfractions(player.getUniqueId(), userInfractionsData));
        this.punishments = (userPunishmentsData == null ? new ArrayList<>() : Punishment.serializePunishments(player.getUniqueId(), userPunishmentsData));
        this.lastAccessed = System.currentTimeMillis();

        for (Punishment punishment : punishments)
            if (punishment.getType() == PunishmentType.BAN && (punishment.getDateIssued() + punishment.getTimeScale().getUnixTime()) > System.currentTimeMillis())
                getPlayer().disconnect(TextComponent.fromLegacyText(ChatColor.RED + "You're Banned! Your ban " + (punishment.getTimeScale() == PunishmentTimeScale.PERMANENT ? "is " + ChatColor.YELLOW
                        + "PERMANENT" : ChatColor.YELLOW + "EXPIRES" + ChatColor.RED + " in " + ChatColor.YELLOW + punishment.getTimeRemaining()) + ChatColor.RED + "."));
    }

    public UUID getPartyInvite() {
        return partyInvite;
    }

    public void setPartyInvite(UUID partyInvite) {
        this.partyInvite = partyInvite;
    }

    public boolean hasPartyInvite() {
        return partyInvite != null;
    }

    public UUID getLastTalkedTo() {
        return lastTalkedTo;
    }

    public void setLastTalkedTo(UUID lastTalkedTo) {
        this.lastTalkedTo = lastTalkedTo;
    }

    public List<UUID> getIgnoredPlayers() {
        return ignoredPlayers;
    }

    /**
     * Returns the time in which the player's deluxe subscription ends.
     * @return The time in which the player's deluxe subscription ends.
     */
    @Override
    public long getDeluxe() {
        return deluxe;
    }

    /**
     * Returns if the player is Deluxe or not. Checks the time left is bigger than the current time.
     * @return if the player is Deluxe or not.
     */
    @Override
    public boolean isDeluxe() {
        return deluxe > System.currentTimeMillis() || rank.getRankType() == RankType.STAFF;
    }

    /**
     * Add a punishment to the player. Note: this doesn't save the punishment to SQL.
     * @param punishment The Punishment to be added.
     */
    public void addPunishment(Punishment punishment) {
        this.punishments.add(punishment);
    }

    /**
     * Gets a list of the Player's Punishments.
     * @return the list of Punishments that the player has gained over time.
     */
    @Override
    public List<Punishment> getPunishments() {
        return punishments;
    }

    /**
     * Gets a list of the Player's Infractions.
     * @return the list of Infractions that the player has gained over time.
     */
    @Override
    public List<Infraction> getInfractions() {
        return infractions;
    }

    /**
     * Returns a list of Friend Requests that the player has gained.
     * What a lucky person.
     * They have friends.
     *
     * Some of us don't
     * @return A list of Friend Requests that the player has.
     */
    public List<UUID> getFriendRequests() {
        return friendRequests;
    }

    /**
     * Adds a friend request to the User, and sends a packet to their local server informing it of their
     * new request.
     * @param from the Player who send the friend request.
     */
    public void addFriendRequest(UUID from) {
        friendRequests.add(from);

        //Send a packet down to the players server telling them they have a friend request (the message needs to be clickable)
        new ParadisePacket("NewFriendRequest")
                .addLine(getPlayer().getName() + "") // to
                .addLine(get(from).getPlayer().getName() + "") // from
                .queueForPlayer(getPlayer().getName()); // queue for the player's server
    }

    public void removeFriendRequest(UUID uuid) {
        friendRequests.remove(uuid);
    }

    /**
     * Gets the {@link ProxiedPlayer} instance of the User.
     * @return the {@link ProxiedPlayer} instance of the User.
     */
    public ProxiedPlayer getPlayer(){
        return BungeeCord.getInstance().getPlayer(uuid);
    }

    /**
     * Sets the rank of the User
     * @param rank the Rank that it should be set to
     */
    @Override
    public void setRank(Rank rank){
        this.rank = rank;
    }

    /**
     * Gets the {@link Rank} of the User.
     * @return the Rank of the User.
     */
    public Rank getRank(){
        return rank;
    }

    /**
     * Gets the UUID of the User
     * @return the UUID of the User
     */
    @Override
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Gets the Username of the User. Be warned that if they're online, this may need to be updated.
     * @return The Username of the User.
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Checks if the User is Online. It loops through all players and checks if the UUID matches.
     * @return if this instance of User is online.
     */
    public boolean isOnline(){
        for (ProxiedPlayer pp : BungeeCord.getInstance().getPlayers())
            if (pp.getUniqueId() == uuid)
                return true;

        return false;
    }

    /**
     * Sends this instance of User to a server.
     * @param server the server that this instance of User should be sent to.
     */
    public void connect(String server) {

        ProxiedPlayer proxiedPlayer = BungeeCord.getInstance().getPlayer(username);
        if (proxiedPlayer != null) {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(stream);

            try {
                out.writeUTF("MCParadise");
                out.writeUTF("SendServer");
                out.writeUTF(username);
                out.writeUTF(server);
            } catch (IOException e) {
                e.printStackTrace();
            }

            proxiedPlayer.getServer().sendData("MCParadise", stream.toByteArray());
        }
    }

    public void sendMessage(String message) {
        getPlayer().sendMessage(TextComponent.fromLegacyText(message));
    }

    public boolean saveUserData(boolean all) {

        Document userData = new Document();
        ArrayList<String> ignored = new ArrayList<>();

        this.getIgnoredPlayers().forEach(obj -> ignored.add(obj.toString()));

        userData.append("uuid", uuid.toString())
                .append("lastknownusername", username)
                .append("rank", rank.toString())
                .append("deluxe", deluxe)
                .append("ignored", ignored);

        Main.getInstance().getDatabase().getMongoCollection(Database.Collection.USERS).replaceOne(Filters.eq("uuid", uuid.toString()), userData, new UpdateOptions().upsert(true));

        if (all) {
            Main.getInstance().getDatabase().getMongoCollection(Database.Collection.INFRACTIONS).replaceOne(Filters.eq("uuid", uuid.toString()), Infraction.parseInfractions(uuid, infractions), new UpdateOptions().upsert(true));
            Main.getInstance().getDatabase().getMongoCollection(Database.Collection.PUNISHMENTS).replaceOne(Filters.eq("uuid", uuid.toString()), Punishment.parsePunishments(uuid, punishments), new UpdateOptions().upsert(true));
        }

        return true; // TODO: TEMP
    }

    // Static //

    /**
     * Returns an instance of User based on the ProxiedPlayer
     * @param p the player that the instance should be of
     * @return an instance of User that matches the player.
     */
    public static User get(ProxiedPlayer p){
        return get(p.getUniqueId());
    }

    /**
     * Returns an instance of User based on the UUID.
     * @param uuid the player that the instance should be of
     * @return an instance of User that matches the player.
     */
    public static User get(UUID uuid) {
        return Main.getInstance().getUserManager().getParadisePlayer(uuid);
    }

}
