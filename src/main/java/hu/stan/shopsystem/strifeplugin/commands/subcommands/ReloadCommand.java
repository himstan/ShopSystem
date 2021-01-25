package hu.stan.shopsystem.strifeplugin.commands.subcommands;

import hu.stan.shopsystem.ShopSystem;
import hu.stan.shopsystem.strifeplugin.commands.SubCommand;
import hu.stan.shopsystem.strifeplugin.utils.TextUtil;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ReloadCommand extends SubCommand {

    private ShopSystem plugin;

    public ReloadCommand(String commandName) {
        super(commandName);
        this.plugin = (ShopSystem) getPlugin();
    }

    @Override
    public void execute(Player player, String commandName, String label, String[] args) {
        long start = System.currentTimeMillis();
        plugin.init();
        plugin.getConfigManager().reloadConfigs();
        plugin.getModuleManager().reloadModules();
        long end = System.currentTimeMillis();
        TextUtil.sendPrefixMessage(player, String.format("&6Plugin was reloaded in &f%d&6ms.", (end - start)));
    }

    @Override
    public List<String> tabComplete(Player player, String label, String[] args) {
        return Collections.emptyList();
    }
}
