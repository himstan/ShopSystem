package hu.stan.shopsystem;

import hu.stan.shopsystem.strifeplugin.DreamPlugin;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlayerStorageManager {

    private DreamPlugin plugin;
    private PlayerStorage playerStorage;
    private File dataFolder;

    public PlayerStorageManager(DreamPlugin plugin, PlayerStorage playerStorage) {
        this.plugin = plugin;
        this.playerStorage = playerStorage;
        try {
            handleDataFolder();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDataFolder() throws IOException {
        File dataFolder = new File(plugin.getDataFolder(), "playerData");
        if (!dataFolder.exists()) dataFolder.mkdir();
        this.dataFolder = dataFolder;
    }

    public List<PlayerData> loadPlayers() {
        List<PlayerData> playerDatas = new ArrayList<>();
        for (File file : Objects.requireNonNull(dataFolder.listFiles())) {
            PlayerData playerData = loadPlayer(file);
            if (playerData != null) {
                playerDatas.add(playerData);
            }
        }
        return playerDatas;
    }

    public void savePlayers() {
        for (PlayerData playerData : playerStorage.getPlayerDatas()) {
            try {
                savePlayer(playerData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public PlayerData loadPlayer(Player player) {
        return loadPlayer(new File(dataFolder, player.getUniqueId() + ".yml"));
    }

    public PlayerData loadPlayer(File file) {
        PlayerData playerData = null;
        if (file.exists()) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            UUID playerUUID = UUID.fromString(yaml.getString("playerUUID"));
            String playerName = yaml.getString("playerName");
            playerData = new PlayerData(playerUUID, playerName);
            if (yaml.isConfigurationSection("shopClaim")) {
                ConfigurationSection shopSection = yaml.getConfigurationSection("shopClaim");
                if (shopSection.getKeys(false).size() > 0) {
                    long claimID = shopSection.getLong("claimID");
                    long lastEdit = shopSection.getLong("lastEdit");
                    Location warpLoc = shopSection.getLocation("warpLoc");
                    ShopClaim shopClaim = new ShopClaim(playerUUID, warpLoc, claimID, lastEdit);
                    playerData.setShopClaim(shopClaim);
                }
            }
        }
        return playerData;
    }

    public boolean savePlayer(PlayerData playerData) throws IOException {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("playerUUID", playerData.getPlayerUUID().toString());
        yaml.set("playerName", playerData.getPlayerName());
        ConfigurationSection claimSection = yaml.createSection("shopClaim");
        if (playerData.hasShopClaim()) {
            setShopClaim(claimSection, playerData.getShopClaim());
        }
        yaml.save(new File(dataFolder, playerData.getPlayerUUID() + ".yml"));
        return true;
    }

    private void setShopClaim(ConfigurationSection claimSection, ShopClaim shopClaim) {
        claimSection.set("claimID", shopClaim.getClaimID());
        claimSection.set("lastEdit", shopClaim.getLastEdit());
        claimSection.set("warpLoc", shopClaim.getWarpLoc());
    }
}
