package me.chasertw123.minigames.bungee.utils;

import me.chasertw123.minigames.bungee.Main;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by Scott Hiett on 8/1/2017.
 */
public class YMLConfig {

    public static Configuration getConfig(String file){
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(Main.getInstance().getDataFolder(), file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
