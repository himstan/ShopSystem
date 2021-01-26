package hu.stan.shopsystem;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;

import java.util.UUID;

public class ShopClaim {

    private UUID claimOwner;
    private Location warpLoc;
    private long claimID;
    private long lastEdit;

    public ShopClaim(UUID claimOwner, long claimID) {
        this.claimOwner = claimOwner;
        this.claimID = claimID;
        updateLastEdit();
    }

    public ShopClaim(UUID claimOwner, long claimID, long lastEdit) {
        this.claimOwner = claimOwner;
        this.claimID = claimID;
        this.lastEdit = lastEdit;
    }

    public ShopClaim(UUID claimOwner, Location warpLoc, long claimID, long lastEdit) {
        this.claimOwner = claimOwner;
        this.warpLoc = warpLoc;
        this.claimID = claimID;
        this.lastEdit = lastEdit;
    }

    public boolean isWarpLocSet() {
        return warpLoc != null;
    }

    public Location getWarpLoc() {
        return warpLoc;
    }

    public void setWarpLoc(Location warpLoc) {
        this.warpLoc = warpLoc;
    }

    public void updateLastEdit() {
        this.lastEdit = System.currentTimeMillis();
    }

    public long getLastEdit() {
        return lastEdit;
    }

    public UUID getClaimOwner() {
        return claimOwner;
    }

    public long getClaimID() {
        return claimID;
    }

    public Claim getClaim() {
        DataStore dataStore = GriefPrevention.instance.dataStore;
        return dataStore.getClaim(claimID);
    }
}
