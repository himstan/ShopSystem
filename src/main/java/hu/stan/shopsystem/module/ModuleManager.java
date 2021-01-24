package hu.stan.shopsystem.module;

import hu.stan.shopsystem.strifeplugin.DreamPlugin;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ModuleManager {

    private DreamPlugin plugin;
    private Logger logger;
    private Map<String, Module> registeredModules = new HashMap<>();

    public ModuleManager(DreamPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    private void enableModules() {
        for (Module module : getModules()) {
            enableModule(module);
        }
    }

    public void disableModules() {
        for (Module module : getModules()) {
            disableModule(module);
        }
    }

    public boolean enableModule(Module module) {
        if (!isModuleRegistered(module) || module.isEnabled() || !module.canBeEnabled()) {
            logger.warning("Failed to enable module (" + module.getModuleID() + ")!");
            return false;
        }
        plugin.getServer().getPluginManager().registerEvents(module, plugin);
        module.setEnabled(true);
        logger.info("Enabled module (" + module.getModuleID() + ")!");
        return true;
    }

    public boolean disableModule(Module module) {
        if (!module.isEnabled()) {
            return false;
        }
        HandlerList.unregisterAll(module);
        module.setEnabled(false);
        return true;
    }

    public boolean reloadModule(Module module) {
        if (!isModuleRegistered(module)) {
            return false;
        }
        disableModule(module);
        enableModule(module);
        return true;
    }

    public void reloadModules() {
        for (Module module : getModules()) {
            reloadModule(module);
        }
    }

    public Module registerModule(Module module) {
        String moduleID = module.getClass().getSimpleName();
        if (isModuleRegistered(module)) {
            logger.warning("Tried to register module with ID: " + moduleID + ", but it was already registered!");
            return null;
        }
        module.setPlugin(plugin);
        registeredModules.put(moduleID.toLowerCase(), module);
        enableModule(module);
        return module;
    }

    public Module unregisterModule(Module module) {
        return unregisterModule(module.getClass());
    }

    public Module unregisterModule(Class<? extends Module> moduleClass) {
        String moduleID = moduleClass.getSimpleName();
        if (!isModuleRegistered(moduleClass)) {
            logger.warning("Tried to unregister module with ID: " + moduleID + ", but it wasn't registered!");
            return null;
        }
        Module module = getModule(moduleClass);
        disableModule(module);
        registeredModules.remove(moduleID.toLowerCase());
        return module;
    }

    public void unregisterModules() {
        getModules().forEach(this::unregisterModule);
        // clear the map just to be sure
        registeredModules.clear();
    }

    public List<String> getModuleIDs() {
        return new ArrayList<>(registeredModules.keySet());
    }

    public List<Module> getModules() {
        return new ArrayList<>(registeredModules.values());
    }

    public <T extends Module> T getModule(Class<T> moduleClass) {
        Module module = registeredModules.get(moduleClass.getSimpleName().toLowerCase());
        if (module != null && module.getClass() == moduleClass) {
            return (T) module;
        }
        throw new IllegalStateException("This module class in not registered!");
    }

    public boolean isModuleRegistered(Module module) {
        return isModuleRegistered(module.getClass());
    }

    public boolean isModuleRegistered(Class<? extends Module> moduleClass) {
        String moduleID = moduleClass.getSimpleName();
        return registeredModules.containsKey(moduleID.toLowerCase());
    }

}
