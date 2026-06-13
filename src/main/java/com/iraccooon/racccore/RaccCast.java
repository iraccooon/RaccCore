package com.iraccooon.racccore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RaccCast {
    private final JavaPlugin plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private int currentIndex = 0;
    private BukkitTask intervalTask;
    private BukkitTask scheduledTask;

    // Tracks the last minute a scheduled message fired to prevent-double firing within the same minute
    // Key: "HH:MM" of last fire time per message index
    private String lastFiredMinute = "";

    public RaccCast(JavaPlugin plugin){
        this.plugin = plugin;
        start();
    }

    //restart to account for config time changes
    public void restart(){
        if(intervalTask != null) intervalTask.cancel();
        if(scheduledTask != null) scheduledTask.cancel();
        currentIndex = 0;
        start();
        plugin.getLogger().info("RaccBroadcast: Restarted with updated config");
    }

    public void start(){
        startIntervalBroadcasts();
        startScheduledBroadcasts();
    }



    //== INTERVAL BROADCASTS ============================

    private void startIntervalBroadcasts(){
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

        intervalTask = new BukkitRunnable(){
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



    //== SCHEDULED BROADCASTS =====================

    private void startScheduledBroadcasts(){
        List<?> scheduled = plugin.getConfig().getList("scheduled");
        if(scheduled == null || scheduled.isEmpty()){
            plugin.getLogger().info("RaccCast: No scheduled messages configured, skipping");
            return;
        }

        // Poll every 20 ticks (1 second)
        // Only fire once per minute by comparing the current "HH:MM" string against lastFiredMinute
        scheduledTask = new BukkitRunnable(){
            @Override
            public void run(){
                LocalDateTime now = LocalDateTime.now();
                String currentMinute = String.format("%02d:%02d", now.getHour(), now.getMinute());

                // Only process once per minute
                if(currentMinute.equals(lastFiredMinute)) return;

                String dayAbbr = now.getDayOfWeek()
                        .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                        .toUpperCase(Locale.ENGLISH); // e.g. "MON", "TUE"

                List<?> entries = plugin.getConfig().getList("scheduled");
                if(entries == null) return;

                boolean firedAny = false;
                for(Object obj : entries){
                    if(!(obj instanceof Map)) continue;

                    @SuppressWarnings("unchecked")
                    Map<String, Object> entry = (Map<String, Object>) obj;

                    String time = (String) entry.get("time");
                    if(time == null || !time.equals(currentMinute)) continue;

                    // Check optional days filter
                    Object daysObj = entry.get("days");
                    if(daysObj instanceof List){
                        @SuppressWarnings("unchecked")
                        List<String> days = (List<String>) daysObj;
                        boolean dayMatches = days.stream()
                                .anyMatch(d -> d.toUpperCase(Locale.ENGLISH).equals(dayAbbr));
                        if(!dayMatches) continue;
                    }

                    String message = (String) entry.get("message");
                    if(message != null){
                        broadcast(message);
                        firedAny = true;
                    }
                }

                if(firedAny){
                    lastFiredMinute = currentMinute;
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // start after 1s, repeat every 1s
    }




    //== HELPERS =======================================

    private void broadcast(String raw){
        Component message = miniMessage.deserialize(raw);
        Bukkit.getServer().broadcast(message);
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