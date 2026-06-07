package com.iraccooon.racccore.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RaccMapOwnerListener implements Listener{

    @EventHandler
    public void onMapCreate(PlayerInteractEvent event){
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = event.getItem();
        if(item == null || item.getType() != Material.MAP) return;

        //don't show message if interacting with a block
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null){
            if(event.getClickedBlock().getType().isInteractable()) return;
        }

        event.getPlayer().sendMessage("§eYou created a map! Use §f/maplock §eto protect it and show ownership.");
    }
}