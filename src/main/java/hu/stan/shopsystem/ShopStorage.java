package hu.stan.shopsystem;

import hu.stan.shopsystem.model.ShopChest;
import hu.stan.shopsystem.strifeplugin.DreamPlugin;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

import java.io.IOException;
import java.util.*;

public class ShopStorage {

    private DreamPlugin plugin;
    private ShopStorageManager shopStorageManager;

    public ShopStorage(DreamPlugin plugin) {
        this.plugin = plugin;
        this.shopStorageManager = new ShopStorageManager(plugin, this);
    }

    private Map<Location, ShopChest> signToShopMap = new HashMap<>();
    private Map<Location, ShopChest> chestToShopMap = new HashMap<>();

    public boolean isShopSign(Location location) {
        return signToShopMap.containsKey(location);
    }

    public boolean isShopChest(Location location) {
        return chestToShopMap.containsKey(location);
    }

    public boolean isShop(Location location) {
        return isShopSign(location) || isShopChest(location);
    }

    public boolean isShop(Inventory inventory) {
        return isShop(inventory.getLocation());
    }

    public Optional<ShopChest> getShop(Location location) {
        Optional<ShopChest> shopChestOptional = Optional.empty();
        if (isShopSign(location)) {
            shopChestOptional = Optional.of(signToShopMap.get(location));
        } else if (isShopChest(location)) {
            shopChestOptional = Optional.of(chestToShopMap.get(location));
        }
        return shopChestOptional;
    }

    public Optional<ShopChest> getShop(Inventory inventory) {
        return getShop(inventory.getLocation());
    }

    public boolean saveShop(ShopChest shopChest) {
        if (shopChest == null) return false;

        Location chestLocation = shopChest.getShopChest().getLocation();
        Location signLocation = shopChest.getShopSign().getLocation();

        removeShopChest(chestLocation);
        removeShopSign(signLocation);

        signToShopMap.put(signLocation, shopChest);
        chestToShopMap.put(chestLocation, shopChest);
        return true;
    }

    public boolean removeShop(ShopChest shopChest) {
        if (shopChest == null) return false;

        Location chestLocation = shopChest.getShopChest().getLocation();
        Location signLocation = shopChest.getShopSign().getLocation();

        removeShopChest(chestLocation);
        removeShopSign(signLocation);
        return true;
    }

    public boolean removeShopSign(Location location) {
        return signToShopMap.remove(location) != null;
    }

    public boolean removeShopChest(Location location) {
        return chestToShopMap.remove(location) != null;
    }

    public Collection<ShopChest> getShopChests() {
        return chestToShopMap.values();
    }

    public ShopStorageManager getShopStorageManager() {
        return shopStorageManager;
    }

    public void loadChests() {
        List<ShopChest> shopChests = shopStorageManager.loadShopChests();
        for (ShopChest shopChest : shopChests) {
            saveShop(shopChest);
        }
    }

    public void saveChests() {
        try {
            shopStorageManager.saveShopChests();
            chestToShopMap.clear();
            signToShopMap.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
