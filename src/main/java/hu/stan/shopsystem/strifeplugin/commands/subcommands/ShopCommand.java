package hu.stan.shopsystem.strifeplugin.commands.subcommands;

import hu.stan.shopsystem.PlayerData;
import hu.stan.shopsystem.PlayerStorage;
import hu.stan.shopsystem.ShopClaim;
import hu.stan.shopsystem.strifeplugin.commands.SubCommand;
import hu.stan.shopsystem.strifeplugin.utils.TextUtil;
import org.bukkit.entity.Player;
import org.w3c.dom.Text;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ShopCommand extends SubCommand {

    private PlayerStorage playerStorage;

    public ShopCommand(String commandName, PlayerStorage playerStorage) {
        super(commandName);
        this.playerStorage = playerStorage;
    }

    @Override
    public void execute(Player player, String commandName, String label, String[] args) {
        String playerName = player.getName();
        if (args.length > 0) {
            playerName = args[0];
        }
        Optional<PlayerData> playerDataOptional = playerStorage.getPlayerData(playerName);
        if (playerDataOptional.isPresent()) {
            PlayerData playerData = playerDataOptional.get();
            if (!playerData.hasShopClaim()) {
                if (playerName.equalsIgnoreCase(player.getName())) {
                    TextUtil.sendPrefixMessage(player, "&6You don't have a shop.");
                } else {
                    TextUtil.sendPrefixMessage(player, "&6This player doesn't have a shop.");
                }
                return;
            }
            ShopClaim shopClaim = playerData.getShopClaim();
            if (!shopClaim.isWarpLocSet()) {
                if (playerName.equalsIgnoreCase(player.getName())) {
                    TextUtil.sendPrefixMessage(player, "&6Your shop doesn't have a warp location set.");
                } else {
                    TextUtil.sendPrefixMessage(player, "&6This player's shop doesn't have a warp location set.");
                }
                return;
            }
            player.teleport(shopClaim.getWarpLoc());
            TextUtil.sendPrefixMessage(player, String.format("&3Teleporting you to &f%s&3's shop...", playerData.getPlayerName()));
            return;
        }
        TextUtil.sendPrefixMessage(player, "&6This player doesn't exist.");
    }

    @Override
    public List<String> tabComplete(Player player, String label, String[] args) {
        if (args.length == 1) {
            return playerStorage.getPlayerDatas().stream().map(PlayerData::getPlayerName).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
