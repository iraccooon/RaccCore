package com.iraccooon.racccore;

import com.iraccooon.racccore.commands.GiveRaccConcreteShovelCommand;
import com.iraccooon.racccore.commands.RaccCoreReloadCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class RaccCore extends JavaPlugin{
    private RaccCast raccCast;
    private RaccFurnaces raccFurnaces;
    private RaccStacks raccStacks;

    @Override
    public void onEnable(){
        saveDefaultConfig();
        getLogger().info("RaccCore "+getPluginMeta().getVersion()+" has been enabled!");

        raccFurnaces = new RaccFurnaces(this);
        raccStacks = new RaccStacks(this);
        raccCast = new RaccCast(this);
        new RaccLock(this);
        new RaccConcrete(this);
        new RaccConcreteShovel(this);

        registerCommands();
    }

    @Override
    public void onDisable(){
        getLogger().info("RaccCore "+getPluginMeta().getVersion()+" has been disabled!");
    }

    //helper to clean up onEnable()
    private void registerCommands(){
        RaccCoreReloadCommand raccCoreReload = new RaccCoreReloadCommand(this, raccFurnaces, raccStacks, raccCast);
        getCommand("racc").setExecutor(raccCoreReload);
        getCommand("racc").setTabCompleter(raccCoreReload);
        getCommand("giveconcshovel").setExecutor(new GiveRaccConcreteShovelCommand(this));
        getCommand("raccfurnaces").setExecutor(raccFurnaces);
        getCommand("raccfurnaces").setTabCompleter(raccFurnaces);
        getCommand("raccstacks").setExecutor(raccStacks);
        getCommand("raccstacks").setTabCompleter(raccStacks);
    }
}
