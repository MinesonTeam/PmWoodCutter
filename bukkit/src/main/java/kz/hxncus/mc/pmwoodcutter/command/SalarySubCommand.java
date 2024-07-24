package kz.hxncus.mc.pmwoodcutter.command;

import kz.hxncus.mc.pmwoodcutter.PmWoodCutter;
import kz.hxncus.mc.pmwoodcutter.cache.CacheManager;
import kz.hxncus.mc.pmwoodcutter.cache.PlayerCache;
import kz.hxncus.mc.pmwoodcutter.config.Messages;
import kz.hxncus.mc.pmwoodcutter.manager.BoosterManager;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@ICommand.CommandInfo(subCommand = "salary", permission = "salary")
public class SalarySubCommand extends AbstractCommand<Player> {
    public SalarySubCommand(JavaPlugin plugin) {
        super(plugin, Player.class);
    }

    @Override
    public void execute(Player sender, Command command, String label, String... args) {
        if (!(plugin instanceof PmWoodCutter)) {
            return;
        }
        PmWoodCutter pmWoodCutter = (PmWoodCutter) plugin;
        CacheManager cacheManager = pmWoodCutter.getCacheManager();
        BoosterManager boosterManager = pmWoodCutter.getBoosterManager();
        PlayerCache playerCache = cacheManager.getPlayerCacheIfExists(sender);
        if (playerCache == null || playerCache.getCurrentTree() < 1) {
            Messages.NOT_ENOUGH_TREE.send(sender);
            return;
        }
        int currentTree = playerCache.getCurrentTree();
        double money = boosterManager.multiplyIfHasBooster(sender, playerCache.getMoneyPerTree() * currentTree);
        pmWoodCutter.getVault().depositPlayer(sender, money);
        playerCache.setCurrentTree(0);
        Messages.SALARY_RECEIVED.send(sender, money, currentTree);
    }
}
