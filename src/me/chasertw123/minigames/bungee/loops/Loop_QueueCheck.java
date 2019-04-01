package me.chasertw123.minigames.bungee.loops;

import com.mongodb.client.model.Filters;
import me.chasertw123.minigames.bungee.Main;
import me.chasertw123.minigames.bungee.queue.v2.QueueGroup;
import me.chasertw123.minigames.bungee.queue.v2.ServerInstance;
import me.chasertw123.minigames.shared.database.Database;
import me.chasertw123.minigames.shared.framework.ServerGameType;
import net.md_5.bungee.BungeeCord;
import org.bson.Document;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Created by Scott Hiett on 8/1/2017.
 */
public class Loop_QueueCheck {

    /**
     * Updates all of the GameQueues to check for available matches.
     */
    public Loop_QueueCheck(){
        BungeeCord.getInstance().getScheduler().schedule(Main.getInstance(), () -> {
            // New Queue Controller!
            Main.getInstance().getQueueController().processQueue();

            // Update the data for the player counts in queue and games to the server.
            Arrays.asList(ServerGameType.values()).forEach(serverType -> {
                Document data = new Document();

                int count = 0;

                for(ServerInstance inst : Main.getInstance().getQueueController().getServers())
                    if(inst.getGameType() == serverType)
                        count += BungeeCord.getInstance().getServerInfo(inst.getName()).getPlayers().size();

                for(QueueGroup queueGroup : Main.getInstance().getQueueController().getQueueLoads())
                    if(queueGroup.getType() == serverType)
                        count += queueGroup.getPlayers().size();

                data.put("serverType", serverType.toString());
                data.put("playerCount", count);

                Main.getInstance().getDatabase().getMongoCollection(Database.Collection.PLAYER_COUNTS)
                        .replaceOne(Filters.eq("serverType", serverType.toString()), data, Database.upsert());
            });
        }, 0, 5, TimeUnit.SECONDS);
    }

}
