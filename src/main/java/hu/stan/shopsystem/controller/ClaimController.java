package hu.stan.shopsystem.controller;

import hu.stan.shopsystem.PlayerData;
import hu.stan.shopsystem.PlayerStorage;
import hu.stan.shopsystem.ShopClaim;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class ClaimController implements IClaimController {

    private DataStore dataStore;
    private PlayerStorage playerStorage;

    public ClaimController(PlayerStorage playerStorage) {
        this.dataStore = GriefPrevention.instance.dataStore;
        this.playerStorage = playerStorage;
    }

    @Override
    public boolean hasOwner(Claim claim) {
        return claim.ownerID != null;
    }

    @Override
    public void removeClaim(Player player, Claim claim) {
        claim.ownerID = null;
        playerStorage.ifExists(player, playerData -> {
            playerData.removeShopClaim();
        });
        dataStore.saveClaim(claim);
    }

    @Override
    public boolean hasClaim(Player player, Claim claim) {
        Optional<PlayerData> playerData = playerStorage.getPlayerData(player);
        boolean playerDataHasClaim = playerData.isPresent() && playerData.get().hasShopClaim() && playerData.get().getShopClaim().getClaim().getID().equals(claim.getID());
        return claim.ownerID != null && player.getUniqueId().equals(claim.ownerID) && playerDataHasClaim;
    }

    @Override
    public int getClaimCount(Player player) {
        AtomicInteger count = new AtomicInteger();
        playerStorage.ifExists(player, playerData -> {
            if (playerData.hasShopClaim()) {
                count.set(1);
            }
        });
        return count.get();
    }

    @Override
    public void addClaim(Player player, Claim claim) {
        playerStorage.ifExists(player, playerData -> {
            playerData.setShopClaim(new ShopClaim(player.getUniqueId(), claim.getID()));
        });
        claim.ownerID = player.getUniqueId();
        dataStore.saveClaim(claim);
    }

    @Override
    public boolean isStandingInSubDiv(Player player) {
        return isSubDivInLocation(player.getLocation());
    }

    @Override
    public Claim getStandingSubDiv(Player player) {
        return getSubDivInLocation(player.getLocation());
    }

    @Override
    public Claim getSubDivInLocation(Location location) {
        Claim claim = dataStore.getClaimAt(location, false, null);
        if (claim.parent == null) {
            return null;
        }
        return claim;
    }

    @Override
    public boolean isSubDivInLocation(Location location) {
        Claim claim = dataStore.getClaimAt(location, false, null);
        return claim != null && claim.parent != null;
    }

    private void handleClaim(Claim claim, List<Claim> claims) {
        if (claim.children != null) {
            for (Claim subClaim : claim.children) {
                handleClaim(subClaim, claims);
            }
        }
        if (claim.parent != null) {
            claims.add(claim);
        }
    }

    public List<Claim> getSubDivisons(Collection<Claim> claims) {
        List<Claim> subClaims = new ArrayList<>();
        for (Claim claim : claims) {
            handleClaim(claim, subClaims);
        }
        return subClaims;
    }

}
