package kz.hxncus.mc.pmwoodcutter.manager;

import kz.hxncus.mc.pmwoodcutter.PmWoodCutter;
import lombok.EqualsAndHashCode;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode
public class BukkitTaskManager {
    private static PmWoodCutter plugin;
    private final Set<BukkitTask> bukkitTaskSet = new HashSet<>();

    public BukkitTaskManager(PmWoodCutter plugin) {
        BukkitTaskManager.plugin = plugin;
        registerTasks();
    }

    private void registerTasks() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> plugin.getBoosterManager().runActionBarBoosterTask(), 0L, 20L);
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> plugin.getCacheManager().runTreeRegenerateTask(), 0L, 20L);
    }

    public void addBukkitTask(BukkitTask task) {
        bukkitTaskSet.add(task);
    }

    public void cancelAllTasks() {
        for (BukkitTask bukkitTask : bukkitTaskSet) {
            bukkitTask.cancel();
        }
        bukkitTaskSet.clear();
    }
}
