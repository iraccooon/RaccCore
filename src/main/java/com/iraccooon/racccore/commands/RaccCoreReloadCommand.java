package com.iraccooon.racccore.commands;

import com.iraccooon.racccore.RaccCast;
import com.iraccooon.racccore.RaccStacks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.command.TabCompleter;
import java.util.List;

public class RaccCoreReloadCommand implements CommandExecutor, TabCompleter{
    private final JavaPlugin plugin;
    private final RaccStacks raccStacks;
    private final RaccCast raccCast;

    public RaccCoreReloadCommand(JavaPlugin plugin, RaccStacks raccStacks, RaccCast raccCast){
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

        if(args.length == 0 || !args[0].equalsIgnoreCase("reload")){
            sender.sendMessage("§eUsage: /racc reload");
            return true;
        }

        plugin.reloadConfig();
        raccStacks.reload();
        raccCast.restart();

        sender.sendMessage("§aRaccCore config reloaded!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
        if (args.length == 1) return List.of("reload");
        return List.of();
    }
}