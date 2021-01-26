package hu.stan.shopsystem.module.modules;

import hu.stan.shopsystem.MaterialAdapter;
import hu.stan.shopsystem.controller.ClaimController;
import hu.stan.shopsystem.events.ShopCreateAttemptEvent;
import hu.stan.shopsystem.model.Result;
import hu.stan.shopsystem.model.ShopChest;
import hu.stan.shopsystem.model.ShopChestResult;
import hu.stan.shopsystem.module.Module;
import hu.stan.shopsystem.strifeplugin.utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class SignHandlerModule extends Module {

    private HashMap<UUID, Chest> creatingShop = new HashMap<>();
    private ClaimController claimController;

    private String config_shopTag;
    private String config_shopWorld;


    public SignHandlerModule(ClaimController claimController) {
        this.claimController = claimController;
    }

    @Override
    protected void onEnable() {
        FileConfiguration config = plugin.getConfigManager().getSubConfig("signconfig").getConfig();
        config_shopTag = config.getString("shop_sign.shop_tag");
        config_shopWorld = config.getString("shop_sign.shop_world", "world");
    }

    @Override
    protected void onDisable() {

    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        creatingShop.remove(player.getUniqueId());
    }

    @EventHandler
    private void onSignPlace(BlockPlaceEvent event) {
        Block placedBlock = event.getBlockPlaced();
        Player player = event.getPlayer();
        if (placedBlock.getState() instanceof Sign) {
            Block placedOnBlock = event.getBlockAgainst();
            if (placedOnBlock.getState() instanceof Chest) {
                Chest chest = (Chest) placedOnBlock.getState();
                creatingShop.remove(player.getUniqueId());
                creatingShop.put(player.getUniqueId(), chest);
            }
        }
    }

    @EventHandler
    private void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        Sign sign = (Sign) event.getBlock().getState();
        if (creatingShop.containsKey(player.getUniqueId())) {
            Chest chest = creatingShop.get(player.getUniqueId());
            creatingShop.remove(player.getUniqueId());
            if (!isShopSign(event)) return;
            ShopChestResult shopChestResult = createShopChest(event.getPlayer(), chest, sign, event);
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> plugin.getServer().getPluginManager().callEvent(new ShopCreateAttemptEvent(player, shopChestResult)), 1);
        }
    }

    private boolean isShopSign(SignChangeEvent event) {
        return Objects.requireNonNull(event.getLine(0)).equalsIgnoreCase(config_shopTag);
    }

    private ShopChestResult createShopChest(Player shopCreator, Chest chest, Sign sign, SignChangeEvent event) {
        String[] lines = event.getLines();
        ShopChestResult shopChestResult = new ShopChestResult(Result.SUCCESS, null);
        String amountLine = lines[1];
        String currencyLine = lines[2];

        Location signLocation = sign.getLocation();

        if (!claimController.isSubDivInLocation(signLocation) || !claimController.hasClaim(shopCreator, claimController.getSubDivInLocation(signLocation))) {
            shopChestResult.setResult(Result.OUT_OF_SUBDIVISION);
        }

        if (!isInShopWorld(shopCreator)) {
            shopChestResult.setResult(Result.NOT_SHOP_WORLD);
        }

        if (isDoubleChest(chest)) {
            shopChestResult.setResult(Result.DOUBLE_CHEST);
        }

        if (shopChestResult.getResult() == Result.SUCCESS) {
            int amount = 1;
            try {
                amount = Integer.parseInt(amountLine);
            } catch (NumberFormatException e) {
                event.setLine(1, "INVALID");
                shopChestResult.setResult(Result.INVALID_AMOUNT);
            }

            if (amount < 1 || amount > 64) {
                event.setLine(1, "OUT OF RANGE");
                shopChestResult.setResult(Result.INVALID_AMOUNT);
            }

            Material material = MaterialAdapter.getMaterial(currencyLine);

            if (material == null) {
                event.setLine(2, "INVALID");
                shopChestResult.setResult(Result.INVALID_CURRENCY);
            }
            if (shopChestResult.getResult() == Result.SUCCESS) {
                setSignLines(sign, event.getLines());
                shopChestResult.setShopChest(new ShopChest(shopCreator.getUniqueId(), sign, chest, material, amount));
            }
        }

        return shopChestResult;
    }

    private void setSignLines(Sign sign, String[] lines) {
        for (int i = 0; i < lines.length; i++) {
            sign.setLine(i, lines[i]);
        }
        sign.update();
    }

    private boolean isInShopWorld(Player player) {
        return (player.getLocation().getWorld().getName().equalsIgnoreCase(config_shopWorld));
    }

    private boolean isDoubleChest(Chest chest) {
        Inventory inventory = chest.getInventory();
        return inventory instanceof DoubleChestInventory;
    }
}
