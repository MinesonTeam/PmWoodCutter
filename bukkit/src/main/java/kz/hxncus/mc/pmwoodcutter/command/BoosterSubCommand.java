package kz.hxncus.mc.pmwoodcutter.command;

import kz.hxncus.mc.pmwoodcutter.PmWoodCutter;
import kz.hxncus.mc.pmwoodcutter.config.Messages;
import kz.hxncus.mc.pmwoodcutter.manager.BoosterManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ICommand.CommandInfo(subCommand = "booster", permission = "booster")
public class BoosterSubCommand extends AbstractCommand<CommandSender> {
    public BoosterSubCommand(JavaPlugin plugin) {
        super(plugin, CommandSender.class);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String... args) {
        if (!(plugin instanceof PmWoodCutter)) {
            return;
        }
        if (args.length > 1) {
            PmWoodCutter pmWoodCutter = (PmWoodCutter) plugin;
            BoosterManager boosterManager = pmWoodCutter.getBoosterManager();
            if (args[1].equalsIgnoreCase("local")) {
                OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(args[2]);
                String playerName = offlinePlayer.getName();
                YamlConfiguration dataConfig = pmWoodCutter.getConfigManager().getData();
                long localBoosterTime;
                Player player = offlinePlayer.getPlayer();
                if (player == null) {
                    localBoosterTime = dataConfig.getLong("players." + playerName + ".local_booster_time", 0);
                } else {
                    localBoosterTime = boosterManager.getLocalBoosterTime(player);
                }
                if (args.length == 3) {
                    Messages.LOCAL_BOOSTER_INFO.send(sender, playerName, localBoosterTime);
                } else {
                    int additionTime;
                    try {
                        additionTime = Integer.parseInt(args[3]);
                    } catch (NumberFormatException e) {
                        Messages.NUMBER_FORMAT.send(sender, args[3]);
                        return;
                    }
                    if (player == null) {
                        dataConfig.set("players." + playerName + ".local_booster_time", localBoosterTime + additionTime);
                        pmWoodCutter.getConfigManager().saveDataConfig();
                    } else {
                        boosterManager.addLocalBoosterTime(player, additionTime);
                    }
                    Messages.LOCAL_BOOSTER_ADDED.send(sender, playerName, additionTime);
                }
            } else if (args[1].equalsIgnoreCase("global")) {
                if (args.length == 2) {
                    Messages.GLOBAL_BOOSTER_INFO.send(sender, boosterManager.getGlobalBoosterTime());
                } else {
                    int additionTime;
                    try {
                        additionTime = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        Messages.NUMBER_FORMAT.send(sender, args[2]);
                        return;
                    }
                    boosterManager.addGlobalBoosterTime(additionTime);
                    Messages.GLOBAL_BOOSTER_ADDED.send(sender, additionTime);
                }
            }
        } else {
            HelpSubCommand.sendHelpMessage(sender, label);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, Command command, String... args) {
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("local")) {
                if (args.length == 3) {
                    return Arrays.stream(plugin.getServer().getOfflinePlayers()).map(OfflinePlayer::getName).collect(Collectors.toList());
                } else if (args.length == 4) {
                    return Collections.singletonList("<time>");
                }
            } else if (args[1].equalsIgnoreCase("global")) {
                if (args.length == 3) {
                    return Collections.singletonList("<time>");
                }
            }
        }
        return Collections.emptyList();
    }
}
