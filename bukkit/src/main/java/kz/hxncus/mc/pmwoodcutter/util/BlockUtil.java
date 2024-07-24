package kz.hxncus.mc.pmwoodcutter.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

@UtilityClass
public class BlockUtil {
    public final BlockFace[] CARTESIAN_BLOCK_FACES = Arrays.stream(BlockFace.values()).filter(BlockFace::isCartesian).toArray(BlockFace[]::new);

    public Set<Location> getCartesianLocations(Block block, Predicate<Location> filter) {
        return getCartesianLocations(block.getLocation(), filter);
    }

    public Set<Location> getCartesianLocations(Location location, Predicate<Location> filter) {
        return getCartesianLocations(location, new HashSet<>(), filter);
    }

    public Set<Location> getCartesianLocations(Location location, Set<Location> locationSet, Predicate<Location> filter) {
        if (!locationSet.add(location)) {
            return locationSet;
        }
        for (BlockFace cartesianBlockFace : CARTESIAN_BLOCK_FACES) {
            Location relativeLocation = LocationUtil.getRelative(location, cartesianBlockFace);
            if (filter.test(relativeLocation)) {
                locationSet.addAll(getCartesianLocations(relativeLocation, locationSet, filter));
            }
        }
        return locationSet;
    }
}