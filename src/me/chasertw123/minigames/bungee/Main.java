package me.chasertw123.minigames.bungee;

import com.google.common.io.ByteStreams;
import me.chasertw123.minigames.bungee.boosters.BoosterManager;
import me.chasertw123.minigames.bungee.commands.CommandManager;
import me.chasertw123.minigames.bungee.dss.DynamicServerSystemManager;
import me.chasertw123.minigames.bungee.dss.MinigameServer;
import me.chasertw123.minigames.bungee.events.EventManager;
import me.chasertw123.minigames.bungee.loops.Loop_QuestCheck;
import me.chasertw123.minigames.bungee.loops.Loop_QueueCheck;
import me.chasertw123.minigames.bungee.packets.ParadisePacket;
import me.chasertw123.minigames.bungee.packets.ParadisePacketManager;
import me.chasertw123.minigames.bungee.parties.PartyManager;
import me.chasertw123.minigames.bungee.queue.v2.QueueController;
import me.chasertw123.minigames.bungee.user.UserManager;
import me.chasertw123.minigames.shared.config.ServerConfiguration;
import me.chasertw123.minigames.shared.database.ChatLog;
import me.chasertw123.minigames.shared.database.Database;
import me.chasertw123.minigames.shared.framework.ServerGameType;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class Main extends Plugin {

    public static final String STARTER = "" + ChatColor.RESET;
    private static Main plugin;

    private Database database;
    private ServerConfiguration serverConfiguration;
    private UserManager userManager;
    private ParadisePacketManager paradisePacketManager;
    private QueueController queueController;
    private BoosterManager boosterManager;
    private PartyManager partyManager;
    private ChatLog chatLog;
    private DynamicServerSystemManager dynamicServerSystemManager;

    @Override
    public void onEnable() {
        plugin = this;
        serverConfiguration = new ServerConfiguration();

        database = new Database(serverConfiguration);
        chatLog = new ChatLog(database);

        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                try (InputStream is = getResourceAsStream("config.yml");
                     OutputStream os = new FileOutputStream(configFile)) {
                     ByteStreams.copy(is, os);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create configuration file", e);
            }
        }

        new CommandManager();
        new EventManager();

        this.getProxy().registerChannel("MCParadise");

        userManager = new UserManager();
        paradisePacketManager = new ParadisePacketManager();
        queueController = new QueueController();
        boosterManager = new BoosterManager();
        partyManager = new PartyManager();
        dynamicServerSystemManager = new DynamicServerSystemManager();

//        new Loop_BuycraftCheck();
        new Loop_QuestCheck();
        new Loop_QueueCheck();
    }

    @Override
    public void onDisable() {
        // Delete all dynamic server system instances and clear the folder.
        try {
            System.out.println("Cleaning Dynamic Server System Processes.");
            for (MinigameServer minigameServer : dynamicServerSystemManager.getServers())
                minigameServer.deleteServer();

            // Clean the output directory.
            FileUtils.deleteDirectory(new File("../minigameservers"));
            new File("../minigameservers").mkdirs();
        } catch (IOException e) {
            e.printStackTrace();
        }

        plugin = null;
    }

    public DynamicServerSystemManager getDynamicServerSystemManager() {
        return dynamicServerSystemManager;
    }

    public ChatLog getChatLog() {
        return chatLog;
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }

    public ServerConfiguration getServerConfiguration() {
        return serverConfiguration;
    }

    /**
     * Returns the UserManager. This class contains a map of all the online ParadisePlayers.
     * Keep in mind that the ParadisePlayers provided are the same as the core's OfflineParadisePlayer,
     * as they only contain SQL data (not Mongo).
     * @return UserManager that contains a map of ParadisePlayers
     */
    public UserManager getUserManager() {
        return userManager;
    }

    /**
     * Returns the ParadisePacketManager. This class is used as part of {@link ParadisePacket}, and
     * contains all of the cached packets (ones that are waiting for their target server to be occupied).
     * @return ParadisePacketManager that contains cached Packets.
     */
    public ParadisePacketManager getParadisePacketManager() {
        return paradisePacketManager;
    }

    /**
     * Returns the QueueController. This class manages the Queue system, which includes the methods
     * to add players to a certain gamemode queue.
     * @return QueueController that contains GameMode queues.
     */
    public QueueController getQueueController() {
        return queueController;
    }

    /**
     * The BoosterManager contains information about the currently active boosters. This includes both
     * {@link me.chasertw123.minigames.shared.booster.GameBooster} and {@link me.chasertw123.minigames.shared.booster.EventBooster}.
     * @return an instance of BoosterManager that contains currently active boosters.
     */
    public BoosterManager getBoosterManager() {
        return boosterManager;
    }

    public Database getDatabase() {
        return database;
    }

    /**
     * This method sends a Packet to all of the online servers. This includes waiting for one player to join
     * {@link ParadisePacket}. A use for this would be if you needed all online servers to update or check something.
     * (For example, boosters).
     * @param name The name of the Packet.
     */
    public void sendPacketToAllServers(String name) {
        for (ServerInfo serverInfo : BungeeCord.getInstance().getServers().values())
            new ParadisePacket(name).queue(serverInfo.getName());
    }

    /**
     * Returns the singleton instance of ParadiseBungee class. This also contains all of the {@link Plugin} data.
     * @return The instance of the Plugin & ParadiseBungee.
     */
    public static Main getInstance() {
        return plugin;
    }

}
