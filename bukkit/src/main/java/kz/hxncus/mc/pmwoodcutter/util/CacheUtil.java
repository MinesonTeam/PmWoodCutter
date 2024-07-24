package kz.hxncus.mc.pmwoodcutter.util;

import kz.hxncus.mc.pmwoodcutter.PmWoodCutter;
import kz.hxncus.mc.pmwoodcutter.cache.PlayerCache;
import kz.hxncus.mc.pmwoodcutter.config.Settings;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.ConfigurationSection;

@UtilityClass
public class CacheUtil {
    public int getRandomChopAmount() {
        int minChopAmount = Settings.MIN_CHOP_AMOUNT.toNumber().intValue();
        int maxChopAmount = Settings.MAX_CHOP_AMOUNT.toNumber().intValue();
        return Constants.RANDOM.nextInt(minChopAmount, maxChopAmount + 1);
    }

    public ConfigurationSection getLvlSection(int lvl) {
        return PmWoodCutter.get().getConfigManager().getSettings().getConfigurationSection("lvl." + lvl);
    }

    public boolean isNextLvlAvailable(int lvl) {
        return getLvlSection(lvl) != null;
    }

    public void upgradeToNextLvl(PlayerCache playerCache) {
        playerCache.setLvl(playerCache.getLvl() + 1);
        playerCache.setCurrentExp(playerCache.getCurrentExp() - playerCache.getNextLvlExp());

        ConfigurationSection lvlSection = getLvlSection(playerCache.getLvl());
        ConfigurationSection defaultPlayerDataSection = Settings.DEFAULT_PLAYER_DATA.toConfigSection();

        playerCache.setAward(lvlSection.getDouble("award", defaultPlayerDataSection.getDouble("award")));
        playerCache.setMoneyPerTree(lvlSection.getDouble("money_per_tree", defaultPlayerDataSection.getDouble("money_per_tree")));
        playerCache.setMaxTree(lvlSection.getInt("max_tree", defaultPlayerDataSection.getInt("max_tree")));
        playerCache.setPerTreeAward(lvlSection.getInt("per_tree_award", defaultPlayerDataSection.getInt("per_tree_award")));
        playerCache.setPerItemSellLimit(lvlSection.getInt("per_item_sell_limit", defaultPlayerDataSection.getInt("per_item_sell_limit")));
        playerCache.setPerWoodExp(lvlSection.getDouble("per_wood_exp", defaultPlayerDataSection.getDouble("per_wood_exp")));
        playerCache.setNextLvlExp(lvlSection.getDouble("next_lvl_exp", defaultPlayerDataSection.getDouble("next_lvl_exp")));
    }
}
