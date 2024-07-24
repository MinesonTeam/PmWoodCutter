package kz.hxncus.mc.pmwoodcutter.listener;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import kz.hxncus.mc.pmwoodcutter.PmWoodCutter;
import kz.hxncus.mc.pmwoodcutter.cache.PlayerCache;
import kz.hxncus.mc.pmwoodcutter.config.Messages;
import kz.hxncus.mc.pmwoodcutter.config.Settings;
import kz.hxncus.mc.pmwoodcutter.util.CacheUtil;
import kz.hxncus.mc.pmwoodcutter.util.RegionUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;
import java.util.Set;

public class PlayerListener implements Listener {
    private static PmWoodCutter plugin;

    public PlayerListener(PmWoodCutter plugin) {
        PlayerListener.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getCacheManager().loadPlayerCacheIntoConfig(player);
    }

    @EventHandler
    public void onPlayerBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block breakedBlock = event.getBlock();
        Set<ProtectedRegion> protectedRegions = RegionUtil.getRegions(breakedBlock.getLocation());
        Optional<ProtectedRegion> priorityRegion = protectedRegions.stream().min((first, second) -> second.getPriority() - first.getPriority());
        priorityRegion.ifPresent(region -> {
            if (!Settings.PLAYER_CHOP_REGIONS.toStringList().contains(region.getId())) {
                return;
            }
            event.setCancelled(true);
            if (!breakedBlock.getType().name().endsWith("_LOG")) {
                return;
            }
            PlayerCache playerCache = plugin.getCacheManager().getPlayerCache(player);
            int currentChop = playerCache.getCurrentWood();
            if (currentChop < 2) {
                playerCache.setCurrentWood(CacheUtil.getRandomChopAmount());
                if (currentChop == 1) {
                    plugin.getAnimationManager().getStringAnimationMap().get("treefall").animate(player);
                    if (playerCache.getCurrentTree() >= playerCache.getMaxTree()) {
                        Messages.INVENTORY_FULL.send(player, playerCache.getMaxTree());
                        return;
                    }
                    playerCache.setCurrentTree(playerCache.getCurrentTree() + 1);
                    playerCache.setTotalTree(playerCache.getTotalTree() + 1);
                    if (playerCache.getCurrentTree() % playerCache.getPerTreeAward() == 0) {
                        plugin.getVault().depositPlayer(player, playerCache.getAward());
                        Messages.AWARDED_MONEY.send(player, playerCache.getAward(), playerCache.getCurrentTree() + playerCache.getPerTreeAward());
                    }
                    Messages.TREE_CHOPPED.send(player, playerCache.getCurrentTree(), playerCache.getMaxTree());
                    return;
                }
            } else {
                playerCache.setCurrentExp(playerCache.getCurrentExp() + playerCache.getPerWoodExp());
                if (playerCache.getCurrentExp() >= playerCache.getNextLvlExp() && CacheUtil.isNextLvlAvailable(playerCache.getLvl() + 1)) {
                    CacheUtil.upgradeToNextLvl(playerCache);
                    Messages.LEVEL_UP.send(player, playerCache.getLvl());
                }
                playerCache.setTotalExp(playerCache.getTotalExp() + playerCache.getPerWoodExp());
                playerCache.setCurrentWood(currentChop - 1);
                playerCache.setTotalWood(playerCache.getTotalWood() + 1);
            }
            Messages.REMAINING_CHOP_TIME.send(player, playerCache.getCurrentWood());
        });
    }
}