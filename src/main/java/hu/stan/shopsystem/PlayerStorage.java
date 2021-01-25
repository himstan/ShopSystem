package hu.stan.shopsystem;

import com.google.gson.Gson;
import hu.stan.shopsystem.strifeplugin.DreamPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerStorage implements Listener {

    private DreamPlugin plugin;
    private File dataFolder;

    public PlayerStorage(DreamPlugin plugin) {
        this.plugin = plugin;
        handleDataFolder();
    }

    private void handleDataFolder() {
        File dataFolder = new File(plugin.getDataFolder(), "playerData");
        if (!dataFolder.exists()) dataFolder.mkdir();
        this.dataFolder = dataFolder;
    }

    private Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    public Optional<PlayerData> getPlayerData(UUID playerUUID) {
        Optional<PlayerData> optionalPlayerData = Optional.empty();
        if (playerUUID != null) {
            PlayerData playerData = playerDataMap.get(playerUUID);
            if (playerData != null) optionalPlayerData = Optional.of(playerData);
        }
        return optionalPlayerData;
    }

    public Optional<PlayerData> getPlayerData(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        UUID playerID = null;
        if (player != null) {
            playerID = player.getUniqueId();
        }
        return getPlayerData(playerID);
    }

    public void addPlayerData(PlayerData playerData) {
        playerDataMap.put(playerData.getPlayerUUID(), playerData);
    }

    public void removePlayerData(UUID playerID) {
        playerDataMap.remove(playerID);
    }

    public void removePlayerData(Player player) {
        playerDataMap.remove(player.getUniqueId());
    }

    public void savePlayer(PlayerData playerData) throws IOException {
        Gson gson = new Gson();
        gson.toJson(playerData, new FileWriter(new File(dataFolder, playerData.getPlayerUUID() + ".json")));
    }

}
