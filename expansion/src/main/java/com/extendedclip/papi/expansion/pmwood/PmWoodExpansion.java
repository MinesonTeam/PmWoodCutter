package com.extendedclip.papi.expansion.pmwood;

import kz.hxncus.mc.pmwoodcutter.PmWoodCutter;
import kz.hxncus.mc.pmwoodcutter.cache.CacheManager;
import kz.hxncus.mc.pmwoodcutter.cache.PlayerCache;
import lombok.NonNull;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Locale;

public final class PmWoodExpansion extends PlaceholderExpansion {
    private static final String VERSION = "1.0.0";

    @Override
    @NonNull
    public String getIdentifier() {
        return "pmwdc";
    }

    @Override
    @NonNull
    public String getAuthor() {
        return "hxncus";
    }

    @Override
    @NonNull
    public String getVersion() {
        return VERSION;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NonNull String identifier) {
        PmWoodCutter plugin = PmWoodCutter.get();
        if (plugin == null) {
            return "";
        }
        Player player = offlinePlayer.getPlayer();
        // If the online player is null, then the player is offline.
        if (player == null) {
            return "";
        }
        // Get player cache from our PmWoodCutter plugin
        CacheManager cacheManager = plugin.getCacheManager();
        PlayerCache playerCache = cacheManager.getPlayerCache(player);
        String identifierLowerCase = identifier.toLowerCase(Locale.ENGLISH);
        switch (identifierLowerCase) {
            case "award": {
                // %pmwdc_award%
                return "" + playerCache.getAward();
            }
            case "currentwood": {
                // %pmwdc_currentwood%
                return "" + playerCache.getCurrentWood();
            }
            case "totalwood": {
                // %pmwdc_totalwood%
                return "" + playerCache.getTotalWood();
            }
            case "moneypertree": {
                // %pmwdc_moneypertree%
                return "" + playerCache.getMoneyPerTree();
            }
            case "currenttree": {
                // %pmwdc_currenttree%
                return "" + playerCache.getCurrentTree();
            }
            case "maxtree": {
                // %pmwdc_maxtree%
                return "" + playerCache.getMaxTree();
            }
            case "totaltree": {
                // %pmwdc_totaltree%
                return "" + playerCache.getTotalTree();
            }
            case "pertreeaward": {
                // %pmwdc_pertreeaward%
                return "" + playerCache.getPerTreeAward();
            }
            case "peritemselllimit": {
                // %pmwdc_peritemselllimit%
                return "" + playerCache.getPerItemSellLimit();
            }
            case "lvl": {
                // %pmwdc_lvl%
                return "" + playerCache.getLvl();
            }
            case "currentexp": {
                // %pmwdc_currentexp%
                return "" + playerCache.getCurrentExp();
            }
            case "perwoodexp": {
                // %pmwdc_perwoodexp%
                return "" + playerCache.getPerWoodExp();
            }
            case "nextlvlexp": {
                // %pmwdc_nextlvlexp%
                return "" + playerCache.getNextLvlExp();
            }
            case "totalexp": {
                // %pmwdc_totalexp%
                return "" + playerCache.getTotalExp();
            }
            case "localboostertime": {
                // %pmwdc_localboostertime%
                return "" + playerCache.getLocalBoosterTime();
            }
            default: {
                if (identifier.startsWith("selllimits_") && identifier.length() > 11) {
                    Material material = Material.getMaterial(identifier.substring(11).toUpperCase(Locale.ENGLISH));
                    if (material == null) {
                        // No material, zero is returned
                        return "0";
                    } else {
                        // %pmwdc_selllimits_{material}% for example %pmwdc_selllimits_stone%
                        return "" + playerCache.getMaterialLongMap().getOrDefault(material, 0L);
                    }
                }
            }
        }
        return "";
    }
}
