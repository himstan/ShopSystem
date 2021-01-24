package hu.stan.shopsystem.strifeplugin;

import hu.stan.shopsystem.strifeplugin.commands.MainCommand;
import hu.stan.shopsystem.strifeplugin.configs.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public abstract class DreamPlugin extends JavaPlugin {

    private Map<String, MainCommand> mainCommands = new HashMap<>();
    private ConfigManager configManager = new ConfigManager(this);
    private CommandMap commandMap;

    /**
     * Registers a main command for the specified main command
     *
     * @param mainCommandName All the sub commands are going to start with this command
     * @param aliases The aliases this command is going to have
     * @return Returns true if the manager got registered successfully, false if not
     */

    public MainCommand registerMainCommand(String mainCommandName, List<String> aliases) {
        if (commandMap == null) {
            this.commandMap = getCommandMap();
        }
        Logger logger = Bukkit.getLogger();
        PluginCommand pluginCommand = getCommand(mainCommandName, this);
        if (mainCommandExists(mainCommandName) || pluginCommand == null) {
            return getMainCommand(mainCommandName);
        }
        logger.info("Registering main command: " + mainCommandName);
        MainCommand mainCommand = new MainCommand(this, mainCommandName);
        mainCommands.put(mainCommandName, mainCommand);
        pluginCommand.setExecutor(mainCommand);
        pluginCommand.setTabCompleter(mainCommand);
        if (aliases != null) {
            pluginCommand.setAliases(aliases);
        }
        pluginCommand.register(getCommandMap());
        getCommandMap().register(mainCommandName, pluginCommand);
        return mainCommand;
    }

    /**
     * Registers a main command for the specified main command
     *
     * @param mainCommandName All the sub commands are going to start with this command
     * @return Returns true if the manager got registered successfully, false if not
     */

    public MainCommand registerMainCommand(String mainCommandName) {
        return registerMainCommand(mainCommandName, null);
    }

    /**
     * Unregisters the specified main command
     *
     * @param mainCommandName The main command's name thats going to be unregistered
     */

    public void unregisterMainCommand(String mainCommandName) {
        if (mainCommandExists(mainCommandName)) {
            Command command = commandMap.getCommand(mainCommandName);
            if (command != null) {
                Bukkit.getLogger().info("Unregistering main command: " + mainCommandName);
                command.unregister(commandMap);
            }
            mainCommands.remove(mainCommandName);
        }
    }

    /**
     * Unregisters every main command
     */

    public void unregisterMainCommands() {
        for (String commandName : mainCommands.keySet()) {
            unregisterMainCommand(commandName);
        }
    }

    /**
     * Checks if the main command exists
     *
     * @param mainCommandName The main command's name
     * @return Returns true if the specified main command exists, false if it doesn't
     */

    public boolean mainCommandExists(String mainCommandName) {
        return mainCommands.containsKey(mainCommandName);
    }

    /**
     * Returns a main command for the specified main command
     * @param mainCommandName The main command's name
     * @return Returns the main command if it exists, null if it doesn't
     */

    public MainCommand getMainCommand(String mainCommandName) {
        return mainCommands.get(mainCommandName);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    private PluginCommand getCommand(String name, Plugin plugin) {
        PluginCommand command = null;
        try {
            Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            c.setAccessible(true);
            command = c.newInstance(name, plugin);
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return command;
    }

    /**
     * Gets the spigot CommandMap object from the server.
     * @return Returns the actual CommandMap the server uses.
     */

    private CommandMap getCommandMap() {
        CommandMap commandMap = null;
        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                Field f = SimplePluginManager.class.getDeclaredField("commandMap");
                f.setAccessible(true);
                commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return commandMap;
    }
}
