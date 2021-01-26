package hu.stan.shopsystem.module.modules;

import hu.stan.shopsystem.PlayerData;
import hu.stan.shopsystem.PlayerStorage;
import hu.stan.shopsystem.module.Module;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class PlayerDataHandler extends Module {

    private PlayerStorage playerStorage;

    public PlayerDataHandler(PlayerStorage playerStorage) {
        this.playerStorage = playerStorage;
    }

    @Override
    protected void onEnable() {
        playerStorage.loadPlayers();
    }

    @Override
    protected void onDisable() {
        playerStorage.savePlayers();
        Collection<PlayerData> playerDatas = new ArrayList<>(playerStorage.getPlayerDatas());
        for (PlayerData playerData : playerDatas) {
            playerStorage.removePlayerData(playerData.getPlayerUUID());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Optional<PlayerData> optionalPlayerData = playerStorage.getPlayerData(player);
        if (!optionalPlayerData.isPresent()) {
            addPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Optional<PlayerData> optionalPlayerData = playerStorage.getPlayerData(player);
        if (optionalPlayerData.isPresent()) {
            PlayerData playerData = optionalPlayerData.get();
            if (!playerData.hasShopClaim()) {
                removePlayer(player);
            }
        }
    }

    private void addPlayer(Player player) {
        playerStorage.addPlayerData(new PlayerData(player.getUniqueId(), player.getName()));
    }

    private void removePlayer(Player player) {
        playerStorage.removePlayerData(player);
    }
}
