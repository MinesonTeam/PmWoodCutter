package kz.hxncus.mc.pmwoodcutter.config;

import kz.hxncus.mc.pmwoodcutter.PmWoodCutter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public enum Messages {
    PREFIX("general.prefix"), UPDATING_CONFIG_KEY("general.updating_config"), REMOVING_CONFIG_KEY("general.removing_config"),
    REMAINING_CHOP_TIME("general.remaining_chop_time"), NOT_ENOUGH_PERMISSION("command.not_enough_permission"),
    INCORRECT_SENDER("command.incorrect_sender"), PLAYER_HELP("command.player_help"), ADMIN_HELP("command.admin_help"),
    LOCAL_BOOSTER("actionbar.local_booster"), GLOBAL_BOOSTER("actionbar.global_booster"), BOOSTER_FORMAT("actionbar.format"),
    AWARDED_MONEY("general.awarded_money"), TREE_CHOPPED("general.tree_chopped"), LEVEL_UP("general.level_up"),
    INVENTORY_FULL("general.inventory_full"), NOT_ENOUGH_TREE("general.not_enough_tree"), SALARY_RECEIVED("general.salary_received"),
    NUMBER_FORMAT("command.number_format"), LEVEL_SET("general.level_set"), PLAYER_INFO("general.player_info"),
    NO_PLAYER_INFO("general.no_player_info"), LOCAL_BOOSTER_ADDED("general.local_booster_added"), GLOBAL_BOOSTER_ADDED("general.global_booster_added"),
    LOCAL_BOOSTER_INFO("general.local_booster_info"), GLOBAL_BOOSTER_INFO("general.global_booster_info"), MATERIAL_NOT_FOUND("general.material_not_found"),;

    private final String path;

    Messages(final String path) {
        this.path = path;
    }

    @NonNull
    public Object getValue() {
        Object obj = getLanguages().get(path);
        return obj == null ? "" : obj;
    }

    @NonNull
    public Object getValue(Object def) {
        return getLanguages().get(path, def);
    }

    @NonNull
    public String toPath() {
        return path;
    }

    @Override
    public String toString() {
        return process(getValue().toString());
    }

    public String toString(String def) {
        return process(getValue(def).toString());
    }

    public String toString(Object... args) {
        return process(getValue().toString(), args);
    }

    public List<String> toStringList() {
        List<String> stringList = getLanguages().getStringList(path);
        stringList.replaceAll(this::process);
        return stringList;
    }

    public String process(String message) {
        return colorize(format(message));
    }

    public String process(String message, Object... args) {
        return colorize(format(message, args));
    }

    private String colorize(String message) {
        return PmWoodCutter.get().getColorManager().process(message);
    }

    private String format(String message, final Object... args) {
        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + i + "}", args[i].toString());
        }
        return message.replace("{PREFIX}", colorize(PREFIX.getValue().toString()));
    }

    private void send(final CommandSender sender, final String msg, final Object... args) {
        // No need to send an empty or null message.
        if (msg == null || msg.isEmpty()) {
            return;
        }
        sender.sendMessage(process(msg, args));
    }

    private void sendList(final CommandSender sender, final Object... args) {
        for (String str : toStringList()) {
            send(sender, str, args);
        }
    }

    public void send(final CommandSender sender, final Object... args) {
        if (getValue() instanceof List<?>) {
            sendList(sender, args);
        } else {
            send(sender, toString(), args);
        }
    }

    public void log(final Object... args) {
        if (getValue() instanceof List<?>) {
            sendList(Bukkit.getConsoleSender(), args);
        } else {
            send(Bukkit.getConsoleSender(), toString(), args);
        }
    }

    public YamlConfiguration getLanguages() {
        return PmWoodCutter.get().getConfigManager().getLanguages();
    }
}
