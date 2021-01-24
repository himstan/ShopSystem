package hu.stan.shopsystem.strifeplugin.configs.subconfigs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class SubConfig {

    protected String configName;
    private File file;
    protected FileConfiguration config;
    protected JavaPlugin plugin;

    public SubConfig(JavaPlugin plugin, String configName) {
        this.plugin = plugin;
        this.configName = configName;
        setupConfig();
    }

    private void setupConfig(){
        file = new File(plugin.getDataFolder(), configName + ".yml");
        if (!file.exists()) {
            plugin.saveResource(configName + ".yml", false);
        }
        file = new File(plugin.getDataFolder(), configName + ".yml");
        if (!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
                plugin.getLogger().warning("Couldn't read " + configName + ".yml");
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig(){
        return config;
    }

    public void saveConfig(){
        try{
            config.save(file);
        }catch (IOException e){
            plugin.getLogger().warning("Couldn't save " + configName + ".yml");
        }
        reloadConfig();
    }

    public void reloadConfig(){
        config = YamlConfiguration.loadConfiguration(file);
    }
}
