package com.exortions.disclink.utilities;

import com.exortions.disclink.DiscLink;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {

    public static Object get(String path) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(DiscLink.getPlugin().getDataFolder() + File.separator + "config.yml"));
        return config.get(path);
    }

    public static String getString(String path) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(DiscLink.getPlugin().getDataFolder() + File.separator + "config.yml"));
        return config.getString(path);
    }

    public static boolean getBoolean(String path) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(DiscLink.getPlugin().getDataFolder() + File.separator + "config.yml"));
        return config.getBoolean(path);
    }

    public static int getInt(String path) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(DiscLink.getPlugin().getDataFolder() + File.separator + "config.yml"));
        return config.getInt(path);
    }

    public static long getLong(String path) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(DiscLink.getPlugin().getDataFolder() + File.separator + "config.yml"));
        return config.getLong(path);
    }

    public static void set(String path, Object value) {
        File file = new File(DiscLink.getPlugin().getDataFolder() + File.separator + "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(path, value);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
