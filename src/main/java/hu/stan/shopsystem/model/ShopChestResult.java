package hu.stan.shopsystem.model;

public class ShopChestResult {
    private Result result;
    private ShopChest shopChest;

    public ShopChestResult(Result result, ShopChest shopChest) {
        this.result = result;
        this.shopChest = shopChest;
    }

    public ShopChestResult() {
    }

    public Result getResult() {
        return result;
    }

    public ShopChest getShopChest() {
        return shopChest;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public void setShopChest(ShopChest shopChest) {
        this.shopChest = shopChest;
    }
}

