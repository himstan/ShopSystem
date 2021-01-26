package hu.stan.shopsystem.strifeplugin.commands.subcommands;

import hu.stan.shopsystem.PlayerData;
import hu.stan.shopsystem.PlayerStorage;
import hu.stan.shopsystem.ShopSystem;
import hu.stan.shopsystem.strifeplugin.commands.SubCommand;
import hu.stan.shopsystem.strifeplugin.utils.TextUtil;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ShopListCommand extends SubCommand {
    
    private int maxPerPage = 10;
    private PlayerStorage playerStorage;
    
    public ShopListCommand(String commandName, PlayerStorage playerStorage) {
        super(commandName);
        this.playerStorage = playerStorage;
    }

    @Override
    public void execute(Player player, String commandName, String label, String[] args) {
        DataStore dataStore = GriefPrevention.instance.dataStore;

        String subArg = null;
        String number = "1";

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("free")) {
                subArg = "free";
                if (args.length > 1) {
                    number = args[1];
                }
            } else {
                number = args[0];
            }
        }
        List<Claim> subDivisons = getSubDivisons(dataStore.getClaims(), subArg);

        int maxPages = maxPages(subDivisons);

        int currentPage = 1;

        try {
            currentPage = Integer.parseInt(number);
            if (currentPage > maxPages) {
                currentPage = maxPages;
            }
        } catch (NumberFormatException ignored) {
        }

        TextUtil.sendPrefixMessage(player, String.format("&a%d &eplots available in &aShops&e (%d/%d)", subDivisons.size(), currentPage, maxPages));

        for (int i = (currentPage * maxPerPage) - maxPerPage; i < currentPage * maxPerPage; i++) {
            if (i >= subDivisons.size()) {
                return;
            }
            Claim claim;
            if ((claim = subDivisons.get(i)) == null) {
                return;
            }
            String ownerName = "FREE!";

            if (claim.ownerID != null) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(claim.ownerID);
                ownerName = offlinePlayer.getName();
            }

            Location lesserCorner = claim.getLesserBoundaryCorner();
            Location greaterCorner = claim.getGreaterBoundaryCorner();
            int minY = Math.min(lesserCorner.getBlockY(), greaterCorner.getBlockY());
            int maxY = Math.max(lesserCorner.getBlockY(), greaterCorner.getBlockY());
            int x = (lesserCorner.getBlockX() + greaterCorner.getBlockX()) / 2;
            int z = (lesserCorner.getBlockZ() + greaterCorner.getBlockZ()) / 2;
            TextComponent textComponent = new TextComponent(TextUtil.color(String.format("&eX: &3%d &eZ: &3%d", x, z)));
            if (claim.ownerID == null) {
                Location bestLocation = findBestLoc(new Location(lesserCorner.getWorld(), x, lesserCorner.getBlockY(), z), minY, maxY);
                textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TextUtil.color("&6Teleport to free plot"))));
                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,  String.format("/tptofreeplot %d %d %d %s" , bestLocation.getBlockX(), bestLocation.getBlockY(), bestLocation.getBlockZ(), ShopSystem.commandUUID.toString())));
            } else {
                Optional<PlayerData> playerDataOptional = playerStorage.getPlayerData(claim.ownerID);
                playerDataOptional.ifPresent(playerData -> {
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TextUtil.color("&6Teleport to &f" + playerData.getPlayerName() + "&6's shop"))));
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,  String.format("/s shop %s", playerData.getPlayerName())));
                });
            }
            player.spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(TextUtil.prefix), new TextComponent(TextUtil.color(String.format("      &eOwner: &3%s ", ownerName))), textComponent);
        }
    }

    private Location findBestLoc(Location location, int minY, int maxY) {
        int yOffset = 2;
        Location bestLoc = location;
        for (int y = minY; y <= 256; y++) {
            Block block = getYLocation(location, y - yOffset).getBlock();
            if (block.getType().isAir()) {
                return block.getLocation();
            }
            bestLoc = block.getLocation();
        }
        return bestLoc;
    }

    private Location getYLocation(Location location, int y) {
        return new Location(location.getWorld(), location.getBlockX(), y, location.getBlockZ());
    }

    private int maxPages(List<Claim> claims) {
        return (int) Math.ceil((double) claims.size() / maxPerPage);
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
