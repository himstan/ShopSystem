package hu.stan.shopsystem.module.modules;

import hu.stan.shopsystem.PlayerStorage;
import hu.stan.shopsystem.ShopStorage;
import hu.stan.shopsystem.ShopSystem;
import hu.stan.shopsystem.events.ShopCreateAttemptEvent;
import hu.stan.shopsystem.model.ShopChest;
import hu.stan.shopsystem.model.ShopChestResult;
import hu.stan.shopsystem.module.Module;
import hu.stan.shopsystem.strifeplugin.utils.ItemUtils;
import hu.stan.shopsystem.strifeplugin.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class ShopHandlerModule extends Module {

    private String config_successTag;
    private String config_shopWorld;
    private boolean config_warningEnabled;
    private int config_warningInterval;
    private String config_warningMessage;
    private int warningTaskID = -1;

    private PlayerStorage playerStorage;
    private ShopStorage shopStorage;

    public ShopHandlerModule(PlayerStorage playerStorage, ShopStorage shopStorage) {
        this.playerStorage = playerStorage;
        this.shopStorage = shopStorage;
    }

    @Override
    protected void onEnable() {
        FileConfiguration config = plugin.getConfigManager().getSubConfig("signconfig").getConfig();
        FileConfiguration shopConfig = plugin.getConfigManager().getSubConfig("config").getConfig();
        config_successTag = TextUtil.color(config.getString("shop_sign.success_shop_tag"));
        config_shopWorld = config.getString("shop_sign.shop_world", "world");
        config_warningEnabled = shopConfig.getBoolean("messages.warning_message.enabled", false);
        config_warningMessage = shopConfig.getString("messages.warning_message.warning_message", "");
        config_warningInterval = shopConfig.getInt("messages.warning_message.warning_message_interval", 500);
        shopStorage.loadChests();
        setShopTags();
        plugin.getLogger().info("Loaded " + shopStorage.getShopChests().size() + " shop chests!");
        if (config_warningEnabled) {
            startWarningTask();
        }
    }

    @Override
    protected void onDisable() {
        stopWarningTask();
        plugin.getLogger().info("Saving " + shopStorage.getShopChests().size() + " shop chests...");
        shopStorage.saveChests();
    }

    @EventHandler
    private void onChestClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        Player player = (Player) event.getWhoClicked();
        if (inventory != null && shopStorage.isShop(inventory) && shopStorage.getShop(inventory).isPresent()) {
            ShopChest shopChest = shopStorage.getShop(inventory).get();
            if (shopChest.getShopOwnerUUID().equals(player.getUniqueId())) return;
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
            event.setCancelled(true);
            PlayerInventory playerInventory = player.getInventory();
            if (!playerInventory.contains(shopChest.getCurrency(), shopChest.getCurrencyCost())) {
                TextUtil.sendPrefixMessage(player, "&6You don't have enough currency to buy this item.");
                return;
            }
            ItemUtils.removeItems(playerInventory, shopChest.getCurrency(), shopChest.getCurrencyCost());
            ItemStack currencyItem = new ItemStack(shopChest.getCurrency(), shopChest.getCurrencyCost());
            inventory.setItem(event.getRawSlot(), currencyItem);
            playerInventory.addItem(clickedItem);
        }
    }

    @EventHandler
    private void onShopCreate(ShopCreateAttemptEvent event) {
        Player shopCreator = event.getShopCreator();
        ShopChestResult result = event.getShopChestResult();
        switch (result.getResult()) {
            case SUCCESS:
                handleShop(result.getShopChest());
                break;
            case OUT_OF_SUBDIVISION:
                TextUtil.sendPrefixMessage(shopCreator, "&6Tried to create a shop outside your subdivison!");
                break;
            case INVALID_AMOUNT:
                TextUtil.sendPrefixMessage(shopCreator, "&6Invalid amount!");
                break;
            case INVALID_CURRENCY:
                TextUtil.sendPrefixMessage(shopCreator, "&6Invalid currency!");
                break;
            case NOT_SHOP_WORLD:
                TextUtil.sendPrefixMessage(shopCreator, "&6Not in shop world!");
                break;
            case DOUBLE_CHEST:
                TextUtil.sendPrefixMessage(shopCreator, "&6You can only create shops on single chests!");
                break;
        }
    }

    @EventHandler
    private void onShopBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block brokenBlock = event.getBlock();

        Location brokenBlockLoc = brokenBlock.getLocation();
        Optional<ShopChest> shopChestOptional = shopStorage.getShop(brokenBlockLoc);

        shopChestOptional.ifPresent(shopChest -> {
            TextUtil.sendPrefixMessage(player, "&6You removed a shop.");
            UUID ownerID = shopChest.getShopOwnerUUID();
            if (!player.getUniqueId().equals(ownerID)) {
                Player ownerPlayer = Bukkit.getPlayer(ownerID);
                if (ownerPlayer != null) {
                    TextUtil.sendPrefixMessage(ownerPlayer, "&6One of your shops have been removed by &f" + player.getName());
                }
            }
            shopStorage.removeShop(shopChest);
        });
    }

    private void handleShop(ShopChest shopChest) {
        Player player = shopChest.getShopOwner().getPlayer();
        assert player != null;
        shopChest.getShopSign().setLine(0, config_successTag);
        shopChest.getShopSign().setLine(3, player.getName());
        shopChest.getShopSign().update();
        shopStorage.saveShop(shopChest);
        TextUtil.sendPrefixMessage(player, "&3Your shop was created successfully!");
    }

    private void setShopTags() {
        for (ShopChest shopChest : shopStorage.getShopChests()) {
            Sign chestSign = shopChest.getShopSign();
            if (chestSign != null) {
                chestSign.setLine(0, config_successTag);
                chestSign.update();
            }
        }
    }

    private void startWarningTask() {
        if (!isWarningTaskRunnning()) {
            long interval = this.config_warningInterval * 20L;
            this.warningTaskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                World world = plugin.getServer().getWorld(config_shopWorld);
                if (world == null) return;

                List<Player> playerList = world.getPlayers();

                for (Player player : playerList) {
                    playerStorage.ifExists(player, playerData -> {
                        if (playerData.hasShopClaim()) {
                            TextUtil.sendPrefixMessage(player, config_warningMessage);
                        }
                    });
                }
            }, interval, interval);
        }
    }

    private void stopWarningTask() {
        if (isWarningTaskRunnning()) {
            plugin.getServer().getScheduler().cancelTask(this.warningTaskID);
            this.warningTaskID = -1;
        }
    }

    private boolean isWarningTaskRunnning() {
        return this.warningTaskID != -1;
    }

}
