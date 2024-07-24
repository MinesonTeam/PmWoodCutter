package kz.hxncus.mc.pmwoodcutter.cache;

import kz.hxncus.mc.pmwoodcutter.PmWoodCutter;
import kz.hxncus.mc.pmwoodcutter.config.ConfigManager;
import kz.hxncus.mc.pmwoodcutter.config.Settings;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
public class CacheManager {
    private static PmWoodCutter plugin;
    private final Set<UUID> fallingBlockSet = new HashSet<>();
    private final Map<UUID, PlayerCache> uuidPlayerCacheMap = new ConcurrentHashMap<>();
    private final Map<Location, Map.Entry<Long, Material>> locationEntryMap = new ConcurrentHashMap<>();

    public CacheManager(final PmWoodCutter plugin) {
        CacheManager.plugin = plugin;
        loadGlobalBoosterTime();
    }

    public void runTreeRegenerateTask() {
        for (Map.Entry<Location, Map.Entry<Long, Material>> locationEntry : locationEntryMap.entrySet()) {
            if (System.currentTimeMillis() > locationEntry.getValue().getKey()) {
                regenerateTree(locationEntry.getKey());
            }
        }
    }

    public void unregister() {
        loadOnlinePlayersCachesIntoConfig();
        regenerateTreesImmediately();
        removeAllFallingBlocks();
        unloadGlobalBoosterTime();
    }

    private void removeAllFallingBlocks() {
        fallingBlockSet.forEach(uuid -> {
            Entity entity = plugin.getServer().getEntity(uuid);
            if (entity != null) {
                entity.remove();
                fallingBlockSet.remove(uuid);
            }
        });
    }

    private void unloadGlobalBoosterTime() {
        long globalBoosterTime = plugin.getBoosterManager().getGlobalBoosterTime();
        long currentTimeMillis = System.currentTimeMillis();
        plugin.getConfigManager().getData().set("global_booster_time", globalBoosterTime > currentTimeMillis ? globalBoosterTime - currentTimeMillis : 0);
        plugin.getConfigManager().saveDataConfig();
    }

    private void loadGlobalBoosterTime() {
        long globalBoosterTime = plugin.getConfigManager().getData().getLong("global_booster_time", 0L);
        plugin.getBoosterManager().setGlobalBoosterTime(globalBoosterTime + System.currentTimeMillis());
    }

    private void regenerateTreesImmediately() {
        locationEntryMap.keySet().forEach(this::regenerateTree);
    }

    private void regenerateTree(Location location) {
        location.getBlock().setType(locationEntryMap.remove(location).getValue());
    }

    private void loadOnlinePlayersCachesIntoConfig() {
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            loadPlayerCacheIntoConfig(onlinePlayer);
        }
    }

    public PlayerCache unloadPlayerCacheFromConfig(Player player) {
        ConfigManager configManager = plugin.getConfigManager();
        YamlConfiguration dataConfig = configManager.getData();
        ConfigurationSection section = dataConfig.getConfigurationSection("players." + player.getName());
        if (section == null) {
            return fromMap(player.getUniqueId(), Settings.DEFAULT_PLAYER_DATA.toConfigSection().getValues(false));
        }
        return fromMap(player.getUniqueId(), section.getValues(false));
    }

    public PlayerCache fromMap(UUID uuid, Map<String, Object> stringObjectMap) {
        PlayerCache playerCache = new PlayerCache(uuid, ((Number) stringObjectMap.get("award")).doubleValue(),
                ((Number) stringObjectMap.get("current_wood")).intValue(),
                ((Number) stringObjectMap.get("total_wood")).intValue(),
                ((Number) stringObjectMap.get("money_per_tree")).doubleValue(),
                ((Number) stringObjectMap.get("current_tree")).intValue(),
                ((Number) stringObjectMap.get("max_tree")).intValue(),
                ((Number) stringObjectMap.get("total_tree")).intValue(),
                ((Number) stringObjectMap.get("per_tree_award")).intValue(),
                ((Number) stringObjectMap.get("per_item_sell_limit")).longValue(),
                ((Number) stringObjectMap.get("lvl")).intValue(),
                ((Number) stringObjectMap.get("current_exp")).doubleValue(),
                ((Number) stringObjectMap.get("per_wood_exp")).doubleValue(),
                ((Number) stringObjectMap.get("next_lvl_exp")).doubleValue(),
                ((Number) stringObjectMap.get("total_exp")).doubleValue(),
                ((Number) stringObjectMap.get("local_booster_time")).longValue() + System.currentTimeMillis());
        Object object = stringObjectMap.get("sell_limits");
        if (object instanceof MemorySection) {
            MemorySection memorySection = (MemorySection) object;
            Map<Material, Long> materialLongMap = memorySection.getValues(false).entrySet().stream()
                .filter(entry -> Material.getMaterial(entry.getKey()) != null && entry.getValue() instanceof Number)
                .collect(Collectors.toMap(entry -> Material.getMaterial(entry.getKey()),
                                                entry -> ((Number) entry.getValue()).longValue()));
            playerCache.getMaterialLongMap().putAll(materialLongMap);
        }
        return playerCache;
    }

    public void storePlayerCacheIntoConfig(String path, YamlConfiguration config, PlayerCache playerCache) {
        config.set(path + ".award", playerCache.getAward());
        config.set(path + ".current_wood", playerCache.getCurrentWood());
        config.set(path + ".total_wood", playerCache.getTotalWood());
        config.set(path + ".money_per_tree", playerCache.getMoneyPerTree());
        config.set(path + ".current_tree", playerCache.getCurrentTree());
        config.set(path + ".max_tree", playerCache.getMaxTree());
        config.set(path + ".total_tree", playerCache.getTotalTree());
        config.set(path + ".per_tree_award", playerCache.getPerTreeAward());
        config.set(path + ".per_item_sell_limit", playerCache.getPerItemSellLimit());
        config.set(path + ".lvl", playerCache.getLvl());
        config.set(path + ".current_exp", playerCache.getCurrentExp());
        config.set(path + ".per_wood_exp", playerCache.getPerWoodExp());
        config.set(path + ".next_lvl_exp", playerCache.getNextLvlExp());
        config.set(path + ".total_exp", playerCache.getTotalExp());
        long localBoosterTime = playerCache.getLocalBoosterTime();
        long currentTimeMillis = System.currentTimeMillis();
        config.set(path + ".local_booster_time", localBoosterTime > currentTimeMillis ? localBoosterTime - currentTimeMillis : 0);
        config.createSection(path + ".sell_limits", playerCache.getMaterialLongMap().entrySet().stream()
                                                     .collect(Collectors.toMap(entry -> entry.getKey().name(),
                                                                                      Map.Entry::getValue)));
    }

    public void loadPlayerCacheIntoConfig(Player player) {
        PlayerCache playerCache = uuidPlayerCacheMap.remove(player.getUniqueId());
        if (playerCache == null) {
            return;
        }
        YamlConfiguration dataConfig = plugin.getConfigManager().getData();
        storePlayerCacheIntoConfig("players." + player.getName(), dataConfig, playerCache);
        plugin.getConfigManager().saveDataConfig();
    }

    public PlayerCache getPlayerCacheIfExists(final Player player) {
        if (isPlayerHasCache(player)) {
            return getPlayerCache(player);
        }
        return null;
    }

    public boolean isPlayerHasCache(final Player player) {
        return uuidPlayerCacheMap.containsKey(player.getUniqueId());
    }

    @NonNull
    public PlayerCache getPlayerCache(final Player player) {
        if (!uuidPlayerCacheMap.containsKey(player.getUniqueId())) {
            uuidPlayerCacheMap.put(player.getUniqueId(), unloadPlayerCacheFromConfig(player));
        }
        return uuidPlayerCacheMap.get(player.getUniqueId());
    }

    public PlayerCache removePlayerCache(final UUID uuid) {
        return uuidPlayerCacheMap.remove(uuid);
    }
}
