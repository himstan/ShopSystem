package hu.stan.shopsystem.module.modules;

import hu.stan.shopsystem.PlayerStorage;
import hu.stan.shopsystem.module.Module;
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

    }

    @Override
    protected void onDisable() {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

    }
}
