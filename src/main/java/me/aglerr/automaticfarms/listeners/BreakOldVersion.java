package me.aglerr.automaticfarms.listeners;

import com.cryptomorin.xseries.XMaterial;
import me.aglerr.automaticfarms.AutomaticFarms;
import me.aglerr.automaticfarms.managers.CropsManager;
import me.aglerr.automaticfarms.managers.GrowingManager;
import me.aglerr.automaticfarms.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;
import org.bukkit.material.NetherWarts;
import org.bukkit.scheduler.BukkitRunnable;

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

        this.handleLegacyNetherWartGrowing(event, blockState, location);
        this.handleLegacyWheatGrowing(event, blockState, location);
        this.handleLegacyCropGrowing(event, blockState, location);


    }

    @EventHandler
    public void playerBreakCactus(BlockBreakEvent event){

        Block block = event.getBlock();
        Location location = block.getLocation().clone().add(0.5, 1, 0.5);

        if(block.getType() == XMaterial.CACTUS.parseMaterial()){
            if(block.getRelative(BlockFace.DOWN).getType() == XMaterial.CACTUS.parseMaterial()) return;

            this.handleCactusGrowing(block.getLocation(), location);

        }

    }

    private void handleCactusGrowing(Location blockLocation, Location particleLocation){

        CropsManager cropsManager = plugin.getCropsManager();
        int waitTime = cropsManager.getGrowingWaitTime(XMaterial.CACTUS.parseMaterial());

        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> new BukkitRunnable(){
            @Override
            public void run(){

                blockLocation.getBlock().setType(XMaterial.CACTUS.parseMaterial());
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                        Utils.summonParticle(particleLocation));

                this.cancel();

            }
        }.runTaskTimer(plugin, 0L, 20L), waitTime * 20);

    }

    private void handleLegacyNetherWartGrowing(BlockBreakEvent event, BlockState blockState, Location location){

        FileConfiguration config = plugin.getConfig();
        CropsManager cropsManager = plugin.getCropsManager();

        int waitTime = cropsManager.getGrowingWaitTime(XMaterial.NETHER_WART.parseMaterial());

        if(blockState.getBlock().getType().toString().equalsIgnoreCase("NETHER_WARTS")){
            if(!config.getBoolean("crops.netherWart.enabled")) return;


            event.setCancelled(true);

            NetherWarts warts = (NetherWarts) blockState.getData();
            warts.setState(NetherWartsState.SEEDED);

            blockState.setData(warts);
            blockState.update();

            plugin.getGrowingManager().addGrowingBlock(blockState.getBlock());
            this.dropItemRandomly(XMaterial.NETHER_WART.parseMaterial(), blockState.getLocation());

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
            }.runTaskTimer(plugin, 0L, 20L), waitTime * 20);

        }

    }

    private void handleLegacyWheatGrowing(BlockBreakEvent event, BlockState blockState, Location location){

        FileConfiguration config = plugin.getConfig();
        CropsManager cropsManager = plugin.getCropsManager();

        int waitTime = cropsManager.getGrowingWaitTime(XMaterial.WHEAT.parseMaterial());

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
            }.runTaskTimer(plugin, 0L, 20L), waitTime * 20);

        }

    }

    private void handleLegacyCropGrowing(BlockBreakEvent event, BlockState blockState, Location location){
        if(blockState.getData() instanceof Crops){

            CropsManager cropsManager = plugin.getCropsManager();

            if(!cropsManager.isMaterialExist(blockState.getType())) return;
            event.setCancelled(true);

            int waitTime = cropsManager.getGrowingWaitTime(blockState.getType());

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
            }.runTaskTimer(plugin, 0L, 20L), waitTime * 20);

        }

    }

    private void dropItemRandomly(Material material, Location location){
        Material finalMaterial = material;
        if(material == XMaterial.POTATOES.parseMaterial()){
            finalMaterial = XMaterial.POTATO.parseMaterial();
        }

        if(material == XMaterial.CARROTS.parseMaterial()){
            finalMaterial = XMaterial.CARROT.parseMaterial();
        }

        ItemStack stack = new ItemStack(finalMaterial);
        int amountDrop = ThreadLocalRandom.current().nextInt(4 - 1) + 1;
        stack.setAmount(amountDrop);

        location.getWorld().dropItemNaturally(location, stack);
    }

}
