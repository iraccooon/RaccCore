package com.iraccooon.racccore.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class RaccConcreteShovelCommands implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;

    public RaccConcreteShovelCommands(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(!sender.hasPermission("giveconcshovel.use")){
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if(args.length == 0){
            sender.sendMessage("§eUsage: /giveconcshovel <player>");
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[0]);
        if(target == null){
            sender.sendMessage("§cPlayer not found: " + args[0]);
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
        target.getInventory().addItem(shovel);
        target.sendMessage("§bYou received the Concrete Shovel!");
        sender.sendMessage("§aGave Concrete Shovel to §b" + target.getName() + "§a!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
        if(args.length == 1){
            return plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .toList();
        }
        return List.of();
    }
}
