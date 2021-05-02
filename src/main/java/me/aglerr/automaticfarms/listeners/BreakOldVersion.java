package me.aglerr.automaticfarms.listeners;

import me.aglerr.automaticfarms.AutomaticFarms;
import me.aglerr.automaticfarms.managers.CropsManager;
import me.aglerr.automaticfarms.managers.GrowingManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakOldVersion implements Listener {

    private final AutomaticFarms plugin;
    public BreakOldVersion(final AutomaticFarms plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event){

        FileConfiguration config = plugin.getConfig();
        Block block = event.getBlock();

        CropsManager cropsManager = plugin.getCropsManager();
        GrowingManager growingManager = plugin.getGrowingManager();

        if(growingManager.isBlockGrowing(block)){
            event.setCancelled(true);
            return;
        }

        BlockState blockState = block.getState();
        Location location = block.getLocation().clone().add(0.5, 1, 0.5);

        if(config.getBoolean("checks.onlyFullyGrown")){
            if(!growingManager.isFullyGrown(blockState)) return;
        }

        cropsManager.handleLegacyNetherWartGrowing(event, blockState, location);
        cropsManager.handleLegacyWheatGrowing(event, blockState, location);

        if(!cropsManager.isMaterialExist(block.getType())) return;

        event.setCancelled(true);
        cropsManager.handleLegacyCropGrowing(blockState, location);


    }

}
