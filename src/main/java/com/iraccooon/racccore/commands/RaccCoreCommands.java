package com.iraccooon.racccore.commands;

import com.iraccooon.racccore.RaccCast;
import com.iraccooon.racccore.RaccStacks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.command.TabCompleter;
import java.util.List;

public class RaccCoreCommands implements CommandExecutor, TabCompleter{
    private final JavaPlugin plugin;
    private final RaccStacks raccStacks;
    private final RaccCast raccCast;

    public RaccCoreCommands(JavaPlugin plugin, RaccStacks raccStacks, RaccCast raccCast){
        this.plugin = plugin;
        this.raccStacks = raccStacks;
        this.raccCast = raccCast;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(!sender.hasPermission("racccore.admin")){
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        if(args.length == 0 || (!args[0].equalsIgnoreCase("help") && !args[0].equalsIgnoreCase("reload"))){
            sender.sendMessage("§eUsage: /racc <help | reload>");
            return true;
        }
        if(args[0].equalsIgnoreCase("help")){
            sender.sendMessage("§6--- RaccCore Help ---");
            sender.sendMessage("§e/racc reload §7- Reload the plugin config");
            sender.sendMessage("§e/racc help §7- Show this help message");
            sender.sendMessage("§e/raccstacks toggle <furnaces | blastfurnaces | smokers | droppers | dispensers | all> §7- Toggle stack-based processing");
            sender.sendMessage("§e/raccstacks toggle §7- Toggle all stack features");
            sender.sendMessage("§e/maplock <lock | unlock> §7- Lock the map you are holding");
            sender.sendMessage("§e/giveconcshovel <player> §7- Give a player the concrete shovel");
            return true;
        }
        if(args[0].equalsIgnoreCase("reload")){
            plugin.reloadConfig();
            raccStacks.reload();
            raccCast.restart();

            sender.sendMessage("§aRaccCore config reloaded!");
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
        if (args.length == 1) return List.of("help", "reload");
        return List.of();
    }
}