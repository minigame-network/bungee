package me.chasertw123.minigames.bungee.boosters;

import me.chasertw123.minigames.bungee.Main;
import me.chasertw123.minigames.bungee.loops.Loop_BoosterCheck;
import me.chasertw123.minigames.shared.booster.EventBooster;
import me.chasertw123.minigames.shared.booster.GameBooster;
import me.chasertw123.minigames.shared.database.Database;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Chase on 8/31/2017.
 */
public class BoosterManager {

    private EventBooster currentEventBooster;
    private List<GameBooster> currentGameBoosters;

    public BoosterManager() {

        this.currentGameBoosters = new ArrayList<>();

        for(Document eventBooster : getDatabase().getMongoCollection(Database.Collection.EVENT_BOOSTERS).find())
            if(eventBooster.getLong("endtime") > System.currentTimeMillis())
                setCurrentEventBooster(new EventBooster(eventBooster.getLong("starttime"), eventBooster.getLong("endtime"),
                        eventBooster.getInteger("multiplier"), eventBooster.getString("reason")));

        for(Document gameBooster : getDatabase().getMongoCollection(Database.Collection.GAME_BOOSTERS).find())
            if(gameBooster.getLong("endtime") > System.currentTimeMillis())
                addGameBooster(new GameBooster(gameBooster.getLong("starttime"), gameBooster.getLong("endtime"),
                        gameBooster.getInteger("multiplier"), UUID.fromString(gameBooster.getString("activatoruuid")),
                        gameBooster.getString("activatorname"), gameBooster.getString("gamemode")));

        new Loop_BoosterCheck();
    }

    private Database getDatabase() {
        return Main.getInstance().getDatabase();
    }

    public void removeGameBooster(GameBooster gameBooster){
        currentGameBoosters.remove(gameBooster);
    }

    public void removeCurrentEventBooster(){
        currentEventBooster = null;
    }

    public boolean isEventBoosterActive(){
        return currentEventBooster != null;
    }

    public EventBooster getCurrentEventBooster() {
        return currentEventBooster;
    }

    public void setCurrentEventBooster(EventBooster currentEventBooster) {
        this.currentEventBooster = currentEventBooster;
        Main.getInstance().sendPacketToAllServers("CheckBoosters");
    }

    public void addGameBooster(GameBooster gameBooster){
        currentGameBoosters.add(gameBooster);
        Main.getInstance().sendPacketToAllServers("CheckBoosters");
    }

    public boolean isAnyGameBoosterActive(){
        return !currentGameBoosters.isEmpty();
    }

    public List<GameBooster> getCurrentGameBoosters(){
        return currentGameBoosters;
    }

}
