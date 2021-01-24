package hu.stan.shopsystem.strifeplugin.commands.subcommands;

import hu.stan.shopsystem.strifeplugin.commands.SubCommand;
import hu.stan.shopsystem.strifeplugin.utils.TextUtil;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ShopListCommand extends SubCommand {

    public ShopListCommand(String commandName) {
        super(commandName);
    }

    @Override
    public void execute(Player player, String commandName, String label, String[] args) {
        DataStore dataStore = GriefPrevention.instance.dataStore;

        String subArg = null;

        if (args.length > 0 && args[0].equalsIgnoreCase("free")) {
            subArg = "free";
        }

        List<Claim> subDivisons = getSubDivisons(dataStore.getClaims(), subArg);

        TextUtil.sendPrefixMessage(player, String.format("&a%d &eplots available in &aShops&e:", subDivisons.size()));

        for (Claim claim : subDivisons) {
            String ownerName = "FREE!";

            if (claim.ownerID != null) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(claim.ownerID);
                ownerName = offlinePlayer.getName();
            }

            Location lesserCorner = claim.getLesserBoundaryCorner();
            Location greaterCorner = claim.getGreaterBoundaryCorner();

            int x = (lesserCorner.getBlockX() + greaterCorner.getBlockX()) / 2;
            int z = (lesserCorner.getBlockZ() + greaterCorner.getBlockZ()) / 2;

            player.sendRawMessage(TextUtil.color(String.format("      &eOWNER: &3%s &eX: &3%d &eZ: &3%d", ownerName, x, z)));
        }
    }

    private void handleClaim(Claim claim, List<Claim> claims, String subArg) {
        if (claim.children != null) {
            for (Claim subClaim : claim.children) {
                handleClaim(subClaim, claims, subArg);
            }
        }
        if (claim.parent != null) {
            if (subArg == null || (subArg.equalsIgnoreCase("free") && claim.ownerID == null)) {
                claims.add(claim);
            }
        }
    }

    private List<Claim> getSubDivisons(Collection<Claim> claims, String subArg) {
        List<Claim> subClaims = new ArrayList<>();
        for (Claim claim : claims) {
            handleClaim(claim, subClaims, subArg);
        }
        return subClaims;
    }

    @Override
    public List<String> tabComplete(Player player, String label, String[] args) {
        if (args.length == 1) {
            return Collections.singletonList("free");
        }
        return Collections.emptyList();
    }
}
