package kz.hxncus.mc.pmwoodcutter.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

@ICommand.CommandInfo(subCommand = "reload", permission = "reload")
public class ReloadSubCommand extends AbstractCommand<CommandSender> {
    public ReloadSubCommand(JavaPlugin plugin) {
        super(plugin, CommandSender.class);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String... args) {
        long currentTimeMillis = System.currentTimeMillis();
        plugin.onDisable();
        plugin.onEnable();
        sender.sendMessage("§aPMWoodcutter reloaded in " + (System.currentTimeMillis() - currentTimeMillis) + "ms");
    }
}
