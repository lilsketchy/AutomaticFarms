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

    }

    public void handleCropGrowing(Ageable ageable, Block block, Location location){

        ageable.setAge(0);
        block.setBlockData(ageable);

        plugin.getGrowingManager().addGrowingBlock(block);
        int waitTime = this.getGrowingWaitTime(block.getType());

        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> new BukkitRunnable(){
            @Override
            public void run(){

                if(!(block.getBlockData() instanceof Ageable)){
                    this.cancel();
                    return;
                }

                Ageable finalAgeable = (Ageable) block.getBlockData();
                int age = finalAgeable.getAge();

                if(age >= finalAgeable.getMaximumAge()){
                    plugin.getGrowingManager().removeGrowingBlock(block);
                    this.cancel();
                    return;
                }

                finalAgeable.setAge(age + 1);
                block.setBlockData(finalAgeable);

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                        Utils.summonParticle(location));

            }
        }.runTaskTimer(plugin, 0L, 20L), waitTime);


    }

    public void handleLegacyNetherWartGrowing(BlockBreakEvent event, BlockState blockState, Location location){
        FileConfiguration config = plugin.getConfig();

        int waitTime = this.getGrowingWaitTime(XMaterial.NETHER_WART.parseMaterial());

        if(blockState.getBlock().getType().toString().equalsIgnoreCase("NETHER_WARTS")){
            if(!config.getBoolean("crops.netherWart.enabled")) return;

            event.setCancelled(true);

            NetherWarts warts = (NetherWarts) blockState.getData();
            warts.setState(NetherWartsState.SEEDED);

            blockState.setData(warts);
            blockState.update();

            plugin.getGrowingManager().addGrowingBlock(blockState.getBlock());
            this.dropItemRandomly(Material.NETHER_WART, blockState.getLocation());

            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> new BukkitRunnable(){
                @Override
                public void run(){

                    if(!(blockState.getData() instanceof NetherWarts)){
                        this.cancel();
                        return;
                    }

                    NetherWarts warts = (NetherWarts) blockState.getData();

                    if(warts.getData() >= AutomaticFarms.MAX_NETHER_WART){
                        plugin.getGrowingManager().removeGrowingBlock(blockState.getBlock());
                        this.cancel();
                        return;
                    }

                    warts.setData((byte) (warts.getData() + 1));
                    blockState.update();

                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                            Utils.summonParticle(location));

                }
            }.runTaskTimer(plugin, 0L, 20L), waitTime);

        }

    }

    public void handleLegacyWheatGrowing(BlockBreakEvent event, BlockState blockState, Location location){
        FileConfiguration config = plugin.getConfig();

        int waitTime = this.getGrowingWaitTime(XMaterial.WHEAT.parseMaterial());

        if(blockState.getBlock().getType().toString().equalsIgnoreCase("CROPS")){
            if(!config.getBoolean("crops.wheat.enabled")) return;

            event.setCancelled(true);

            Crops crop = (Crops) blockState.getData();
            crop.setState(CropState.getByData((byte) 0));

            blockState.setData(crop);
            blockState.update();

            plugin.getGrowingManager().addGrowingBlock(blockState.getBlock());
            this.dropItemRandomly(XMaterial.WHEAT_SEEDS.parseMaterial(), blockState.getLocation());
            this.dropItemRandomly(XMaterial.WHEAT.parseMaterial(), blockState.getLocation());

            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> new BukkitRunnable(){
                @Override
                public void run() {

                    if(!(blockState.getData() instanceof Crops)){
                        this.cancel();
                        return;
                    }

                    Crops crop = (Crops) blockState.getData();

                    if(crop.getData() >= AutomaticFarms.MAX_CROP){
                        plugin.getGrowingManager().removeGrowingBlock(blockState.getBlock());
                        this.cancel();
                        return;
                    }

                    crop.setData((byte) (crop.getData() + 1));
                    blockState.update();

                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                            Utils.summonParticle(location));

                }
            }.runTaskTimer(plugin, 0L, 20L), waitTime);

        }

    }

    public void handleLegacyCropGrowing(BlockState blockState, Location location){
        if(blockState.getData() instanceof Crops){

            int waitTime = this.getGrowingWaitTime(blockState.getType());

            Crops crop = (Crops) blockState.getData();
            crop.setState(CropState.getByData((byte) 0));

            blockState.setData(crop);
            blockState.update();

            plugin.getGrowingManager().addGrowingBlock(blockState.getBlock());
            this.dropItemRandomly(blockState.getType(), blockState.getLocation());

            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> new BukkitRunnable(){
                @Override
                public void run() {

                    if(!(blockState.getData() instanceof Crops)){
                        this.cancel();
                        return;
                    }

                    Crops crop = (Crops) blockState.getData();

                    if(crop.getData() >= AutomaticFarms.MAX_CROP){
                        plugin.getGrowingManager().removeGrowingBlock(blockState.getBlock());
                        this.cancel();
                        return;
                    }

                    crop.setData((byte) (crop.getData() + 1));
                    blockState.update();

                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                            Utils.summonParticle(location));

                }
            }.runTaskTimer(plugin, 0L, 20L), waitTime);

        }

    }

    private void dropItemRandomly(Material material, Location location){
        ItemStack stack = new ItemStack(material);
        stack.setAmount(ThreadLocalRandom.current().nextInt(4));

        location.getWorld().dropItemNaturally(location, stack);
    }

}
