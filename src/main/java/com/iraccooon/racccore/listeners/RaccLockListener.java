package com.iraccooon.racccore.listeners;

import com.iraccooon.racccore.storage.RaccMapLockStorage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CartographyInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import java.util.UUID;

public class RaccLockListener implements Listener{
    private final RaccMapLockStorage storage;

    public RaccLockListener(RaccMapLockStorage storage){
        this.storage = storage;
    }

    //blocks duplication in crafting environment (inv 2x2 and crafting table 3x3)
    @EventHandler
    public void onMapCraft(PrepareItemCraftEvent event){
        Player player = (Player) event.getView().getPlayer();
        for(ItemStack item : event.getInventory().getMatrix()){
            if(isLockedMap(item) && !isOwner(player, item)){
                event.getInventory().setResult(new ItemStack(Material.AIR));
                return;
            }
        }
    }

    //blocks duplication in cartography table
    @EventHandler
    public void onCartography(InventoryClickEvent event){
        Player player = (Player) event.getView().getPlayer();
        if(!(event.getInventory() instanceof CartographyInventory inv)) return;
        for(ItemStack item : inv.getContents()){
            if(isLockedMap(item) && !isOwner(player, item)){
                inv.setItem(2, new ItemStack(Material.AIR)); // slot 2 is the result slot
                return;
            }
        }
    }

    private boolean isLockedMap(ItemStack item){
        if(item == null || item.getType() != Material.FILLED_MAP) return false;
        MapMeta meta = (MapMeta) item.getItemMeta();
        return meta != null && meta.getMapView() != null && meta.getMapView().isLocked();
    }

    private boolean isOwner(Player player, ItemStack item){
        MapMeta meta = (MapMeta) item.getItemMeta();
        int mapId = meta.getMapView().getId();
        UUID owner = storage.getOwner(mapId);
        return owner == null || owner.equals(player.getUniqueId()) || player.hasPermission("raccmaplock.admin");
    }

}
