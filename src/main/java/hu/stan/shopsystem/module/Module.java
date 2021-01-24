package hu.stan.shopsystem.module;

import hu.stan.shopsystem.strifeplugin.DreamPlugin;
import hu.stan.shopsystem.strifeplugin.configs.subconfigs.SubConfig;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Module implements Listener {

    protected DreamPlugin plugin;
    private boolean isEnabled = false;
    private SubConfig subConfig;

    abstract protected void onEnable();

    abstract protected void onDisable();

    public String getModuleID() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    public boolean canBeEnabled() {
        return true;
    }

    public void setEnabled(boolean enabled) {
        if (isEnabled() && !enabled) {
            onDisable();
        } else if (!isEnabled && enabled) {
            onEnable();
        }
        this.isEnabled = enabled;
    }

    public  <T extends Module> T convertToSubModule(Class<T> tClass) {
        T result = null;
        if (this.getClass() == tClass) result = (T) this;
        return result;
    }

    protected void createConfig() {
        String configName = getConfigName();
        if (configName == null) return;
        plugin.getConfigManager().addSubConfig(configName);
    }

    protected boolean hasConfig() {
        String configName = getConfigName();
        if (configName == null) return false;
        return plugin.getConfigManager().subConfigExists(configName);
    }

    protected SubConfig getSubConfig() {
        String configName = getConfigName();
        if (configName == null) return null;
        return plugin.getConfigManager().getSubConfig(configName);
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public Module setPlugin(DreamPlugin plugin) {
        this.plugin = plugin;
        return this;
    }

    private String getConfigName() {
        String moduleID = getModuleID();
        if (moduleID == null) {
            return null;
        }
        return "module_" + moduleID.toLowerCase() + "_config";
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
