package hu.stan.shopsystem.module.modules;

import hu.stan.shopsystem.controller.ClaimController;
import hu.stan.shopsystem.events.ShopCreateAttemptEvent;
import hu.stan.shopsystem.model.ShopChest;
import hu.stan.shopsystem.model.ShopChestResult;
import hu.stan.shopsystem.module.Module;
import hu.stan.shopsystem.strifeplugin.utils.ItemUtils;
import hu.stan.shopsystem.strifeplugin.utils.TextUtil;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

public class ShopHandlerModule extends Module {

    private Map<Inventory, ShopChest> shopChestMap = new HashMap<>();

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    @EventHandler
    private void onChestClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        Player player = (Player) event.getWhoClicked();
        if (inventory != null && shopChestMap.containsKey(inventory)) {
            ShopChest shopChest = shopChestMap.get(inventory);
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

    private void handleShop(ShopChest shopChest) {
        Player player = shopChest.getShopOwner().getPlayer();
        assert player != null;
        shopChest.getShopSign().setLine(0, TextUtil.color("&0[&5Price&0]"));
        shopChest.getShopSign().setLine(3, player.getName());
        shopChest.getShopSign().update();
        shopChestMap.put(shopChest.getShopChest().getBlockInventory(), shopChest);
    }
}
