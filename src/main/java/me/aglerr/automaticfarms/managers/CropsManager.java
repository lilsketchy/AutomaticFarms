package me.aglerr.automaticfarms.managers;

import com.cryptomorin.xseries.XMaterial;
import me.aglerr.automaticfarms.AutomaticFarms;
import me.aglerr.automaticfarms.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;
import org.bukkit.material.NetherWarts;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class CropsManager {

    private final Map<Material, Integer> crops = new HashMap<>();

    private final AutomaticFarms plugin;
    public CropsManager(AutomaticFarms plugin){
        this.plugin = plugin;
    }

    public boolean isMaterialExist(Material material){
        return crops.containsKey(material);
    }

    public int getGrowingWaitTime(Material material){
        return crops.get(material);
    }

    public void initializeCrops(){
        FileConfiguration config = plugin.getConfig();

        if(config.getBoolean("crops.carrots.enabled")){
            int waitTime = config.getInt("crops.carrots.waitTime");
            crops.put(XMaterial.CARROTS.parseMaterial(), waitTime);
        }

        if(config.getBoolean("crops.potato.enabled")){
            int waitTime = config.getInt("crops.potato.waitTime");
            crops.put(XMaterial.POTATOES.parseMaterial(), waitTime);
        }

        if(config.getBoolean("crops.wheat.enabled")){
            int waitTime = config.getInt("crops.wheat.waitTime");
            crops.put(XMaterial.WHEAT.parseMaterial(), waitTime);
        }

        if(config.getBoolean("crops.netherWart.enabled")){
            int waitTime = config.getInt("crops.netherWart.waitTime");
            crops.put(XMaterial.NETHER_WART.parseMaterial(), waitTime);
        }

        if(config.getBoolean("crops.cactus.enabled")){
            int waitTime = config.getInt("crops.cactus.waitTime");
            crops.put(XMaterial.CACTUS.parseMaterial(), waitTime);
        }

    }

}
