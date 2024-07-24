package kz.hxncus.mc.pmwoodcutter.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Material;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@AllArgsConstructor
public class PlayerCache {
    private final UUID uuid;
    private final Map<Material, Long> materialLongMap = new ConcurrentHashMap<>();
    private double award;
    private int currentWood;
    private int totalWood;
    private double moneyPerTree;
    private int currentTree;
    private int maxTree;
    private int totalTree;
    private int perTreeAward;
    private long perItemSellLimit;
    private int lvl;
    private double currentExp;
    private double perWoodExp;
    private double nextLvlExp;
    private double totalExp;
    private long localBoosterTime;
}
