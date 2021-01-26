package hu.stan.shopsystem.module.modules;

import hu.stan.shopsystem.PlayerStorage;
import hu.stan.shopsystem.ShopClaim;
import hu.stan.shopsystem.ShopStorage;
import hu.stan.shopsystem.ShopSystem;
import hu.stan.shopsystem.model.ShopChest;
import hu.stan.shopsystem.module.Module;
import hu.stan.shopsystem.strifeplugin.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.w3c.dom.Text;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class ValidatorModule extends Module {

    private ShopStorage shopStorage;
    private PlayerStorage playerStorage;

    private String config_shopWorld;

    public ValidatorModule(ShopStorage shopStorage, PlayerStorage playerStorage) {
        this.shopStorage = shopStorage;
        this.playerStorage = playerStorage;
    }

    @Override
    protected void onEnable() {
        FileConfiguration config = plugin.getConfigManager().getSubConfig("signconfig").getConfig();
        config_shopWorld = config.getString("shop_sign.shop_world", "world");
    }

    @Override
    protected void onDisable() {

    }

    @EventHandler
    public void onChestOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();
        if (shopStorage.isShop(inventory)) {
            Optional<ShopChest> shopChestOptional = shopStorage.getShop(inventory);
            if (shopChestOptional.isPresent()) {
                ShopChest shopChest = shopChestOptional.get();
                playerStorage.ifExists(player, playerData -> {
                    if (playerData.hasShopClaim()) {
                        ShopClaim shopClaim = playerData.getShopClaim();
                        if (shopChest.getShopOwnerUUID().equals(player.getUniqueId())) {
                            shopClaim.updateLastEdit();
                        }
                    }
                });
            }
        }
    }

    @EventHandler
    public void onChestPlace(BlockPlaceEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Block block = event.getBlock();
            BlockState blockState = block.getState();
            if (blockState instanceof Chest) {
                Inventory inventory = ((Chest) blockState).getInventory();
                if (inventory instanceof DoubleChestInventory) {
                    if (isShop(block, BlockFace.EAST) || isShop(block, BlockFace.WEST) || isShop(block, BlockFace.SOUTH) || isShop(block, BlockFace.NORTH)) {
                        block.breakNaturally();
                    }
                }
            }
        },1);
    }

    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        String commandMessage = event.getMessage();
        String[] args = commandMessage.split(" ");
        if (args.length == 5) {
            if (args[0].equalsIgnoreCase("/tptofreeplot")) {
                try {
                    int x = Integer.parseInt(args[1]);
                    int y = Integer.parseInt(args[2]);
                    int z = Integer.parseInt(args[3]);
                    UUID uuid = UUID.fromString(args[4]);
                    if (uuid.equals(ShopSystem.commandUUID)) {
                        World shopWorld = plugin.getServer().getWorld(config_shopWorld);
                        event.getPlayer().teleport(new Location(shopWorld, x, y, z));
                        TextUtil.sendPrefixMessage(event.getPlayer(), "&3Teleporting you to a free plot...");
                    } else {
                        TextUtil.sendPrefixMessage(event.getPlayer(), "&6Invalid command ID! Use the list command agian!");
                    }
                    event.setCancelled(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private boolean isShop(Block block, BlockFace blockFace) {
        Block relativeBlock = block.getRelative(blockFace);
        BlockState blockState = relativeBlock.getState();
        if (blockState instanceof Chest) {
            return shopStorage.isShop(relativeBlock.getLocation());
        }
        return false;
    }
}
