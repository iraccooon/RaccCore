package com.iraccooon.racccore.commands;

import com.iraccooon.racccore.RaccStacks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RaccStacksCommands implements CommandExecutor, TabCompleter {
    private final RaccStacks raccStacks;

    public RaccStacksCommands(RaccStacks raccStacks){
        this.raccStacks = raccStacks;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(!sender.hasPermission("raccstacks.admin")){
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        if(args.length == 0 || !args[0].equalsIgnoreCase("toggle")){
            sender.sendMessage("§eUsage: /raccstacks toggle <furnaces | blastfurnaces | smokers | droppers | dispensers | all>");
            return true;
        }
        if(args.length == 1){
            raccStacks.toggleAll(sender);
            return true;
        }
        if(args.length == 2){
            switch(args[1].toLowerCase()){
                case "furnaces": raccStacks.toggleFurnace(sender); return true;
                case "blastfurnaces": raccStacks.toggleBlastFurnace(sender); return true;
                case "smokers": raccStacks.toggleSmoker(sender); return true;
                case "droppers": raccStacks.toggleDropper(sender); return true;
                case "dispensers": raccStacks.toggleDispenser(sender); return true;
                case "all": raccStacks.toggleAll(sender); return true;
                default:
                    sender.sendMessage("§eUsage: /raccstacks toggle <furnaces | blastfurnaces | smokers | droppers | dispensers | all>");
                    return true;
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
        if(args.length == 1) return List.of("toggle");
        if(args.length == 2) return List.of("furnaces", "blastfurnaces", "smokers", "droppers", "dispensers", "all");
        return List.of();
    }
}