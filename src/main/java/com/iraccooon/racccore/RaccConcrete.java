package com.iraccooon.racccore;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RaccConcrete implements Listener {

    private final JavaPlugin plugin;
    //tracks item entity UUIDs dropped by players with permission
    private final Set<UUID> trackedItems = new HashSet<>();

    public RaccConcrete(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    //step 1: player drops concrete powder — check permission and track the item
    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        Item item = event.getItemDrop();

        if(!item.getItemStack().getType().name().endsWith("_CONCRETE_POWDER")) return;
        if(!player.hasPermission("raccconcrete.use")) return;

        trackedItems.add(item.getUniqueId());
    }

    //step 2: item spawns — start watching if it's tracked
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event){
        Item item = event.getEntity();
        if(!trackedItems.contains(item.getUniqueId())) return;

        startWatchingItem(item);
    }

    private void startWatchingItem(Item item){
        new BukkitRunnable() {
            @Override
            public void run() {
                //stop if item was picked up or despawned
                if(item.isDead() || !item.isValid()){
                    trackedItems.remove(item.getUniqueId());
                    cancel();
                    return;
                }

                Block block = item.getLocation().getBlock();
                if(block.getType() == Material.WATER){
                    String concreteName = item.getItemStack().getType().name().replace("_POWDER", "");
                    Material concrete = Material.valueOf(concreteName);
                    int amount = item.getItemStack().getAmount();
                    item.setItemStack(new ItemStack(concrete, amount));
                    trackedItems.remove(item.getUniqueId());
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }
}