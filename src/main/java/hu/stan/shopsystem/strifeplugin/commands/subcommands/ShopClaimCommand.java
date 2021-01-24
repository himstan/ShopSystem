package hu.stan.shopsystem.strifeplugin.commands.subcommands;

import hu.stan.shopsystem.controller.ClaimController;
import hu.stan.shopsystem.strifeplugin.commands.SubCommand;
import hu.stan.shopsystem.strifeplugin.utils.TextUtil;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ShopClaimCommand extends SubCommand {

    private DataStore dataStore;
    private ClaimController claimController;

    public ShopClaimCommand(String commandName, ClaimController claimController) {
        super(commandName);
        dataStore = GriefPrevention.instance.dataStore;
        this.claimController = claimController;
    }

    @Override
    public void execute(Player player, String commandName, String label, String[] args) {

        Claim claim = dataStore.getClaimAt(player.getLocation(), false, null);
        if (claim == null || claim.parent == null) {
            TextUtil.sendPrefixMessage(player, "&6You are not standing in a subdivision.");
            return;
        }

        if (claimController.hasClaim(player, claim)) {
            TextUtil.sendPrefixMessage(player, "&6You already own this subdivison.");
            return;
        }

        if (claimController.getClaimCount(player) > 0) {
            TextUtil.sendPrefixMessage(player, "&6You already own a subdivison.");
            return;
        }

        if (claimController.hasOwner(claim)) {
            TextUtil.sendPrefixMessage(player, "&6This subdivison already has an owner.");
            return;
        }

        claimController.addClaim(player, claim);

        TextUtil.sendPrefixMessage(player, "&3You claimed the subdivision!");
    }

    @Override
    public List<String> tabComplete(Player player, String label, String[] args) {
        return Collections.emptyList();
    }
}
