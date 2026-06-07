package com.iraccooon.racccore.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class GiveRaccConcreteShovelCommand implements CommandExecutor{
    private final JavaPlugin plugin;

    public GiveRaccConcreteShovelCommand(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        //checks if sender is not a player
        if(!(sender instanceof final Player player)){
            //here if sender is not a player
            sender.sendMessage("§cOnly players can execute this command!");
            return true;
        }

        if(!sender.hasPermission("giveconcshovel.use")){
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        ItemStack shovel = new ItemStack(Material.DIAMOND_SHOVEL);
        ItemMeta meta = shovel.getItemMeta();

        meta.setDisplayName("§bConcrete Shovel"); //custom name
        meta.addEnchant(Enchantment.EFFICIENCY, 5, true);
        String durabilityConfig = plugin.getConfig().getString("concrete-shovel-durability");
        if (durabilityConfig.equalsIgnoreCase("false")) {
            meta.setUnbreakable(true);
        }

        shovel.setItemMeta(meta);
        player.getInventory().addItem(shovel);
        player.sendMessage("§bYou received the Concrete Shovel!");
        return true;
    }
}
