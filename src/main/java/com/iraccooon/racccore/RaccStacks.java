package com.iraccooon.racccore;

import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import java.util.Set;

import java.util.List;

public class RaccStacks implements Listener, CommandExecutor, TabCompleter {
    private boolean dropperEnabled;
    private boolean dispenserEnabled;
    private final JavaPlugin plugin;

    public RaccStacks(JavaPlugin plugin){
        this.plugin = plugin;
        this.dropperEnabled = plugin.getConfig().getBoolean("RaccDroppers-enabled");
        this.dispenserEnabled = plugin.getConfig().getBoolean("RaccDispensers-enabled");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void reload(){
        this.dropperEnabled = plugin.getConfig().getBoolean("RaccDroppers-enabled");
        this.dispenserEnabled = plugin.getConfig().getBoolean("RaccDispensers-enabled");
    }


    // useable items for dispenser that should be ignored by onDispense
    private static final Set<Material> DISPENSER_USE_ITEMS = Set.of(
            Material.WATER_BUCKET,
            Material.LAVA_BUCKET,
            Material.POWDER_SNOW_BUCKET,
            Material.BUCKET,
            // tools/utility
            Material.FLINT_AND_STEEL,
            Material.BONE_MEAL,
            Material.SHEARS,
            // explosives
            Material.TNT,
            Material.FIRE_CHARGE,
            // projectiles
            Material.ARROW,
            Material.SPECTRAL_ARROW,
            Material.TIPPED_ARROW,
            Material.FIREWORK_ROCKET,
            Material.EGG,
            Material.SNOWBALL,
            Material.ENDER_PEARL,
            // potions
            Material.SPLASH_POTION,
            Material.LINGERING_POTION
    );



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(!sender.hasPermission("raccstacks.admin")){
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        if(args.length == 0){
            sender.sendMessage("§eUsage: /raccstacks toggle <droppers | dispensers | all>");
            return true;
        }
        if(args.length == 1){
            switch(args[0].toLowerCase()){
                case "toggle":
                    // Switch all
                    toggleAll(sender);
                    return true;
                default:
                    sender.sendMessage("§eUsage: /raccstacks toggle <droppers | dispensers | all>");
                    return true;
            }
        }
        if(args.length == 2){
            switch(args[1].toLowerCase()){
                case "droppers":
                    dropperEnabled = !dropperEnabled;
                    plugin.getConfig().set("RaccDroppers-enabled", dropperEnabled);
                    plugin.saveConfig();
                    sender.sendMessage("§aRaccDroppers are now "+(dropperEnabled ? "§aenabled" : "§cdisabled")+"§a");
                    return true;
                case "dispensers":
                    dispenserEnabled = !dispenserEnabled;
                    plugin.getConfig().set("RaccDispensers-enabled", dispenserEnabled);
                    plugin.saveConfig();
                    sender.sendMessage("§aRaccDispensers are now "+(dispenserEnabled ? "§aenabled" : "§cdisabled")+"§a");
                    return true;
                case "all":
                    toggleAll(sender);
                    return true;
                default:
                    sender.sendMessage("§eUsage: /raccstacks toggle <droppers | dispensers | all>");
                    return true;
            }
        }
        return true;
    }


    @EventHandler
    public void onDispense(BlockDispenseEvent event){
        Material blockType = event.getBlock().getType();

        // skip if neither is enabled or wrong block type
        if(blockType != Material.DROPPER && blockType != Material.DISPENSER) return;
        if(blockType == Material.DROPPER && !dropperEnabled) return;
        if(blockType == Material.DISPENSER && !dispenserEnabled) return;

        ItemStack dispensed = event.getItem();
        Material itemType = dispensed.getType();

        // dispenser-only: skip items that are "used" rather than ejected
        if(blockType == Material.DISPENSER && (DISPENSER_USE_ITEMS.contains(itemType) || itemType.name().endsWith("_SPAWN_EGG"))) return;

        // skip non-stackable items
        int maxStack = itemType.getMaxStackSize();
        if(maxStack <= 1) return;

        // get inventory (oth Dropper and Dispenser implement Container)
        Container container = (Container) event.getBlock().getState();
        Inventory inv = container.getInventory();

        // find the slot containing this item and eject the full stack
        for(int i = 0; i < inv.getSize(); i++){
            ItemStack slot = inv.getItem(i);
            if(slot != null && slot.isSimilar(dispensed)){
                int actualAmount = slot.getAmount() + 1;
                int toDispense = Math.min(actualAmount, maxStack);

                ItemStack newItem = dispensed.clone();
                newItem.setAmount(toDispense);
                event.setItem(newItem);

                int leftover = actualAmount - toDispense;
                final int slotIndex = i;
                final int leftoverAmount = leftover;

                // delay inventory update by 1 tick so vanilla doesn't overwrite it
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if(leftoverAmount <= 0){
                        inv.setItem(slotIndex, null);
                    } else {
                        ItemStack remaining = slot.clone();
                        remaining.setAmount(leftoverAmount);
                        inv.setItem(slotIndex, remaining);
                    }
                });
                break;
            }
        }
    }


    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
        if(args.length == 1){
            return List.of("toggle");
        }
        if(args.length == 2){
            return List.of("droppers", "dispensers", "all");
        }
        return List.of();
    }


    public void toggleAll(CommandSender sender){
        dropperEnabled = !dropperEnabled;
        dispenserEnabled = !dispenserEnabled;
        // set and save config
        plugin.getConfig().set("RaccDroppers-enabled", dropperEnabled);
        plugin.getConfig().set("RaccDispensers-enabled", dispenserEnabled);
        plugin.saveConfig();
        // Send feedback message
        sender.sendMessage("§aRaccDroppers are now "+(dropperEnabled ? "§aenabled" : "§cdisabled")+"§a");
        sender.sendMessage("§aRaccDispensers are now "+(dispenserEnabled ? "§aenabled" : "§cdisabled")+"§a");
    }
}
