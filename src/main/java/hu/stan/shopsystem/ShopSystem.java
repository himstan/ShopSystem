package hu.stan.shopsystem;

import hu.stan.shopsystem.controller.ClaimController;
import hu.stan.shopsystem.module.ModuleManager;
import hu.stan.shopsystem.module.modules.ShopHandlerModule;
import hu.stan.shopsystem.module.modules.SignHandlerModule;
import hu.stan.shopsystem.strifeplugin.DreamPlugin;
import hu.stan.shopsystem.strifeplugin.commands.subcommands.ShopClaimCommand;
import hu.stan.shopsystem.strifeplugin.commands.subcommands.ShopInfoCommand;
import hu.stan.shopsystem.strifeplugin.commands.subcommands.ShopListCommand;
import hu.stan.shopsystem.strifeplugin.commands.subcommands.ShopUnclaimCommand;

public final class ShopSystem extends DreamPlugin {

    private ModuleManager moduleManager;
    private ClaimController claimController;

    @Override
    public void onEnable() {

        claimController = new ClaimController();

        moduleManager = new ModuleManager(this);
        moduleManager.registerModule(new SignHandlerModule(claimController));
        moduleManager.registerModule(new ShopHandlerModule());

        registerMainCommand("shops").addSubCommands(
                new ShopClaimCommand("claim", claimController)
                    .setCommandDescription("Claims a free plot")
                    .setCommandUsage("claim"),
                new ShopUnclaimCommand("unclaim", claimController)
                        .setCommandDescription("Unclaims your plot")
                        .setCommandUsage("unclaim"),
                new ShopInfoCommand("info")
                    .setCommandDescription("Gives info about the plot you're standing on")
                    .setCommandUsage("info"),
                new ShopListCommand("list")
                    .setCommandDescription("Lists the shops")
                    .setCommandUsage("list")
        );
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
}
