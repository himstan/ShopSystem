package hu.stan.shopsystem.strifeplugin.commands.subcommands;

import hu.stan.shopsystem.PlayerData;
import hu.stan.shopsystem.PlayerStorage;
import hu.stan.shopsystem.ShopClaim;
import hu.stan.shopsystem.controller.ClaimController;
import hu.stan.shopsystem.strifeplugin.commands.SubCommand;
import hu.stan.shopsystem.strifeplugin.utils.TextUtil;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.entity.Player;
import org.w3c.dom.Text;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SetShopCommand extends SubCommand {

    private PlayerStorage playerStorage;
    private ClaimController claimController;

    public SetShopCommand(String commandName, PlayerStorage playerStorage, ClaimController claimController) {
        super(commandName);
        this.playerStorage = playerStorage;
        this.claimController = claimController;
    }

    @Override
    public void execute(Player player, String commandName, String label, String[] args) {
        Optional<PlayerData> optionalPlayerData = playerStorage.getPlayerData(player);

        if (!optionalPlayerData.isPresent()) {
            TextUtil.sendPrefixMessage(player, "&6Error loading user data. Try reconnecting to the server.");
            return;
        }

        PlayerData playerData = optionalPlayerData.get();

        if (!playerData.hasShopClaim()) {
            TextUtil.sendPrefixMessage(player, "&6You don't have a claimed shop subdivision.");
            return;
        }

        if (!claimController.isStandingInSubDiv(player)) {
            TextUtil.sendPrefixMessage(player, "&6You are not standing in a subdivision.");
            return;
        }

        Claim claim = claimController.getStandingSubDiv(player);

        if (!claimController.hasClaim(player, claim)) {
            TextUtil.sendPrefixMessage(player, "&6You don't own this subdivision.");
            return;
        }

        ShopClaim shopClaim = playerData.getShopClaim();

        if (shopClaim.isWarpLocSet()) {
            TextUtil.sendPrefixMessage(player, "&6You successfully replaced your shop's warp.");
        } else {
            TextUtil.sendPrefixMessage(player, "&6You successfully created a warp for your shop!");
        }
        shopClaim.setWarpLoc(player.getLocation());
    }

    @Override
    public List<String> tabComplete(Player player, String label, String[] args) {
        return Collections.emptyList();
    }
}
