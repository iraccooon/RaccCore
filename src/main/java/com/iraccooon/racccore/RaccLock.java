package com.iraccooon.racccore;

import com.iraccooon.racccore.commands.RaccLockCommand;
import com.iraccooon.racccore.listeners.RaccLockListener;
import com.iraccooon.racccore.listeners.RaccMapOwnerListener;

import com.iraccooon.racccore.storage.RaccMapLockStorage;
import org.bukkit.plugin.java.JavaPlugin;

public class RaccLock {

    public RaccLock(JavaPlugin plugin){
        RaccMapLockStorage storage = new RaccMapLockStorage(plugin);
        plugin.getCommand("maplock").setExecutor(new RaccLockCommand(storage, plugin));
        plugin.getServer().getPluginManager().registerEvents(new RaccLockListener(storage), plugin);
        plugin.getServer().getPluginManager().registerEvents(new RaccMapOwnerListener(), plugin);
    }
}
