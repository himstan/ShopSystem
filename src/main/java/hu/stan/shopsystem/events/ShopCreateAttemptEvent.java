package hu.stan.shopsystem.events;

import hu.stan.shopsystem.model.ShopChestResult;
import org.bukkit.entity.Player;

public class ShopCreateAttemptEvent extends DreamEvent{

    private long createTime;
    private Player shopCreator;
    private ShopChestResult shopChestResult;

    public ShopCreateAttemptEvent(Player shopCreator, ShopChestResult shopChestResult) {
        this.shopCreator = shopCreator;
        this.createTime = System.currentTimeMillis();
        this.shopChestResult = shopChestResult;
    }

    public Player getShopCreator() {
        return shopCreator;
    }

    public long getCreateTime() {
        return createTime;
    }

    public ShopChestResult getShopChestResult() {
        return shopChestResult;
    }
}
