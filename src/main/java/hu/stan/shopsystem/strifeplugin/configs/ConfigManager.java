package hu.stan.shopsystem.strifeplugin.configs;


import hu.stan.shopsystem.strifeplugin.configs.subconfigs.SubConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private JavaPlugin plugin;
    private Map<String, SubConfig> subConfigs = new HashMap<>();

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Registers a subconfig with the specified name
     * @param configName A config will be crated under this name
     * @return Returns true if the config was created successfully, false if not
     */

    public boolean addSubConfig(String configName) {
        if (subConfigExists(configName)) {
            return false;
        }
        SubConfig subConfig = new SubConfig(plugin, configName);
        subConfigs.put(configName.toLowerCase(), subConfig);
        return true;
    }

    /**
     * Registers a custom subconfig with the specified name
     * @param configName A config will be created under this name
     * @param customConfig A custom config object if you'd like to use some custom functionality
     * @return Returns true if the config was created successfully, false if not
     */

    public boolean addSubConfig(String configName, SubConfig customConfig) {
        if (subConfigExists(configName)) {
            return false;
        }
        subConfigs.put(configName.toLowerCase(), customConfig);
        return true;
    }

    /**
     * Checks if the specified config exists
     * @param configName The config's name you'd like to check
     * @return True if the config exists, false if it doesn't
     */

    public boolean subConfigExists(String configName) {
        return subConfigs.containsKey(configName.toLowerCase());
    }

    /**
     *
     * @param configName The config's name you're trying to get
     * @return Returns the subconfig if the config exists, null if it doesn't
     */

    public SubConfig getSubConfig(String configName) {
        return subConfigs.get(configName.toLowerCase());
    }

    /**
     * Reloads a subconfig
     * @param configName The sub config's name you're trying to relaod
     * @return Returns true if the config was reloaded successfully, false if it was not
     */

    public boolean reloadConfig(String configName) {
        if (subConfigExists(configName)) {
            getSubConfig(configName).reloadConfig();
            return true;
        }
        return false;
    }

    /**
     * Reloads every subconfig
     */

    public void reloadConfigs() {
        for (SubConfig subConfig : getSubConfigs()) {
            subConfig.reloadConfig();
        }
    }

    /**
     * Gets every subconfig in a list
     * @return Returns a list that contains every subconfig
     */

    public List<SubConfig> getSubConfigs() {
        return new ArrayList<>(subConfigs.values());
    }

}
