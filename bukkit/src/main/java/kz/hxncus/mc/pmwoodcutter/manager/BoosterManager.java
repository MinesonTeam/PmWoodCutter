package kz.hxncus.mc.pmwoodcutter.manager;

import kz.hxncus.mc.pmwoodcutter.PmWoodCutter;
import kz.hxncus.mc.pmwoodcutter.cache.PlayerCache;
import kz.hxncus.mc.pmwoodcutter.config.Messages;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

@Setter
@Getter
@EqualsAndHashCode
public class BoosterManager {
    private static PmWoodCutter plugin;
    private long globalBoosterTime = System.currentTimeMillis();

    public BoosterManager(PmWoodCutter plugin) {
        BoosterManager.plugin = plugin;
    }

    public void addGlobalBoosterTime(long additionTime) {
        if (globalBoosterTime >= System.currentTimeMillis()) {
            globalBoosterTime += additionTime;
        } else {
            globalBoosterTime = System.currentTimeMillis() + additionTime;
        }
    }

    public void addLocalBoosterTime(Player player, long additionTime) {
        PlayerCache playerCache = plugin.getCacheManager().getPlayerCache(player);
        if (playerCache.getLocalBoosterTime() >= System.currentTimeMillis()) {
            playerCache.setLocalBoosterTime(playerCache.getLocalBoosterTime() + additionTime);
        } else {
            playerCache.setLocalBoosterTime(System.currentTimeMillis() + additionTime);
        }
    }

    public boolean isGlobalBoosterActive() {
        return globalBoosterTime >= System.currentTimeMillis();
    }

    public boolean isLocalBoosterActive(Player player) {
        PlayerCache playerCache = plugin.getCacheManager().getPlayerCacheIfExists(player);
        return playerCache != null && playerCache.getLocalBoosterTime() >= System.currentTimeMillis();
    }

    public long getLocalBoosterTime(Player player) {
        return plugin.getCacheManager().getPlayerCache(player).getLocalBoosterTime();
    }

    public double multiplyIfHasBooster(Player player, double amount) {
        if (isGlobalBoosterActive() || isLocalBoosterActive(player)) {
            return amount * 2;
        }
        return amount;
    }

    public void runActionBarBoosterTask() {
        TimeUnit milliseconds = TimeUnit.MILLISECONDS;
        long globalBooster = globalBoosterTime - System.currentTimeMillis();
        String globalBoosterText = Messages.GLOBAL_BOOSTER.toString(milliseconds.toDays(globalBooster), milliseconds.toHours(globalBooster) % 24,
                milliseconds.toMinutes(globalBooster) % 60, milliseconds.toSeconds(globalBooster) % 60);
        String actionBarText = null;
        if (isGlobalBoosterActive()) {
            actionBarText = globalBoosterText;
        }
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            if (isLocalBoosterActive(onlinePlayer)) {
                long localBooster = getLocalBoosterTime(onlinePlayer) - System.currentTimeMillis();
                String localBoosterText = Messages.LOCAL_BOOSTER.toString(milliseconds.toDays(localBooster), milliseconds.toHours(localBooster) % 24,
                                          milliseconds.toMinutes(localBooster) % 60, milliseconds.toSeconds(localBooster) % 60);
                if (actionBarText == null) {
                    actionBarText = localBoosterText;
                } else {
                    actionBarText = Messages.BOOSTER_FORMAT.toString(globalBoosterText, localBoosterText);
                }
            }
            if (actionBarText != null) {
                onlinePlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionBarText));
            }
        }
    }
}
