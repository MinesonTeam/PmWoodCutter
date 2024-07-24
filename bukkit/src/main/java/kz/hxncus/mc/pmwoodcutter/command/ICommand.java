package kz.hxncus.mc.pmwoodcutter.command;

import org.bukkit.command.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

public interface ICommand<T extends CommandSender> extends CommandExecutor, TabCompleter, PluginIdentifiableCommand {
    void execute(T sender, Command command, String label, String... args);

    List<String> complete(T sender, Command command, String... args);

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface CommandInfo {
        String command() default "";
        String subCommand() default "";
        String permission() default "";
    }
}
