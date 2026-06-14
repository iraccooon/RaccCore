package com.iraccooon.racccore;

import com.iraccooon.racccore.commands.RaccConcreteShovelCommands;
import com.iraccooon.racccore.commands.RaccCoreCommands;
import com.iraccooon.racccore.commands.RaccStacksCommands;
import com.iraccooon.racccore.listeners.ServerLifecycleListener;
import org.bukkit.plugin.java.JavaPlugin;

public class RaccCore extends JavaPlugin{
    private RaccCast raccCast;
    private RaccStacks raccStacks;
    private DiscordWebhook webhook;

    @Override
    public void onEnable(){
        saveDefaultConfig();
        getLogger().info("RaccCore "+getPluginMeta().getVersion()+" has been enabled!");

        raccStacks = new RaccStacks(this);
        raccCast = new RaccCast(this);
        webhook = new DiscordWebhook(this);

        new RaccLock(this);
        new RaccConcrete(this);
        new RaccConcreteShovel(this);

        registerCommands();
        registerEvents();
    }

    @Override
    public void onDisable(){
        getLogger().info("RaccCore "+getPluginMeta().getVersion()+" has been disabled!");
    }

    //helper to clean up onEnable()
    private void registerCommands(){
        RaccCoreCommands raccCoreReload = new RaccCoreCommands(this, raccStacks, raccCast);
        getCommand("racc").setExecutor(raccCoreReload);
        getCommand("racc").setTabCompleter(raccCoreReload);
        RaccStacksCommands raccStacksCommand = new RaccStacksCommands(raccStacks);
        getCommand("raccstacks").setExecutor(raccStacksCommand);
        getCommand("raccstacks").setTabCompleter(raccStacksCommand);
        getCommand("giveconcshovel").setExecutor(new RaccConcreteShovelCommands(this, webhook));
    }

    private void registerEvents(){
        getServer().getPluginManager().registerEvents(new ServerLifecycleListener(webhook), this);
    }

    public DiscordWebhook getWebhook(){
        return webhook;
    }
}
