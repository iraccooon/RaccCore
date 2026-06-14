package com.iraccooon.racccore;

import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

public class DiscordWebhook {
    private final JavaPlugin plugin;
    private final HttpClient client = HttpClient.newHttpClient();

    public DiscordWebhook(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public void log(String eventKey, String title, String description, int color){
        if(!plugin.getConfig().getBoolean("discord-webhook.enabled")) return;
        if(!plugin.getConfig().getBoolean("discord-webhook.events." + eventKey)) return;

        String url = plugin.getConfig().getString("discord-webhook.url");
        if(url == null || url.isBlank()) return;

        String payload = """
        {
          "embeds": [{
            "title": "%s",
            "description": "%s",
            "color": %d,
            "timestamp": "%s"
          }]
        }
        """.formatted(escape(title), escape(description), color, Instant.now().toString());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .exceptionally(e -> {
                    plugin.getLogger().warning("DiscordWebhook: failed to send - "+e.getMessage());
                    return null;
                });
    }



    private String escape(String s){
        return s.replace("\"", "\\\"").replace("\n", "\\n");
    }

}
