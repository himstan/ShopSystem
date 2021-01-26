package hu.stan.shopsystem.strifeplugin.commands.subcommands;

import hu.stan.shopsystem.PlayerData;
import hu.stan.shopsystem.PlayerStorage;
import hu.stan.shopsystem.controller.ClaimController;
import hu.stan.shopsystem.strifeplugin.commands.SubCommand;
import hu.stan.shopsystem.strifeplugin.utils.TextUtil;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ShopRemoveClaimOwnerCommand extends SubCommand {

    private DataStore dataStore;
    private ClaimController claimController;
    private PlayerStorage playerStorage;

    public ShopRemoveClaimOwnerCommand(String commandName, ClaimController claimController, PlayerStorage playerStorage) {
        super(commandName);
        dataStore = GriefPrevention.instance.dataStore;
        this.claimController = claimController;
        this.playerStorage = playerStorage;
    }

    @Override
    public void execute(Player player, String commandName, String label, String[] args) {

        if (!claimController.isStandingInSubDiv(player)) {
            TextUtil.sendPrefixMessage(player, "&6You are not standing in a subdivision!");
        }

        Claim claim = claimController.getStandingSubDiv(player);
        if (claim.ownerID == null) {
            TextUtil.sendPrefixMessage(player, "&3This subdivison doesn't have an owner.");
        } else {
            Optional<PlayerData> playerDataOptional = playerStorage.getPlayerData(claim.ownerID);
            playerDataOptional.ifPresent(playerData -> {
                playerData.removeShopClaim();
                Player removedFrom = Bukkit.getPlayer(playerData.getPlayerUUID());
                if (removedFrom != null) {
                    TextUtil.sendPrefixMessage(removedFrom, "&6Your shop has been removed by &f" + player.getName());
                }
            });
            claim.ownerID = null;
            dataStore.saveClaim(claim);
            TextUtil.sendPrefixMessage(player, "&3You removed this claim's owner.");
        }
    }

    @Override
    public List<String> tabComplete(Player player, String label, String[] args) {
        return Collections.emptyList();
    }
}
