package hu.stan.shopsystem.strifeplugin.commands.subcommands;

import hu.stan.shopsystem.controller.ClaimController;
import hu.stan.shopsystem.strifeplugin.commands.SubCommand;
import hu.stan.shopsystem.strifeplugin.utils.TextUtil;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ShopUnclaimCommand extends SubCommand {

    private DataStore dataStore;
    private ClaimController claimController;

    public ShopUnclaimCommand(String commandName, ClaimController claimController) {
        super(commandName);
        dataStore = GriefPrevention.instance.dataStore;
        this.claimController = claimController;
    }

    @Override
    public void execute(Player player, String commandName, String label, String[] args) {

        PlayerData playerData = dataStore.getPlayerData(player.getUniqueId());
        if (claimController.getClaimCount(player) > 0) {
            for (Claim claim : claimController.getSubDivisons(playerData.getClaims())) {
                claimController.removeClaim(player, claim);
            }
            TextUtil.sendPrefixMessage(player, "&3You unclaimed your subdivision!");
        } else {
            TextUtil.sendPrefixMessage(player, "&6You don't have a subdivision.");
        }
    }

    @Override
    public List<String> tabComplete(Player player, String label, String[] args) {
        return Collections.emptyList();
    }
}
