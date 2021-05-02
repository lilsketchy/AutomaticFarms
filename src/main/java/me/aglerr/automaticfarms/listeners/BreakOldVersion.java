package me.aglerr.automaticfarms.listeners;

import com.cryptomorin.xseries.XMaterial;
import me.aglerr.automaticfarms.AutomaticFarms;
import me.aglerr.automaticfarms.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;
import org.bukkit.material.NetherWarts;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

public class BreakOldVersion implements Listener {

    private final AutomaticFarms plugin;
    public BreakOldVersion(final AutomaticFarms plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event){

        FileConfiguration config = plugin.getConfig();
        Block block = event.getBlock();

        if(plugin.isBlockGrowing(block)){
            event.setCancelled(true);
            return;
        }

        BlockState blockState = block.getState();
        Location location = block.getLocation().clone().add(0.5, 1, 0.5);

        if(config.getBoolean("checks.onlyFullyGrown")){
            if(!Utils.isFullyGrown(blockState)) return;
        }

        if(block.getType().toString().equalsIgnoreCase("NETHER_WARTS")){
            if(!config.getBoolean("crops.netherWart")) return;

            event.setCancelled(true);

            NetherWarts warts = (NetherWarts) blockState.getData();
            warts.setState(NetherWartsState.values()[0]);

            blockState.setData(warts);
            blockState.update();

            plugin.addGrowingBlock(block);
            this.dropItemRandomly(XMaterial.NETHER_WART.parseMaterial(), block.getLocation());

            Utils.regrowsOldNetherWart(plugin, blockState, location);

            return;
        }

        if(block.getType().toString().equalsIgnoreCase("CROPS")){
            if(!config.getBoolean("crops.wheat")) return;

            event.setCancelled(true);

            Crops crop = (Crops) blockState.getData();
            crop.setState(CropState.getByData((byte) 0));

            blockState.setData(crop);
            blockState.update();

            plugin.addGrowingBlock(block);
            this.dropItemRandomly(XMaterial.WHEAT_SEEDS.parseMaterial(), block.getLocation());
            this.dropItemRandomly(XMaterial.WHEAT.parseMaterial(), block.getLocation());

            Utils.regrowsOldVersion(plugin, blockState, location);

            return;
        }

        if(!plugin.getCrops().contains(block.getType())) return;
        event.setCancelled(true);

        if(blockState.getData() instanceof Crops){

            Crops crop = (Crops) blockState.getData();
            crop.setState(CropState.getByData((byte) 0));

            blockState.setData(crop);
            blockState.update();

            plugin.addGrowingBlock(block);
            this.dropItemRandomly(block.getType(), block.getLocation());

            Utils.regrowsOldVersion(plugin, blockState, location);

        }


    }

    private void dropItemRandomly(Material material, Location location){
        ItemStack stack = new ItemStack(material);
        stack.setAmount(ThreadLocalRandom.current().nextInt(4));

        location.getWorld().dropItemNaturally(location, stack);
    }

}
