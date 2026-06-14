package com.iraccooon.racccore.listeners;

import com.iraccooon.racccore.DiscordWebhook;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;

public class ServerLifecycleListener implements Listener {
    private final DiscordWebhook webhook;

    public ServerLifecycleListener(DiscordWebhook webhook){
        this.webhook = webhook;
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event){
        webhook.log("server-status", "Server Started", "Server is online and accepting connections!", 0x57F287);
    }
}
