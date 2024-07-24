package kz.hxncus.mc.pmwoodcutter.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

@UtilityClass
public class LocationUtil {
    public Location getRelative(Location location, int x, int y, int z) {
        return location.clone().add(x, y, z);
    }

    public Location getRelative(Location location, BlockFace blockFace) {
        return getRelative(location, blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());
    }
}