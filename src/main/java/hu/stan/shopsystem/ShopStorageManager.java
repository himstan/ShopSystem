package hu.stan.shopsystem;

import hu.stan.shopsystem.model.ShopChest;
import hu.stan.shopsystem.strifeplugin.DreamPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShopStorageManager {

    private DreamPlugin plugin;
    private ShopStorage shopStorage;
    private File dataFolder;
    private File dataFile;

    public ShopStorageManager(DreamPlugin plugin, ShopStorage shopStorage) {
        this.plugin = plugin;
        this.shopStorage = shopStorage;
        try {
            handleDataFolder();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDataFolder() throws IOException {
        File dataFolder = new File(plugin.getDataFolder(), "shopData");
        if (!dataFolder.exists()) dataFolder.mkdir();
        this.dataFolder = dataFolder;
        File dataFile = new File(dataFolder, "data.yml");
        if (!dataFile.exists()) {
            dataFile.createNewFile();
        }
        this.dataFile = dataFile;
    }

    public List<ShopChest> loadShopChests() {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(dataFile);
        ConfigurationSection section = yaml.getConfigurationSection("shopChests");
        List<ShopChest> shopChests = new ArrayList<>();
        if (section != null) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection chestSection = section.getConfigurationSection(key);
                assert chestSection != null;
                ShopChest shopChest = buildChestFromSection(chestSection);
                if (shopChest != null) shopChests.add(shopChest);
            }
        }
        return shopChests;
    }

    public boolean saveShopChests() throws IOException {
        YamlConfiguration yaml = new YamlConfiguration();
        ConfigurationSection configurationSection = yaml.createSection("shopChests");
        int i = 0;
        for (ShopChest shopChest : shopStorage.getShopChests()) {
            if (shopChest == null) continue;
            addToSection(i, configurationSection, shopChest);
            i++;
        }
        yaml.save(dataFile);
        return i > 0;
    }

    private ShopChest buildChestFromSection(ConfigurationSection section) {
        ShopChest shopChest = null;
        UUID shopOwner = UUID.fromString(section.getString("shopOwner"));
        Location signLocation = section.getLocation("signLoc");
        Location chestLocation = section.getLocation("chestLoc");
        Material currency = Material.getMaterial(section.getString("currency"));
        int cost = section.getInt("cost");
        try {
            Sign sign = getBlockFromLoc(signLocation, Sign.class);
            Chest chest = getBlockFromLoc(chestLocation, Chest.class);
            shopChest = new ShopChest(shopOwner, sign, chest, currency, cost);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return shopChest;
    }

    private <T extends BlockState> T getBlockFromLoc(Location location, Class<T> type) throws Exception{
        if (location == null) throw new Exception("Invalid location for finding a " + type.getSimpleName());

        Block block = location.getBlock();

        if (!type.isAssignableFrom(block.getState().getClass())) throw new Exception("Couldn't find a " + type.getSimpleName() + " in this location, not loading shopchest...");

        return (T) block.getState();
    }

    private void addToSection(int i, ConfigurationSection section, ShopChest shopChest) {
        ConfigurationSection chestSection = section.createSection(i + "");
        chestSection.set("shopOwner", shopChest.getShopOwnerUUID().toString());
        chestSection.set("signLoc", shopChest.getShopSign().getLocation());
        chestSection.set("chestLoc", shopChest.getShopChest().getLocation());
        chestSection.set("currency", shopChest.getCurrency().toString());
        chestSection.set("cost", shopChest.getCurrencyCost());
    }

}
