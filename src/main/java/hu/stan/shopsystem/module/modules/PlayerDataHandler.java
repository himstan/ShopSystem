package hu.stan.shopsystem.module.modules;

import hu.stan.shopsystem.PlayerData;
import hu.stan.shopsystem.PlayerStorage;
import hu.stan.shopsystem.module.Module;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDataHandler extends Module {

    private PlayerStorage playerStorage;

    public PlayerDataHandler(PlayerStorage playerStorage) {
        this.playerStorage = playerStorage;
    }

    @Override
    protected void onEnable() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            addPlayer(player);
        }
    }

    @Override
    protected void onDisable() {
        for (PlayerData playerData : playerStorage.getPlayerDatas()) {
            playerStorage.removePlayerData(playerData.getPlayerUUID());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        addPlayer(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        removePlayer(player);
    }

    private void addPlayer(Player player) {
        playerStorage.addPlayerData(new PlayerData(player.getUniqueId(), player.getName()));
    }

    private void removePlayer(Player player) {
        playerStorage.removePlayerData(player);
    }
}
