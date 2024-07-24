package kz.hxncus.mc.pmwoodcutter.animation;

import kz.hxncus.mc.pmwoodcutter.PmWoodCutter;
import kz.hxncus.mc.pmwoodcutter.config.Settings;
import kz.hxncus.mc.pmwoodcutter.util.BlockUtil;
import kz.hxncus.mc.pmwoodcutter.util.Constants;
import kz.hxncus.mc.pmwoodcutter.util.VersionUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TreeFallAnimation implements Animation {
    private static PmWoodCutter plugin;

    public TreeFallAnimation(PmWoodCutter plugin) {
        TreeFallAnimation.plugin = plugin;
    }

    @Override
    public void animate(Player player) {
        Block targetBlock = VersionUtil.getTargetBlock(player, 5);
        String targetBlockName = targetBlock.getType().name();
        if (!targetBlockName.endsWith("_LOG")) {
            return;
        }
        Set<Location> locationSet = BlockUtil.getCartesianLocations(targetBlock, location ->
            location.getBlock().getType().name().startsWith(targetBlockName.replace("_LOG", "")));
        Map<Location, Map.Entry<Long, Material>> locationEntryMap = plugin.getCacheManager().getLocationEntryMap();
        Set<UUID> fallingBlockSet = plugin.getCacheManager().getFallingBlockSet();
        long regenerateTime = System.currentTimeMillis() + Settings.TREE_REGENERATE_TIME.toNumber().longValue();
        for (Location location : locationSet) {
            Block block = location.getBlock();
            Material material = block.getType();
            block.setType(Material.AIR);

            FallingBlock fallingBlock = player.getWorld().spawnFallingBlock(location, material, (byte) 1);
            fallingBlock.setDropItem(false);

            locationEntryMap.put(location, new AbstractMap.SimpleEntry<>(regenerateTime, material));
            fallingBlockSet.add(fallingBlock.getUniqueId());

            int firstRandomNum = Constants.RANDOM.nextInt(3);
            int secRandomNum = Constants.RANDOM.nextInt(3);
            double x = firstRandomNum == 1 ? 0.2 : firstRandomNum == 2 ? -0.2 : 0;
            double z = secRandomNum == 1 ? 0.2 : secRandomNum == 2 ? -0.2 : 0;
            fallingBlock.setVelocity(new Vector(x, 0.5, z));
        }
    }
}
