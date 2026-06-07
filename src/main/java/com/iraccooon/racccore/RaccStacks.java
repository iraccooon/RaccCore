package com.iraccooon.racccore;

import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.block.Furnace;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.block.Dropper;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

import java.util.Set;

public class RaccStacks implements Listener {
    private boolean dropperEnabled;
    private boolean dispenserEnabled;
    private boolean furnaceEnabled;
    private boolean blastFurnaceEnabled;
    private boolean smokerEnabled;
    private final JavaPlugin plugin;

    // RaccStacks constructor
    public RaccStacks(JavaPlugin plugin){
        this.plugin = plugin;
        this.dropperEnabled = plugin.getConfig().getBoolean("RaccDroppers-enabled");
        this.dispenserEnabled = plugin.getConfig().getBoolean("RaccDispensers-enabled");
        this.furnaceEnabled = plugin.getConfig().getBoolean("RaccFurnaces-enabled");
        this.blastFurnaceEnabled = plugin.getConfig().getBoolean("RaccBlastFurnaces-enabled");
        this.smokerEnabled = plugin.getConfig().getBoolean("RaccSmokers-enabled");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    // =================================================================================================================
    // Helpers
    // =================================================================================================================

    public void reload(){
        this.dropperEnabled = plugin.getConfig().getBoolean("RaccDroppers-enabled");
        this.dispenserEnabled = plugin.getConfig().getBoolean("RaccDispensers-enabled");
        this.furnaceEnabled = plugin.getConfig().getBoolean("RaccFurnaces-enabled");
        this.blastFurnaceEnabled = plugin.getConfig().getBoolean("RaccBlastFurnaces-enabled");
        this.smokerEnabled = plugin.getConfig().getBoolean("RaccSmokers-enabled");
    }

    public void toggleAll(CommandSender sender){
        dropperEnabled = !dropperEnabled;
        dispenserEnabled = !dispenserEnabled;
        furnaceEnabled = !furnaceEnabled;
        blastFurnaceEnabled = !blastFurnaceEnabled;
        smokerEnabled = !smokerEnabled;
        // set and save config
        plugin.getConfig().set("RaccDroppers-enabled", dropperEnabled);
        plugin.getConfig().set("RaccDispensers-enabled", dispenserEnabled);
        plugin.getConfig().set("RaccFurnaces-enabled", furnaceEnabled);
        plugin.getConfig().set("RaccBlastFurnaces-enabled", blastFurnaceEnabled);
        plugin.getConfig().set("RaccSmokers-enabled", smokerEnabled);
        plugin.saveConfig();
        // Send feedback message
        sender.sendMessage("§aRaccDroppers are now "+(dropperEnabled ? "§aenabled" : "§cdisabled")+"§a");
        sender.sendMessage("§aRaccDispensers are now "+(dispenserEnabled ? "§aenabled" : "§cdisabled")+"§a");
        sender.sendMessage("§aRaccFurnaces are now "+(furnaceEnabled ? "§aenabled" : "§cdisabled")+"§a");
        sender.sendMessage("§aRaccBlastFurnaces are now "+(blastFurnaceEnabled ? "§aenabled" : "§cdisabled")+"§a");
        sender.sendMessage("§aRaccSmokers are now "+(smokerEnabled ? "§aenabled" : "§cdisabled")+"§a");
    }

    public void toggleFurnace(CommandSender sender){
        furnaceEnabled = !furnaceEnabled;
        plugin.getConfig().set("RaccFurnaces-enabled", furnaceEnabled);
        plugin.saveConfig();
        sender.sendMessage("§aRaccFurnaces are now "+(furnaceEnabled ? "§aenabled" : "§cdisabled")+"§a");
    }

    public void toggleBlastFurnace(CommandSender sender){
        blastFurnaceEnabled = !blastFurnaceEnabled;
        plugin.getConfig().set("RaccBlastFurnaces-enabled", blastFurnaceEnabled);
        plugin.saveConfig();
        sender.sendMessage("§aRaccBlastFurnaces are now "+(blastFurnaceEnabled ? "§aenabled" : "§cdisabled")+"§a");
    }

    public void toggleSmoker(CommandSender sender){
        smokerEnabled = !smokerEnabled;
        plugin.getConfig().set("RaccSmokers-enabled", smokerEnabled);
        plugin.saveConfig();
        sender.sendMessage("§aRaccSmokers are now "+(smokerEnabled ? "§aenabled" : "§cdisabled")+"§a");
    }

    public void toggleDropper(CommandSender sender){
        dropperEnabled = !dropperEnabled;
        plugin.getConfig().set("RaccDroppers-enabled", dropperEnabled);
        plugin.saveConfig();
        sender.sendMessage("§aRaccDroppers are now "+(dropperEnabled ? "§aenabled" : "§cdisabled")+"§a");
    }

    public void toggleDispenser(CommandSender sender){
        dispenserEnabled = !dispenserEnabled;
        plugin.getConfig().set("RaccDispensers-enabled", dispenserEnabled);
        plugin.saveConfig();
        sender.sendMessage("§aRaccDispensers are now "+(dispenserEnabled ? "§aenabled" : "§cdisabled")+"§a");
    }


    // =================================================================================================================
    // Dropper & Dispenser logic
    // =================================================================================================================

    // Usable items for dispenser that should be ignored by onDispense.
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
        if(blockType == Material.DISPENSER && (
                DISPENSER_USE_ITEMS.contains(itemType) ||
                        itemType.name().endsWith("_SPAWN_EGG") ||
                        itemType.name().endsWith("_HELMET") ||
                        itemType.name().endsWith("_CHESTPLATE") ||
                        itemType.name().endsWith("_LEGGINGS") ||
                        itemType.name().endsWith("_BOOTS")
        )) return;

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

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event){
        if(!dropperEnabled) return;

        if(!(event.getSource().getHolder() instanceof Dropper dropper)) return;

        ItemStack item = event.getItem();
        int maxStack = item.getType().getMaxStackSize();
        if(maxStack <= 1) return;

        Inventory source = event.getSource();
        Inventory destination = event.getDestination();

        // find the slot in source containing this item
        for(int i = 0; i < source.getSize(); i++){
            ItemStack slot = source.getItem(i);
            if(slot != null && slot.isSimilar(item)){
                int toMove = Math.min(slot.getAmount(), maxStack);

                // calculate how much space destination has for this item
                int freeSpace = 0;
                for(ItemStack destSlot : destination.getContents()){
                    if(destSlot == null){
                        freeSpace += maxStack;
                    } else if(destSlot.isSimilar(item)){
                        freeSpace += maxStack - destSlot.getAmount();
                    }
                }

                int canFit = Math.min(toMove, freeSpace);
                int overflow = toMove - canFit;

                // move what fits
                if(canFit > 0){
                    event.setItem(new ItemStack(item.getType(), canFit));
                    slot.setAmount(slot.getAmount() - canFit);
                    if(slot.getAmount() <= 0) source.setItem(i, null);
                } else {
                    event.setCancelled(true);
                }

                // drop overflow to ground
                if(overflow > 0){
                    ItemStack drop = item.clone();
                    drop.setAmount(overflow);
                    dropper.getBlock().getWorld().dropItemNaturally(
                            dropper.getBlock().getLocation().add(0.5, 0.5, 0.5), drop
                    );
                    slot.setAmount(slot.getAmount() - overflow);
                    if(slot.getAmount() <= 0) source.setItem(i, null);
                }
                break;
            }
        }
    }


    // =================================================================================================================
    // Furnace logic
    // =================================================================================================================

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
}
