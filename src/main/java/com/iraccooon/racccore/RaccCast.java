package com.iraccooon.racccore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class RaccCast {
    private final JavaPlugin plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private int currentIndex = 0;
    private BukkitTask task;

    public RaccCast(JavaPlugin plugin){
        this.plugin = plugin;
        start();
    }

    //restart to account for config time changes
    public void restart(){
        if(task != null){
            task.cancel();
        }
        currentIndex = 0;
        start();
        plugin.getLogger().info("RaccBroadcast: Restarted with updated config");
    }

    private void start(){
        List<String> messages = plugin.getConfig().getStringList("broadcasts.messages");
        String intervalConfig = plugin.getConfig().getString("broadcasts.interval");
        int intervalSeconds = parseInterval(intervalConfig);

        if(messages.isEmpty()){
            plugin.getLogger().info("RaccBroadcast: No messages configured, skipping.");
            return;
        }

        if(intervalSeconds <= 0){
            plugin.getLogger().warning("RaccBroadcast: Warning! Invalid or missing interval in config, broadcasts have been disabled.");
            return;
        }

        long intervalTicks = intervalSeconds * 20L;

        task = new BukkitRunnable(){
            @Override
            public void run() {
                List<String> msgs = plugin.getConfig().getStringList("broadcasts.messages");
                if(msgs.isEmpty()) return; //failsafe

                if(currentIndex >= msgs.size()) currentIndex = 0;

                Component message = miniMessage.deserialize(msgs.get(currentIndex));
                Bukkit.getServer().broadcast(message);
                currentIndex++;
            }
        }.runTaskTimer(plugin, intervalTicks, intervalTicks);
    }

    private int parseInterval(String input){
        if(input == null) return -1;
        input = input.trim().toLowerCase();
        try
        {
            if(input.endsWith("min")){
                return Integer.parseInt(input.replace("min", "").trim()) * 60;
            }
            else if (input.endsWith("sec")){
                return Integer.parseInt(input.replace("sec", "").trim());
            }
        }
        catch (NumberFormatException e){
            plugin.getLogger().warning("RaccCast: Invalid interval format: " + input);
        }
        return -1;
    }
}
