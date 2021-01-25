package hu.stan.shopsystem;

import java.util.UUID;

public class PlayerData {

    private UUID playerUUID;
    private String playerName;
    private ShopClaim shopClaim;

    public PlayerData(UUID playerUUID, String playerName) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
    }

    public PlayerData(UUID playerUUID, String playerName, ShopClaim shopClaim) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.shopClaim = shopClaim;
    }

    public boolean hasShopClaim() {
        return this.shopClaim != null;
    }

    public void removeShopClaim() {
        this.shopClaim = null;
    }

    public void setShopClaim(ShopClaim shopClaim) {
        this.shopClaim = shopClaim;
    }

    public ShopClaim getShopClaim() {
        return shopClaim;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getPlayerName() {
        return playerName;
    }
}
