package kz.hxncus.mc.pmwoodcutter.command;

import kz.hxncus.mc.pmwoodcutter.config.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

@ICommand.CommandInfo(subCommand = "help", permission = "help")
public class HelpSubCommand extends AbstractCommand<CommandSender> {
    public HelpSubCommand(JavaPlugin plugin) {
        super(plugin, CommandSender.class);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String... args) {
        sendHelpMessage(sender, label);
    }

    public static void sendHelpMessage(CommandSender sender, String label) {
        if (sender.isOp() || sender.hasPermission("*") || sender.hasPermission("pmwdc.command.*")) {
            Messages.ADMIN_HELP.send(sender, label);
        } else {
            Messages.PLAYER_HELP.send(sender, label);
        }
    }
}
