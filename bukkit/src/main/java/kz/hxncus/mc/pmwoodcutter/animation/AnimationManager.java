package kz.hxncus.mc.pmwoodcutter.animation;

import kz.hxncus.mc.pmwoodcutter.PmWoodCutter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@EqualsAndHashCode
public class AnimationManager implements Listener {
    private static PmWoodCutter plugin;
    private final Map<String, Animation> stringAnimationMap = new ConcurrentHashMap<>();

    public AnimationManager(PmWoodCutter plugin) {
        AnimationManager.plugin = plugin;
        stringAnimationMap.put("treefall", new TreeFallAnimation(plugin));
    }

    // Tree fall animation falling blocks cancel
    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.FALLING_BLOCK) {
            return;
        }
        if (plugin.getCacheManager().getFallingBlockSet().remove(entity.getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
