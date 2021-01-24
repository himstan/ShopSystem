package hu.stan.shopsystem.strifeplugin.commands.subcommands;

import hu.stan.shopsystem.strifeplugin.commands.SubCommand;
import hu.stan.shopsystem.strifeplugin.utils.TextUtil;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ShopInfoCommand extends SubCommand {

    public ShopInfoCommand(String commandName) {
        super(commandName);
    }

    @Override
    public void execute(Player player, String commandName, String label, String[] args) {
        DataStore dataStore = GriefPrevention.instance.dataStore;
        Claim claim;
        if (args.length > 0) {
            String playerName = args[0];
            Player shopOwner = Bukkit.getPlayer(playerName);
            if (shopOwner == null) {
                TextUtil.sendPrefixMessage(player, "&6Couldn't find a player with this name.");
                return;
            }
            UUID shopOwnerID = shopOwner.getUniqueId();
            PlayerData playerData = dataStore.getPlayerData(shopOwnerID);
            for (Claim c : playerData.getClaims()) {
                if (c.parent != null) {
                    printClaimInfo(player, c);
                }
            }
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
            String ownerName = "FREE!";

            if (claim.ownerID != null) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(claim.ownerID);
                ownerName = offlinePlayer.getName();
            }

            Location lesserCorner = claim.getLesserBoundaryCorner();
            Location greaterCorner = claim.getGreaterBoundaryCorner();

            int x = (lesserCorner.getBlockX() + greaterCorner.getBlockX()) / 2;
            int z = (lesserCorner.getBlockZ() + greaterCorner.getBlockZ()) / 2;

            TextUtil.sendPrefixMessage(player, String.format("&eOWNER: &3%s &eX: &3%d &eZ: &3%d", ownerName, x, z));
    }

    @Override
    public List<String> tabComplete(Player player, String label, String[] args) {
        if (args.length == 1) return null;
        return Collections.emptyList();
    }
}
