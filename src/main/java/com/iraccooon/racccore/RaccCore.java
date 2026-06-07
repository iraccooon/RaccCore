package com.iraccooon.racccore;

import com.iraccooon.racccore.commands.GiveRaccConcreteShovelCommand;
import com.iraccooon.racccore.commands.RaccCoreReloadCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class RaccCore extends JavaPlugin{
    private RaccCast raccCast;
    private RaccFurnaces raccFurnaces;

    @Override
    public void onEnable(){
        saveDefaultConfig();
        getLogger().info("RaccCore "+getPluginMeta().getVersion()+" has been enabled!");
        raccFurnaces = new RaccFurnaces(this);
        new RaccLock(this);
        getServer().getPluginManager().registerEvents(new RaccConcrete(this), this);
        getServer().getPluginManager().registerEvents(new RaccConcreteShovel(this), this);
        raccCast = new RaccCast(this);
        registerCommands();
    }

    @Override
    public void onDisable(){
        getLogger().info("RaccCore "+getPluginMeta().getVersion()+" has been disabled!");
    }

    //helper to clean up onEnable()
    private void registerCommands(){
        RaccCoreReloadCommand raccCoreReload = new RaccCoreReloadCommand(this, raccFurnaces, raccCast);
        getCommand("giveconcshovel").setExecutor(new GiveRaccConcreteShovelCommand(this));
        getCommand("racc").setExecutor(raccCoreReload);
        getCommand("racc").setTabCompleter(raccCoreReload);
    }
}
