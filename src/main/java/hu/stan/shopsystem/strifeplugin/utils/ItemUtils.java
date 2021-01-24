package hu.stan.shopsystem.strifeplugin.utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemUtils {

    public static void removeItems(Inventory inventory, Material itemType, int amount) {
        int removed = 0;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack != null && itemStack.getType() == itemType) {
                int itemAmount = itemStack.getAmount();
                if (itemAmount <= amount) {
                    inventory.remove(itemStack);
                    removed += itemAmount;
                } else {
                    itemStack.setAmount(itemAmount - (amount - removed));
                    removed += (amount - removed);
                }
                if (removed >= amount) {
                    break;
                }
            }
        }
    }

}
