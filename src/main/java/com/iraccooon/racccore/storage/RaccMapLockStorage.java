package com.iraccooon.racccore.storage;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class RaccMapLockStorage {

    private Connection connection;
    private final JavaPlugin plugin;

    public RaccMapLockStorage(JavaPlugin plugin){
        this.plugin = plugin;
        try{
            plugin.getDataFolder().mkdirs();
            File db = new File(plugin.getDataFolder(), "maplocks.db");
            connection = DriverManager.getConnection("jdbc:sqlite:" + db.getAbsolutePath());
            connection.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS maplocks (map_id INTEGER PRIMARY KEY, owner_uuid TEXT)"
            );
        }
        catch (SQLException e){
            plugin.getLogger().severe("RaccMapLock: DB error - " + e.getMessage());
        }
    }

    public void setOwner(int mapId, UUID owner){
        try(PreparedStatement ps = connection.prepareStatement(
                "INSERT OR REPLACE INTO maplocks (map_id, owner_uuid) VALUES (?, ?)")) {
            ps.setInt(1, mapId);
            ps.setString(2, owner.toString());
            ps.executeUpdate();
        }
        catch (SQLException e){
            plugin.getLogger().warning("RaccMapLock: Failed to save owner - " + e.getMessage());
        }
    }

    public UUID getOwner(int mapId) {
        try(PreparedStatement ps = connection.prepareStatement(
                "SELECT owner_uuid FROM maplocks WHERE map_id = ?")){
            ps.setInt(1, mapId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return UUID.fromString(rs.getString("owner_uuid"));
        }
        catch (SQLException e){
            plugin.getLogger().warning("RaccMapLock: Failed to get owner - " + e.getMessage());
        }
        return null;
    }

    public void remove(int mapId) {
        try(PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM maplocks WHERE map_id = ?")){
            ps.setInt(1, mapId);
            ps.executeUpdate();
        }
        catch (SQLException e){
            plugin.getLogger().warning("RaccMapLock: Failed to remove map - " + e.getMessage());
        }
    }
}