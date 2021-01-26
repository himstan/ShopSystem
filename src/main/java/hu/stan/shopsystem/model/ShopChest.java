package hu.stan.shopsystem.model;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;

import java.util.UUID;

public class ShopChest {

    private UUID shopOwner;
    private Sign shopSign;
    private Chest shopChest;
    private Material currency;
    private int currencyCost;

    public ShopChest(UUID shopOwner, Sign shopSign, Chest shopChest, Material currency, int currencyCost) {
        this.shopOwner = shopOwner;
        this.shopSign = shopSign;
        this.shopChest = shopChest;
        this.currency = currency;
        this.currencyCost = currencyCost;
    }

    public Sign getShopSign() {
        return shopSign;
    }

    public OfflinePlayer getShopOwner() {
        return Bukkit.getOfflinePlayer(shopOwner);
    }

    public UUID getShopOwnerUUID() {
        return shopOwner;
    }

    public Chest getShopChest() {
        return shopChest;
    }

    public Material getCurrency() {
        return currency;
    }

    public int getCurrencyCost() {
        return currencyCost;
    }
}
