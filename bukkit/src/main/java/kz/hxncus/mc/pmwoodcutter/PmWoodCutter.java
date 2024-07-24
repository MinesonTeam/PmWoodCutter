package kz.hxncus.mc.pmwoodcutter;

import kz.hxncus.mc.pmwoodcutter.animation.AnimationManager;
import kz.hxncus.mc.pmwoodcutter.cache.CacheManager;
import kz.hxncus.mc.pmwoodcutter.color.ColorManager;
import kz.hxncus.mc.pmwoodcutter.command.PMWDCCommand;
import kz.hxncus.mc.pmwoodcutter.config.ConfigManager;
import kz.hxncus.mc.pmwoodcutter.listener.PlayerListener;
import kz.hxncus.mc.pmwoodcutter.manager.BoosterManager;
import kz.hxncus.mc.pmwoodcutter.manager.BukkitTaskManager;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class PmWoodCutter extends JavaPlugin {
    private static PmWoodCutter instance;
    private Economy vault;
    private ColorManager colorManager;
    private CacheManager cacheManager;
    private ConfigManager configManager;
    private AnimationManager animationManager;
    private BoosterManager boosterManager;
    private BukkitTaskManager bukkitTaskManager;

    public PmWoodCutter() {
        instance = this;
    }
    
    public static PmWoodCutter get() {
        return instance;
    }
    
    @Override
    public void onEnable() {
        registerPlugins();
        registerManagers();
        registerListeners(getServer().getPluginManager());
        registerCommands();
    }

    @Override
    public void onDisable() {
        unregisterManagers();
    }

    private void registerPlugins() {
        Plugin pluginVault = getServer().getPluginManager().getPlugin("Vault");
        if (pluginVault == null || !pluginVault.isEnabled()) {
            return;
        }
        RegisteredServiceProvider<Economy> economyServiceProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyServiceProvider == null) {
            return;
        }
        vault = economyServiceProvider.getProvider();
    }

    private void registerManagers() {
        boosterManager = new BoosterManager(this);
        colorManager = new ColorManager(this);
        configManager = new ConfigManager(this);
        cacheManager = new CacheManager(this);
        animationManager = new AnimationManager(this);
        bukkitTaskManager = new BukkitTaskManager(this);
    }

    private void registerListeners(PluginManager pluginManager) {
        pluginManager.registerEvents(new PlayerListener(this), this);
        pluginManager.registerEvents(animationManager, this);
    }

    private void registerCommands() {
        new PMWDCCommand(this);
    }

    private void unregisterManagers() {
        bukkitTaskManager.cancelAllTasks();
        cacheManager.unregister();
    }
}
