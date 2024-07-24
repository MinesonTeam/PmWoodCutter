package kz.hxncus.mc.pmwoodcutter.command;

import kz.hxncus.mc.pmwoodcutter.config.Messages;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractCommand<T extends CommandSender> implements ICommand<T> {
    public final JavaPlugin plugin;
    private final Class<T> type;
    private final CommandInfo commandInfo;
    private final AbstractCommand<?>[] subCommands;

    protected AbstractCommand(JavaPlugin plugin, Class<T> type, AbstractCommand<?>... subCommands) {
        this.plugin = plugin;
        this.type = type;
        this.subCommands = subCommands;
        this.commandInfo = getClass().getDeclaredAnnotation(CommandInfo.class);
        if (commandInfo == null) {
            plugin.getLogger().severe("One of the commands is not initialized correctly. Contact the plugin developer.");
            return;
        }
        String command = commandInfo.command();
        if (command.isEmpty()) {
            return;
        }
        PluginCommand pluginCommand = this.plugin.getCommand(command);
        if (pluginCommand == null) {
            plugin.getLogger().severe("The command " + command + " not found. Contact the plugin developer.");
            return;
        }
        pluginCommand.setExecutor(this);
        pluginCommand.setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull ... args) {
        for (AbstractCommand<?> subCommand : Arrays.stream(subCommands).sorted((a, b) -> b.commandInfo.subCommand().length() - a.commandInfo.subCommand().length()).collect(Collectors.toList())) {
            if (isExecutable(subCommand.commandInfo.subCommand(), args)) {
                execute(sender, command, label, subCommand, args);
                return true;
            }
        }
        execute(sender, command, label, this, args);
        return true;
    }

    private boolean isExecutable(String subCommand, String... args) {
        return String.join(" ", args).toLowerCase(Locale.ENGLISH).startsWith(subCommand.toLowerCase(Locale.ENGLISH));
    }

    private <R extends CommandSender> void execute(CommandSender sender, Command command, String label, AbstractCommand<R> abstractCommand, String... args) {
        CommandInfo subCommandInfo = abstractCommand.commandInfo;
        String permission = commandInfo.permission() + ".command." + subCommandInfo.permission();
        if (!subCommandInfo.permission().isEmpty() && !sender.hasPermission(permission)) {
            Messages.NOT_ENOUGH_PERMISSION.send(sender, permission);
        } else if (abstractCommand.type.isAssignableFrom(sender.getClass())) {
            abstractCommand.execute((R) sender, command, label, args);
        } else {
            Messages.INCORRECT_SENDER.send(sender, abstractCommand.type.getSimpleName());
        }
    }

    @Override
    public void execute(T sender, Command command, String label, String... args) {
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, String @NonNull ... args) {
        if (subCommands != null) {
            List<String> result = new ArrayList<>();
            for (AbstractCommand<?> subCommand : subCommands) {
                CommandInfo subCommandInfo = subCommand.commandInfo;
                String permission = commandInfo.permission() + ".command." + subCommandInfo.permission();
                if (!subCommandInfo.permission().isEmpty() && !sender.hasPermission(permission)) {
                    continue;
                }
                result.addAll(complete((T) sender, command, subCommand, args));
            }
            return filter(result, args);
        }
        return Collections.emptyList();
    }

    private List<String> filter(List<String> list, String... args) {
        String last = args[args.length - 1];
        List<String> result = new ArrayList<>();
        for (final String arg : list) {
            if (arg.toLowerCase(Locale.ENGLISH).startsWith(last.toLowerCase(Locale.ENGLISH))) {
                result.add(arg);
            }
        }
        return result;
    }

    @Override
    public List<String> complete(T sender, Command command, String... args) {
        return Collections.emptyList();
    }

    public <R extends CommandSender> List<String> complete(T sender, Command command, AbstractCommand<R> subCommand, String... args) {
        List<String> result = new ArrayList<>();
        String subCommandString = subCommand.commandInfo.subCommand();
        String[] split = subCommandString.split(" ");
        if (args.length == split.length && subCommandString.startsWith(String.join(" ", args))) {
            result.add(split[split.length - 1]);
        }
        if (subCommand.type.isAssignableFrom(sender.getClass())) {
            if (String.join(" ", args).replace(" ", "").endsWith(split[split.length - 1])) {
                result.addAll(subCommand.complete((R) sender, command, args));
            }
        }
        return result;
    }

    @Override
    public @NonNull Plugin getPlugin() {
        return plugin;
    }
}