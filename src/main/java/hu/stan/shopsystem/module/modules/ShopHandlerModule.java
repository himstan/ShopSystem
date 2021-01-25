package hu.stan.shopsystem.module.modules;

import hu.stan.shopsystem.ShopStorage;
import hu.stan.shopsystem.ShopSystem;
import hu.stan.shopsystem.controller.ClaimController;
import hu.stan.shopsystem.events.ShopCreateAttemptEvent;
import hu.stan.shopsystem.model.ShopChest;
import hu.stan.shopsystem.model.ShopChestResult;
import hu.stan.shopsystem.module.Module;
import hu.stan.shopsystem.strifeplugin.utils.ItemUtils;
import hu.stan.shopsystem.strifeplugin.utils.TextUtil;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ShopHandlerModule extends Module {

    private String config_successTag;
    private ShopStorage shopStorage;

    @Override
    protected void onEnable() {
        FileConfiguration config = plugin.getConfigManager().getSubConfig("signconfig").getConfig();
        config_successTag = TextUtil.color(config.getString("shop_sign.success_shop_tag"));
        shopStorage = ((ShopSystem) plugin).getShopStorage();
    }

    @Override
    protected void onDisable() {

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
                player.sendRawMessage("You don't have enough to buy this!");
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
    }
}
