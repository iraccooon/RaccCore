package com.iraccooon.racccore;

import org.bukkit.Bukkit;
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
    private boolean bfEnabled;
    private final JavaPlugin plugin;

    // update constructor to store plugin
    public RaccFurnaces(JavaPlugin plugin){
        this.plugin = plugin;
        this.bfEnabled = plugin.getConfig().getBoolean("RaccFurnaces-enabled");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getCommand("raccfurnaces").setExecutor(this);
    }

    public void reload(){
        this.bfEnabled = plugin.getConfig().getBoolean("RaccFurnaces-enabled");
    }


    //tab complete
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(!sender.hasPermission("raccfurnaces.admin")){
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        if(args.length==0){
            sender.sendMessage("§eUsage: /bf toggle");
            return true;
        }
        switch(args[0].toLowerCase()){
            case "toggle":
                bfEnabled = !bfEnabled; //toggle switch
                sender.sendMessage("§aRaccFurnaces is now "+(bfEnabled ? "§aenabled" : "§cdisabled")+"§a");
                return true;
            default:
                sender.sendMessage("§eUsage: /bf toggle");
                return true;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
        //tab autocomplete
        if(args.length == 1){
            return List.of("toggle");
        }

        return List.of();
    }

    @EventHandler
    public void onSmelt(FurnaceSmeltEvent event){
        if(!bfEnabled) return;

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
        if(!bfEnabled) return;

        int items = event.getItemAmount();
        event.setExpToDrop(event.getExpToDrop() * items);
    }
}
