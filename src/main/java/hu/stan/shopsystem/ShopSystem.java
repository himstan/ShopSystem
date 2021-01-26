package hu.stan.shopsystem;

import hu.stan.shopsystem.controller.ClaimController;
import hu.stan.shopsystem.module.ModuleManager;
import hu.stan.shopsystem.module.modules.ValidatorModule;
import hu.stan.shopsystem.module.modules.PlayerDataHandler;
import hu.stan.shopsystem.module.modules.ShopHandlerModule;
import hu.stan.shopsystem.module.modules.SignHandlerModule;
import hu.stan.shopsystem.strifeplugin.DreamPlugin;
import hu.stan.shopsystem.strifeplugin.commands.subcommands.*;
import hu.stan.shopsystem.strifeplugin.utils.TextUtil;

import java.util.Arrays;
import java.util.UUID;

public final class ShopSystem extends DreamPlugin {

    public static UUID commandUUID;

    private ModuleManager moduleManager;
    private ShopStorage shopStorage;
    private PlayerStorage playerStorage;
    private ClaimController claimController;
    private MaterialAdapter materialAdapter;

    @Override
    public void onEnable() {

        getConfigManager().addSubConfig("config");
        getConfigManager().addSubConfig("signconfig");
        getConfigManager().addSubConfig("material_pairs");

        this.materialAdapter = new MaterialAdapter(this);

        init();

        shopStorage = new ShopStorage(this);
        playerStorage = new PlayerStorage(this);
        claimController = new ClaimController(playerStorage);

        moduleManager = new ModuleManager(this);
        moduleManager.registerModule(new SignHandlerModule(claimController));
        moduleManager.registerModule(new ShopHandlerModule(playerStorage, shopStorage));
        moduleManager.registerModule(new PlayerDataHandler(playerStorage));
        moduleManager.registerModule(new ValidatorModule(shopStorage, playerStorage));

        registerMainCommand("shop", Arrays.asList("s")).addSubCommands(
                new ReloadCommand("reload")
                        .setCommandDescription("Reloads the plugin")
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
                new ShopRemoveClaimOwnerCommand("removeclaim", claimController, playerStorage)
                        .setCommandDescription("Removes the owner of the claim you're standing on")
                        .setCommandUsage("removeclaim")
                        .setPermission("shops.removeclaim"),
                new SetShopCommand("setshop", playerStorage, claimController)
                        .setCommandDescription("Sets a warp point in your shop")
                        .setCommandUsage("setshop")
                        .setPermission("shops.setshop"),
                new ShopCommand("shop", playerStorage)
                        .setCommandDescription("Teleports you to a shop")
                        .setCommandUsage("shop <PlayerName>")
                        .setPermission("shops.shop"),
                new ShopListCommand("list", playerStorage)
                        .setCommandDescription("Lists the shops")
                        .setCommandUsage("list <free/page> <page>")
                        .setPermission("shops.list"),
                new ShopInfoCommand("info", playerStorage)
                        .setCommandDescription("Gives info about the plot you're standing on")
                        .setCommandUsage("info <PlayerName>")
                        .setPermission("shops.info")
        );
    }

    public void init() {
        commandUUID = UUID.randomUUID();
        TextUtil.prefix = TextUtil.color(getConfigManager().getSubConfig("config").getConfig().getString("messages.message_prefix", "&4&lSHOPS > &r"));
        materialAdapter.init();
    }

    @Override
    public void onDisable() {
        moduleManager.unregisterModules();
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
