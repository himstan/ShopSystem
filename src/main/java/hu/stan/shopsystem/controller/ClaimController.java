package hu.stan.shopsystem.controller;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClaimController implements IClaimController {

    private DataStore dataStore;

    public ClaimController() {
        this.dataStore = GriefPrevention.instance.dataStore;

        for (Claim subClaim : getSubDivisons(dataStore.getClaims())) {
            if (subClaim.ownerID != null) {
                PlayerData playerData = dataStore.getPlayerData(subClaim.ownerID);
                if (!playerData.getClaims().contains(subClaim))
                    playerData.getClaims().add(subClaim);
            }
        }
    }

    @Override
    public boolean hasOwner(Claim claim) {
        return claim.ownerID != null;
    }

    @Override
    public void removeClaim(Player player, Claim claim) {
        claim.ownerID = null;
        PlayerData playerData = dataStore.getPlayerData(player.getUniqueId());
        playerData.getClaims().remove(claim);
        dataStore.saveClaim(claim);
        dataStore.savePlayerDataSync(player.getUniqueId(), playerData);
    }

    @Override
    public boolean hasClaim(Player player, Claim claim) {
        PlayerData playerData = dataStore.getPlayerData(player.getUniqueId());
        return claim.ownerID != null && player.getUniqueId().equals(claim.ownerID) && playerData.getClaims().contains(claim);
    }

    @Override
    public int getClaimCount(Player player) {
        PlayerData playerData = dataStore.getPlayerData(player.getUniqueId());
        return (int) playerData.getClaims().stream().filter(claim -> claim.parent != null).count();
    }

    @Override
    public void addClaim(Player player, Claim claim) {
        PlayerData playerData = dataStore.getPlayerData(player.getUniqueId());
        if (!playerData.getClaims().contains(claim)) {
            playerData.getClaims().add(claim);
        }
        claim.ownerID = player.getUniqueId();
        dataStore.saveClaim(claim);
        dataStore.savePlayerDataSync(player.getUniqueId(), playerData);
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
