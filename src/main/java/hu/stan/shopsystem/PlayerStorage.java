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
import java.util.*;
import java.util.function.Consumer;

public class PlayerStorage {

    private DreamPlugin plugin;
    private PlayerStorageManager playerStorageManager;
    private Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    private Map<String, UUID> nameToID = new HashMap<>();

    public PlayerStorage(DreamPlugin plugin) {
        this.plugin = plugin;
        this.playerStorageManager = new PlayerStorageManager(plugin, this);
    }

    public Optional<PlayerData> getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    public Optional<PlayerData> getPlayerData(UUID playerUUID) {
        Optional<PlayerData> optionalPlayerData = Optional.empty();
        if (playerUUID != null) {
            PlayerData playerData = playerDataMap.get(playerUUID);
            if (playerData != null) optionalPlayerData = Optional.of(playerData);
        }
        return optionalPlayerData;
    }

    public Optional<PlayerData> getPlayerData(String playerName) {
        UUID playerID = null;
        playerName = playerName.toLowerCase();
        if (nameToID.containsKey(playerName)) {
            playerID = nameToID.get(playerName);
        }
        return getPlayerData(playerID);
    }

    public void ifExists(Player player, Consumer<PlayerData> action) {
        Optional<PlayerData> playerDataOpt = getPlayerData(player);
        if (playerDataOpt.isPresent()) {
            PlayerData playerData = playerDataOpt.get();
            action.accept(playerData);
        }
    }

    public void addPlayerData(PlayerData playerData) {
        playerDataMap.put(playerData.getPlayerUUID(), playerData);
        nameToID.put(playerData.getPlayerName().toLowerCase(), playerData.getPlayerUUID());
    }

    public void removePlayerData(UUID playerID) {
        playerDataMap.remove(playerID);
        nameToID.remove(Bukkit.getOfflinePlayer(playerID).getName().toLowerCase());
    }

    public void removePlayerData(Player player) {
        playerDataMap.remove(player.getUniqueId());
        nameToID.remove(player.getName().toLowerCase());
    }

    public Collection<PlayerData> getPlayerDatas() {
        return playerDataMap.values();
    }

    public void loadPlayers() {
        List<PlayerData> playerDataList = playerStorageManager.loadPlayers();
        for (PlayerData playerData : playerDataList) {
            addPlayerData(playerData);
        }
    }

    public void savePlayers() {
        playerStorageManager.savePlayers();
    }

    public void savePlayer(Player player) {
        ifExists(player, playerData -> {
            try {
                playerStorageManager.savePlayer(playerData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public PlayerStorageManager getPlayerStorageManager() {
        return playerStorageManager;
    }
}
