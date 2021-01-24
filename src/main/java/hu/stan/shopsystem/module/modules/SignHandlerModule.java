package hu.stan.shopsystem.module.modules;

import hu.stan.shopsystem.controller.ClaimController;
import hu.stan.shopsystem.events.ShopCreateAttemptEvent;
import hu.stan.shopsystem.model.Result;
import hu.stan.shopsystem.model.ShopChest;
import hu.stan.shopsystem.model.ShopChestResult;
import hu.stan.shopsystem.module.Module;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

import java.util.*;

public class SignHandlerModule extends Module {

    private HashMap<UUID, Chest> creatingShop = new HashMap<>();
    private ClaimController claimController;
    private String shopTag = "[Price]";

    public SignHandlerModule(ClaimController claimController) {
        this.claimController = claimController;
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

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
        return Objects.requireNonNull(event.getLine(0)).equalsIgnoreCase(shopTag);
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

            Material material = Material.getMaterial(currencyLine);

            if (material == null) {
                event.setLine(2, "INVALID");
                shopChestResult.setResult(Result.INVALID_CURRENCY);
            }
            if (shopChestResult.getResult() == Result.SUCCESS) {
                setSignLines(sign, event.getLines());
                shopChestResult.setShopChest(new ShopChest(shopCreator.getUniqueId(), chest.getLocation(), sign, chest, material, amount));
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
}
