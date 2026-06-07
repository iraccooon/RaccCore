package com.iraccooon.racccore;

import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.meta.Damageable;

public class RaccConcreteShovel implements Listener {
    private final JavaPlugin plugin;

    public RaccConcreteShovel(JavaPlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onShovelUse(PlayerItemDamageEvent event){
        //get the item in the player's hand
        ItemStack tool = event.getItem();

        //check if custom shovel
        ItemMeta meta = tool.getItemMeta();
        if(meta == null || !meta.hasDisplayName()) return;
        if(!meta.getDisplayName().equals("§bConcrete Shovel")) return;

        String durabilityConfig = plugin.getConfig().getString("concrete-shovel-durability");
        if (durabilityConfig.equalsIgnoreCase("false")) return; // unbreakable

        event.setCancelled(true);

        Damageable damageable = (Damageable) meta;
        int maxDurability = Integer.parseInt(durabilityConfig);
        int scaledIncrement = (int) Math.ceil(1561.0 / maxDurability);
        int newDamage = damageable.getDamage() + scaledIncrement;

        if(newDamage >= 1561){
            tool.setAmount(0); // removes the item
            return;
        }
        else{
            damageable.setDamage(newDamage);
        }

        tool.setItemMeta((ItemMeta) damageable);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        //get the item in the player's hand
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();

        //check if custom shovel
        ItemMeta meta = tool.getItemMeta();
        if(meta == null || !meta.hasDisplayName()) return;
        if(!meta.getDisplayName().equals("§bConcrete Shovel")) return;

        //check if the broken block is concrete powder
        if(!event.getBlock().getType().name().endsWith("_CONCRETE_POWDER")) return;

        //cancel normal drops
        event.setDropItems(false);

        //drop converted concrete
        String concreteName = event.getBlock().getType().name().replace("_POWDER", "");
        Material concrete = Material.valueOf(concreteName);
        Location loc = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
        event.getBlock().getWorld().dropItemNaturally(loc, new ItemStack(concrete, 1));
    }
}
