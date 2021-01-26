package hu.stan.shopsystem;

import hu.stan.shopsystem.controller.ClaimController;
import hu.stan.shopsystem.module.ModuleManager;
import hu.stan.shopsystem.module.modules.ShopHandlerModule;
import hu.stan.shopsystem.module.modules.SignHandlerModule;
import hu.stan.shopsystem.strifeplugin.DreamPlugin;
import hu.stan.shopsystem.strifeplugin.commands.subcommands.*;
import hu.stan.shopsystem.strifeplugin.utils.TextUtil;
import net.md_5.bungee.api.ChatColor;

public final class ShopSystem extends DreamPlugin {

    private ModuleManager moduleManager;
    private ShopStorage shopStorage;
    private PlayerStorage playerStorage;
    private ClaimController claimController;

    @Override
    public void onEnable() {

        getConfigManager().addSubConfig("config");
        getConfigManager().addSubConfig("signconfig");

        init();

        shopStorage = new ShopStorage();
        playerStorage = new PlayerStorage(this);
        claimController = new ClaimController(playerStorage);

        moduleManager = new ModuleManager(this);
        moduleManager.registerModule(new SignHandlerModule(claimController));
        moduleManager.registerModule(new ShopHandlerModule());

        registerMainCommand("shops").addSubCommands(
                new ReloadCommand("reload")
                        .setCommandUsage("Reloads the plugin")
                        .setCommandUsage("reload")
                        .setPermission("shops.reload"),
                new ShopClaimCommand("claim", claimController)
                        .setCommandDescription("Claims a free plot")
                        .setCommandUsage("claim")
                        .setPermission("shops.claim"),
                new ShopUnclaimCommand("unclaim", claimController)
                        .setCommandDescription("Unclaims your plot")
                        .setCommandUsage("unclaim")
                        .setPermission("shops.unclaim"),
                new ShopInfoCommand("info")
                        .setCommandDescription("Gives info about the plot you're standing on")
                        .setCommandUsage("info <PlayerName>")
                        .setPermission("shops.info"),
                new ShopListCommand("list")
                        .setCommandDescription("Lists the shops")
                        .setCommandUsage("list <free>")
                        .setPermission("shops.list")
        );
    }

    public void init() {
        TextUtil.prefix = TextUtil.color(getConfigManager().getSubConfig("config").getConfig().getString("messages.message_prefix", "&4&lSHOPS > &r"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public ClaimController getClaimController() {
        return claimController;
    }

    public ShopStorage getShopStorage() {
        return shopStorage;
    }

    public PlayerStorage getPlayerStorage() {
        return playerStorage;
    }
}
