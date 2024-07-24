package kz.hxncus.mc.pmwoodcutter.command;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

@ICommand.CommandInfo(command = "pmwoodcutter", permission = "pmwdc")
public class PMWDCCommand extends AbstractCommand<CommandSender> {
    public PMWDCCommand(JavaPlugin plugin) {
        super(plugin, CommandSender.class, new ReloadSubCommand(plugin), new HelpSubCommand(plugin), new SalarySubCommand(plugin),
            new SetLvlSubCommand(plugin), new BoosterSubCommand(plugin), new InfoSubCommand(plugin), new LimitSubCommand(plugin));
    }
}
