package me.aglerr.automaticfarms.managers;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.aglerr.automaticfarms.AutomaticFarms;
import me.aglerr.automaticfarms.enums.DataType;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class DataManager {

    private final Table<UUID, DataType, Integer> table = HashBasedTable.create();

    private final AutomaticFarms plugin;
    public DataManager(final AutomaticFarms plugin){
        this.plugin = plugin;
    }

    public int getBrokenCrop(UUID uuid, DataType type){
        if(table.get(uuid, type) == null) return 0;
        return table.get(uuid, type);
    }

    public void assignPlayerData(UUID uuid, DataType type, int amount){
        table.put(uuid, type, amount);
    }

    public void incrementBrokenCrop(UUID uuid, DataType type){
        table.put(uuid, type, getBrokenCrop(uuid, type) + 1);
    }

    public void incrementOrAssignData(UUID uuid, DataType type){
        if(this.containsPlayerData(uuid, type)){
            this.incrementBrokenCrop(uuid, type);
            return;
        }

        this.assignPlayerData(uuid, type, 1);
    }

    public int getTotalBrokenCrop(UUID uuid){
        int total = 0;
        for(DataType type : table.columnKeySet()){
            total += this.getBrokenCrop(uuid, type);
        }

        return total;
    }

    public boolean containsPlayerData(UUID uuid, DataType type){
        return table.contains(uuid, type);
    }

    public void savePlayerDataToConfig(){

        FileConfiguration data = plugin.getDataConfiguration().getConfiguration();
        for(UUID uuid : table.rowKeySet()){
            for(DataType type : table.columnKeySet()){
                int amount = getBrokenCrop(uuid, type);
                data.set("data." + uuid.toString() + "." + type.toString(), amount);
            }
        }

        plugin.getDataConfiguration().saveData();

    }

    public void loadPlayerData(){

        FileConfiguration data = plugin.getDataConfiguration().getConfiguration();
        if(!data.isConfigurationSection("data")) return;

        for(String uuid : data.getConfigurationSection("data").getKeys(false)){
            for(String type : data.getConfigurationSection("data." + uuid).getKeys(false)){
                int amount = data.getInt("data." + uuid + "." + type);
                this.assignPlayerData(UUID.fromString(uuid), DataType.valueOf(type), amount);
            }
        }

        data.set("data", null);
        plugin.getDataConfiguration().saveData();

    }

}
