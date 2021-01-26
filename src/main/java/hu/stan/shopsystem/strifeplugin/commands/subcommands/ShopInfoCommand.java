package hu.stan.shopsystem.strifeplugin.commands.subcommands;

import hu.stan.shopsystem.PlayerData;
import hu.stan.shopsystem.PlayerStorage;
import hu.stan.shopsystem.ShopClaim;
import hu.stan.shopsystem.model.ShopChest;
import hu.stan.shopsystem.strifeplugin.commands.SubCommand;
import hu.stan.shopsystem.strifeplugin.utils.OnlineTime;
import hu.stan.shopsystem.strifeplugin.utils.TextUtil;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ShopInfoCommand extends SubCommand {

    private PlayerStorage playerStorage;

    public ShopInfoCommand(String commandName, PlayerStorage playerStorage) {
        super(commandName);
        this.playerStorage = playerStorage;
    }

    @Override
    public void execute(Player player, String commandName, String label, String[] args) {
        DataStore dataStore = GriefPrevention.instance.dataStore;
        Claim claim;
        if (args.length > 0) {
            String playerName = args[0];
            Optional<PlayerData> playerDataOptional = playerStorage.getPlayerData(playerName);
            if (playerDataOptional.isPresent()) {
                PlayerData playerData = playerDataOptional.get();
                if (playerData.hasShopClaim()) {
                    UUID shopOwnerID = playerData.getPlayerUUID();
                    me.ryanhamshire.GriefPrevention.PlayerData playerD = dataStore.getPlayerData(shopOwnerID);
                    for (Claim c : playerD.getClaims()) {
                        if (c.parent != null) {
                            printClaimInfo(player, c);
                        }
                    }
                    return;
                }
            }
            TextUtil.sendPrefixMessage(player, "&6This player doesn't have a shop.");
        } else {
            claim = dataStore.getClaimAt(player.getLocation(), false, null);
            if (claim == null || claim.parent == null) {
                TextUtil.sendPrefixMessage(player, "&6You are not standing in a subdivision.");
                return;
            }
            printClaimInfo(player, claim);
        }
    }

    private void printClaimInfo(Player player, Claim claim) {
            UUID claimOwner = claim.ownerID;
            World world = claim.getGreaterBoundaryCorner().getWorld();
            if (claimOwner != null) {
                Optional<PlayerData> optionalPlayerData = playerStorage.getPlayerData(claimOwner);
                if (optionalPlayerData.isPresent()) {
                    PlayerData playerData = optionalPlayerData.get();
                    if (playerData.hasShopClaim()) {
                        ShopClaim shopClaim = playerData.getShopClaim();
                        OnlineTime onlineTime = new OnlineTime((int) ((System.currentTimeMillis() - shopClaim.getLastEdit()) / 1000));
                        TextUtil.sendPrefixMessage(player, "&6Plot information:");
                        TextUtil.sendPrefixMessage(player, "&6Area: &a" + TextUtil.capitalize(world.getName()));
                        TextUtil.sendPrefixMessage(player, String.format("&6Owner: &a%s", playerData.getPlayerName()));
                        TextUtil.sendPrefixMessage(player, String.format("&6Last edit: &a%dd %dh %dm %ds", onlineTime.getDays(), onlineTime.getHours(), onlineTime.getMinutes(), onlineTime.getSeconds()));
                    } else {
                        TextUtil.sendPrefixMessage(player, "&6This player doesn't have a shop.");
                    }
                }
            } else {
                TextUtil.sendPrefixMessage(player, "&6Plot information:");
                TextUtil.sendPrefixMessage(player, "&6Area: &a" + TextUtil.capitalize(world.getName()));
                TextUtil.sendPrefixMessage(player, "&6Owner: &aFREE!");
                TextUtil.sendPrefixMessage(player, "&6Last edit: &a0d 0h");
            }
    }

    @Override
    public List<String> tabComplete(Player player, String label, String[] args) {
        if (args.length == 1) return null;
        return Collections.emptyList();
    }
}
