package com.iraccooon.racccore;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.block.Furnace;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RaccFurnaces implements Listener, CommandExecutor, TabCompleter {
    private boolean furnaceEnabled;
    private boolean blastFurnaceEnabled;
    private boolean smokerEnabled;
    private final JavaPlugin plugin;

    // update constructor to store plugin
    public RaccFurnaces(JavaPlugin plugin){
        this.plugin = plugin;
        this.furnaceEnabled = plugin.getConfig().getBoolean("RaccFurnaces-enabled");
        this.blastFurnaceEnabled = plugin.getConfig().getBoolean("RaccBlastFurnaces-enabled");
        this.smokerEnabled = plugin.getConfig().getBoolean("RaccSmokers-enabled");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void reload(){
        this.furnaceEnabled = plugin.getConfig().getBoolean("RaccFurnaces-enabled");
        this.blastFurnaceEnabled = plugin.getConfig().getBoolean("RaccBlastFurnaces-enabled");
        this.smokerEnabled = plugin.getConfig().getBoolean("RaccSmokers-enabled");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(!sender.hasPermission("raccfurnaces.admin")){
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        if(args.length==0){
            sender.sendMessage("§eUsage: /raccfurnaces toggle <furnaces | blastfurnaces | smokers | all>");
            return true;
        }
        if(args.length == 1){
            switch(args[0].toLowerCase()){
                case "toggle":
                    // switch all
                    toggleAll(sender);
                    return true;
                default:
                    sender.sendMessage("§eUsage: /raccfurnaces toggle <furnaces | blastfurnaces | smokers | all>");
                    return true;
            }
        }
        if(args.length == 2){
            switch(args[1].toLowerCase()){
                case "furnaces":
                    furnaceEnabled = !furnaceEnabled;
                    plugin.getConfig().set("RaccFurnaces-enabled", furnaceEnabled);
                    plugin.saveConfig();
                    sender.sendMessage("§aRaccFurnaces are now "+(furnaceEnabled ? "§aenabled" : "§cdisabled")+"§a");
                    return true;
                case "blastfurnaces":
                    blastFurnaceEnabled = !blastFurnaceEnabled;
                    plugin.getConfig().set("RaccBlastFurnaces-enabled", blastFurnaceEnabled);
                    plugin.saveConfig();
                    sender.sendMessage("§aRaccBlastFurnaces are now "+(blastFurnaceEnabled ? "§aenabled" : "§cdisabled")+"§a");
                    return true;
                case "smokers":
                    smokerEnabled = !smokerEnabled;
                    plugin.getConfig().set("RaccSmokers-enabled", smokerEnabled);
                    plugin.saveConfig();
                    sender.sendMessage("§aRaccSmokers are now "+(smokerEnabled ? "§aenabled" : "§cdisabled")+"§a");
                    return true;
                case "all":
                    // switch all
                    toggleAll(sender);
                    return true;
                default:
                    sender.sendMessage("§eUsage: /raccfurnaces toggle <furnaces | blastfurnaces | smokers | all>");
                    return true;
            }
        }
        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
        if(args.length == 1){
            return List.of("toggle");
        }
        if(args.length == 2){
            return List.of("furnaces", "blastfurnaces", "smokers", "all");
        }
        return List.of();
    }


    @EventHandler
    public void onSmelt(FurnaceSmeltEvent event){
        Material blockType = event.getBlock().getType();

        if(blockType == Material.FURNACE && !furnaceEnabled) return;
        if(blockType == Material.BLAST_FURNACE && !blastFurnaceEnabled) return;
        if(blockType == Material.SMOKER && !smokerEnabled) return;

        Furnace furnace = (Furnace)event.getBlock().getState();
        FurnaceInventory furnInv = furnace.getInventory();
        ItemStack source = furnInv.getSmelting();

        int sourceAmt = (source != null ? source.getAmount():0);
        if(sourceAmt <= 1) return; //normal behavior

        ItemStack existing = furnInv.getResult();
        int existingAmount = 0;

        //item check, prevent losing items if already in output
        if(existing != null && existing.isSimilar(event.getResult())){
            existingAmount = existing.getAmount();
        }

        //calc max items that cna fit in output before hitting 64 cap
        int maxStack = event.getResult().getMaxStackSize();
        int freeSpace = maxStack - existingAmount;

        //dont smelt if full
        if(freeSpace <= 0) return;

        //output ratio
        int outputPerSmelt = event.getResult().getAmount();

        //smelt only what output has room for
        int itemsToSmelt = Math.min(sourceAmt, freeSpace/outputPerSmelt);

        //prevent overfill, bail if too high
        if(itemsToSmelt <= 0) return;

        //set result to calc value
        ItemStack result = event.getResult().clone();
        result.setAmount(itemsToSmelt*outputPerSmelt);
        event.setResult(result);

        //calc input items that weren't smelted from constraints
        int leftover = sourceAmt - itemsToSmelt;
        if(leftover > 0){
            //put unconsumed items back into output
            ItemStack remaining = source.clone();
            remaining.setAmount(leftover);
            furnInv.setSmelting(remaining);
        }
        else{
            //clear input slot, all items smelted
            furnInv.setSmelting(null);
        }
    }


    @EventHandler
    public void onExtract(FurnaceExtractEvent event){
        Material blockType = event.getBlock().getType();

        if(blockType == Material.FURNACE && !furnaceEnabled) return;
        if(blockType == Material.BLAST_FURNACE && !blastFurnaceEnabled) return;
        if(blockType == Material.SMOKER && !smokerEnabled) return;

        int items = event.getItemAmount();
        int baseExp = event.getExpToDrop();
        event.setExpToDrop(baseExp * items);
    }


    public void toggleAll(CommandSender sender){
        furnaceEnabled = !furnaceEnabled;
        blastFurnaceEnabled = !blastFurnaceEnabled;
        smokerEnabled = !smokerEnabled;
        //save and set config
        plugin.getConfig().set("RaccFurnaces-enabled", furnaceEnabled);
        plugin.getConfig().set("RaccBlastFurnaces-enabled", blastFurnaceEnabled);
        plugin.getConfig().set("RaccSmokers-enabled", smokerEnabled);
        plugin.saveConfig();
        sender.sendMessage("§aRaccFurnaces are now "+(furnaceEnabled ? "§aenabled" : "§cdisabled")+"§a");
        sender.sendMessage("§aRaccBlastFurnaces are now "+(blastFurnaceEnabled ? "§aenabled" : "§cdisabled")+"§a");
        sender.sendMessage("§aRaccSmokers are now "+(smokerEnabled ? "§aenabled" : "§cdisabled")+"§a");
    }
}
