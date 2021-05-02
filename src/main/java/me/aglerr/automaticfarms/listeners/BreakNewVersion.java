package me.aglerr.automaticfarms.listeners;

import me.aglerr.automaticfarms.AutomaticFarms;
import me.aglerr.automaticfarms.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BreakNewVersion implements Listener {

    private final AutomaticFarms plugin;
    public BreakNewVersion(final AutomaticFarms plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event){

        FileConfiguration config = plugin.getConfig();

        Player player = event.getPlayer();
        Block block = event.getBlock();

        if(plugin.isBlockGrowing(block)) {
            event.setCancelled(true);
            return;
        }

        if(!plugin.getCrops().contains(block.getType())) return;
        if(!(block.getBlockData() instanceof Ageable)) return;

        if(config.getBoolean("checks.onlyFullyGrown")){
            if(!Utils.isFullyGrown(block)) return;
        }

        event.setCancelled(true);

        for(ItemStack stack : event.getBlock().getDrops(player.getItemInHand())){
            block.getWorld().dropItemNaturally(block.getLocation(), stack);
        }

        Ageable ageable = (Ageable) block.getBlockData();
        int targetAge = ageable.getAge();

        Location location = block.getLocation().clone().add(0.5, 1, 0.5);

        ageable.setAge(0);
        block.setBlockData(ageable);

        plugin.addGrowingBlock(block);
        Utils.regrowsNewVersion(plugin, block, targetAge, location);

    }

}
