package hu.stan.shopsystem.controller;

import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IClaimController {
    boolean hasOwner(Claim claim);

    void removeClaim(Player player, Claim claim);

    boolean hasClaim(Player player, Claim claim);

    int getClaimCount(Player player);

    void addClaim(Player player, Claim claim);

    boolean isStandingInSubDiv(Player player);

    Claim getStandingSubDiv(Player player);

    Claim getSubDivInLocation(Location location);

    boolean isSubDivInLocation(Location location);
}
