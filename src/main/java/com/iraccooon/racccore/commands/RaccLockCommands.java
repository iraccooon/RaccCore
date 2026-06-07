package com.iraccooon.racccore.commands;

import com.iraccooon.racccore.storage.RaccMapLockStorage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public class RaccLockCommands implements CommandExecutor, TabCompleter{
    private final RaccMapLockStorage storage;
    private final JavaPlugin plugin;

    public RaccLockCommands(RaccMapLockStorage storage, JavaPlugin plugin){
        this.storage = storage;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(!(sender instanceof final Player player)){
            sender.sendMessage("§cOnly players can execute this command!");
            return true;
        }

        if(args.length == 0 || (!args[0].equalsIgnoreCase("lock") && !args[0].equalsIgnoreCase("unlock"))){
            player.sendMessage("§cUsage: /maplock <lock | unlock>");
            return true;
        }

        ItemStack map = player.getInventory().getItemInMainHand();
        if(map.getType() != Material.FILLED_MAP){
            player.sendMessage("§cYou must be holding a filled map!");
            return true;
        }

        MapMeta meta = (MapMeta) map.getItemMeta();
        int mapId = meta.getMapView().getId();

        if(args[0].equalsIgnoreCase("lock")){
            if(meta.getMapView().isLocked()){
                player.sendMessage("§cThis map is already locked!");
                return true;
            }

            meta.getMapView().setLocked(true);

            if(!meta.hasLore()){
                meta.setLore(List.of("§7Created by: §f" + player.getName()));
                storage.setOwner(mapId, player.getUniqueId());
                plugin.getLogger().info("Map ID " + mapId + " locked and claimed by " + player.getName());
            }

            map.setItemMeta(meta);
            player.sendMessage("§aMap locked!");

        }
        else{
            if(!meta.getMapView().isLocked()){
                player.sendMessage("§cThis map is not locked!");
                return true;
            }

            UUID owner = storage.getOwner(mapId);
            if(owner != null && !owner.equals(player.getUniqueId()) && !player.hasPermission("raccmaplock.admin")){
                player.sendMessage("§cYou don't own this map!");
                return true;
            }

            meta.getMapView().setLocked(false);
            map.setItemMeta(meta);

            if(player.hasPermission("raccmaplock.admin") && owner != null && !player.getUniqueId().equals(owner)){
                player.sendMessage("§aUnlocked a map owned by " + Bukkit.getOfflinePlayer(owner).getName());
            }
            else{
                player.sendMessage("§aMap unlocked!");
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
        if(args.length == 1) return List.of("lock", "unlock");
        return List.of();
    }
}