package kz.hxncus.mc.pmwoodcutter.command;

import kz.hxncus.mc.pmwoodcutter.PmWoodCutter;
import kz.hxncus.mc.pmwoodcutter.cache.CacheManager;
import kz.hxncus.mc.pmwoodcutter.cache.PlayerCache;
import kz.hxncus.mc.pmwoodcutter.config.Messages;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

@ICommand.CommandInfo(subCommand = "info", permission = "info")
public class InfoSubCommand extends AbstractCommand<CommandSender> {
    public InfoSubCommand(JavaPlugin plugin) {
        super(plugin, CommandSender.class);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String... args) {
        if (!(plugin instanceof PmWoodCutter)) {
            return;
        }
        PmWoodCutter pmWoodCutter = (PmWoodCutter) plugin;
        CacheManager cacheManager = pmWoodCutter.getCacheManager();
        if (args.length == 1 && sender instanceof Player) {
            sendPlayerInfo((Player) sender, cacheManager, Messages.PLAYER_INFO);
        } else if (args.length > 1) {
            OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(args[1]);
            Player player = offlinePlayer.getPlayer();
            if (player == null) {
                ConfigurationSection section = pmWoodCutter.getConfigManager().getData().getConfigurationSection("players." + offlinePlayer.getName());
                if (section == null) {
                    Messages.NO_PLAYER_INFO.send(sender);
                    return;
                }
                Set<String> stringSet = new HashSet<>();
                for (String key : section.getKeys(false)) {
                    Object object = section.get(key);
                    if (object == null) {
                        continue;
                    }
                    stringSet.add(object.toString());
                }
                Messages.PLAYER_INFO.send(sender, stringSet);
            } else {
                sendPlayerInfo(player, cacheManager, Messages.PLAYER_INFO);
            }
        }
    }

    private static void sendPlayerInfo(Player player, CacheManager cacheManager, Messages message) {
        if (cacheManager.isPlayerHasCache(player)) {
            PlayerCache playerCache = cacheManager.getPlayerCache(player);
            message.send(player, playerCache.getAward(), playerCache.getCurrentWood(), playerCache.getTotalWood(),
                playerCache.getMoneyPerTree(), playerCache.getCurrentTree(), playerCache.getMaxTree(), playerCache.getTotalTree(),
                playerCache.getPerTreeAward(), playerCache.getPerItemSellLimit(), playerCache.getLvl(), playerCache.getCurrentExp(),
                playerCache.getPerWoodExp(), playerCache.getNextLvlExp(), playerCache.getTotalExp(), playerCache.getLocalBoosterTime());
        } else {
            Messages.NO_PLAYER_INFO.send(player);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, Command command, String... args) {
        if (args.length == 2) {
            return Arrays.stream(plugin.getServer().getOfflinePlayers()).map(OfflinePlayer::getName).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
