package kz.hxncus.mc.pmwoodcutter.command;

import kz.hxncus.mc.pmwoodcutter.PmWoodCutter;
import kz.hxncus.mc.pmwoodcutter.cache.PlayerCache;
import kz.hxncus.mc.pmwoodcutter.config.ConfigManager;
import kz.hxncus.mc.pmwoodcutter.config.Messages;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

@ICommand.CommandInfo(subCommand = "limit", permission = "limit")
public class LimitSubCommand extends AbstractCommand<CommandSender> {
    private PmWoodCutter pmWoodCutter;

    public LimitSubCommand(JavaPlugin plugin) {
        super(plugin, CommandSender.class);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String... args) {
        if (!(plugin instanceof PmWoodCutter)) {
            return;
        }
        this.pmWoodCutter = (PmWoodCutter) plugin;
        if (args.length == 5) {
            String action = args[1];
            Material material = Material.getMaterial(args[3].toUpperCase(Locale.ENGLISH));
            if (material == null) {
                Messages.MATERIAL_NOT_FOUND.send(sender, args[3]);
            }
            int amount;
            try {
                amount = Integer.parseInt(args[4]);
            } catch (RuntimeException e) {
                Messages.NUMBER_FORMAT.send(sender, args[4]);
                return;
            }
            OfflinePlayer offlinePlayer = pmWoodCutter.getServer().getOfflinePlayer(args[2]);
            switch (action) {
                case "add":
                    addLimit(offlinePlayer, material, amount);
                    break;
                case "remove":
                    removeLimit(offlinePlayer, material, amount);
                    break;
                case "set":
                    setLimit(offlinePlayer, material, amount);
                    break;
                default:
                    HelpSubCommand.sendHelpMessage(sender, label);
                    return;
            }
        } else {
            HelpSubCommand.sendHelpMessage(sender, label);
        }
    }

    private void addLimit(OfflinePlayer offlinePlayer, Material material, long amount) {
        Player player = offlinePlayer.getPlayer();
        if (player == null) {
            ConfigManager configManager = pmWoodCutter.getConfigManager();
            YamlConfiguration dataConfig = configManager.getData();
            ConfigurationSection limitsSection = dataConfig.getConfigurationSection("players." + offlinePlayer.getName() + ".sell_limits");
            if (limitsSection == null) {
                limitsSection = dataConfig.createSection("players." + offlinePlayer.getName() + ".sell_limits");
            }
            long defAmount = limitsSection.getLong(material.name(), 0);
            limitsSection.set(material.name(), defAmount + amount);
            configManager.saveDataConfig();
        } else {
            PlayerCache playerCache = pmWoodCutter.getCacheManager().getPlayerCache(player);
            Map<Material, Long> materialDoubleMap = playerCache.getMaterialLongMap();

            Long defAmount = materialDoubleMap.getOrDefault(material, 0L);
            materialDoubleMap.put(material, amount + defAmount);
        }
    }

    private void removeLimit(OfflinePlayer offlinePlayer, Material material, long amount) {
        Player player = offlinePlayer.getPlayer();
        if (player == null) {
            ConfigManager configManager = pmWoodCutter.getConfigManager();
            YamlConfiguration dataConfig = configManager.getData();
            ConfigurationSection limitsSection = dataConfig.getConfigurationSection("players." + offlinePlayer.getName() + ".sell_limits");
            if (limitsSection == null) {
                limitsSection = dataConfig.createSection("players." + offlinePlayer.getName() + ".sell_limits");
            }
            long defAmount = limitsSection.getLong(material.name(), 0);
            limitsSection.set(material.name(), defAmount - amount);
            configManager.saveDataConfig();
        } else {
            PlayerCache playerCache = pmWoodCutter.getCacheManager().getPlayerCache(player);
            Map<Material, Long> materialDoubleMap = playerCache.getMaterialLongMap();

            Long defAmount = materialDoubleMap.getOrDefault(material, 0L);
            materialDoubleMap.put(material, amount - defAmount);
        }
    }

    private void setLimit(OfflinePlayer offlinePlayer, Material material, long amount) {
        Player player = offlinePlayer.getPlayer();
        if (player == null) {
            ConfigManager configManager = pmWoodCutter.getConfigManager();
            YamlConfiguration dataConfig = configManager.getData();
            ConfigurationSection limitsSection = dataConfig.getConfigurationSection("players." + offlinePlayer.getName() + ".sell_limits");
            if (limitsSection == null) {
                limitsSection = dataConfig.createSection("players." + offlinePlayer.getName() + ".sell_limits");
            }
            limitsSection.set(material.name(), amount);
            configManager.saveDataConfig();
        } else {
            pmWoodCutter.getCacheManager().getPlayerCache(player)
                        .getMaterialLongMap().put(material, amount);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, Command command, String... args) {
        if (args.length == 2) {
            return Arrays.asList("add", "remove", "set");
        } else if (args.length == 3) {
            return Arrays.stream(plugin.getServer().getOfflinePlayers()).map(OfflinePlayer::getName).collect(Collectors.toList());
        } else if (args.length == 4) {
            return Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList());
        } else if (args.length == 5) {
            return Collections.singletonList("{amount}");
        }
        return Collections.emptyList();
    }
}
