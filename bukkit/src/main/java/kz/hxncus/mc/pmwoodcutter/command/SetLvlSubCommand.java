package kz.hxncus.mc.pmwoodcutter.command;

import kz.hxncus.mc.pmwoodcutter.PmWoodCutter;
import kz.hxncus.mc.pmwoodcutter.config.ConfigManager;
import kz.hxncus.mc.pmwoodcutter.config.Messages;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ICommand.CommandInfo(subCommand = "setlvl", permission = "setlvl")
public class SetLvlSubCommand extends AbstractCommand<CommandSender> {
    public SetLvlSubCommand(JavaPlugin plugin) {
        super(plugin, CommandSender.class);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String... args) {
        if (args.length == 3) {
            OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(args[1]);
            int settingLvl;
            try {
                settingLvl = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                Messages.NUMBER_FORMAT.send(sender, args[2]);
                return;
            }
            PmWoodCutter pmWoodCutter = (PmWoodCutter) plugin;
            Player player = offlinePlayer.getPlayer();
            if (player == null) {
                ConfigManager configManager = pmWoodCutter.getConfigManager();
                configManager.getData().set("players." + offlinePlayer.getName() + ".lvl", settingLvl);
                configManager.saveDataConfig();
            } else {
                pmWoodCutter.getCacheManager().getPlayerCache(player).setLvl(settingLvl);
            }
            Messages.LEVEL_SET.send(sender, args[1], args[2]);
        } else {
            HelpSubCommand.sendHelpMessage(sender, label);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, Command command, String... args) {
        if (args.length == 2) {
            return Arrays.stream(plugin.getServer().getOfflinePlayers()).map(OfflinePlayer::getName).collect(Collectors.toList());
        } else if (args.length == 3) {
            return new ArrayList<>(((PmWoodCutter) plugin).getConfigManager().getSettings().getConfigurationSection("lvl").getKeys(false));
        }
        return Collections.emptyList();
    }
}
