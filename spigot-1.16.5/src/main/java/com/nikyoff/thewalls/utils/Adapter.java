package com.nikyoff.thewalls.utils;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;
import org.bukkit.World;

public class Adapter {
    public static Location Adapt(World world, BlockVector3 blockVector3) {
        return new Location(world, blockVector3.getBlockX(), blockVector3.getBlockY(), blockVector3.getBlockZ());
    }

    public static Region GetWorldEditRegion(Player player) {
        LocalSession session = WorldEdit.getInstance().getSessionManager().get(player);
        return session.getSelection(player.getWorld());
    }

    public static String LocationToString(Location location) {
        return "x: " + location.getX() + ", y: " + location.getY() + ", z: " + location.getZ();
    }
}
