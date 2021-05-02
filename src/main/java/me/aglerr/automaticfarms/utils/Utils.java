package me.aglerr.automaticfarms.utils;

import me.aglerr.automaticfarms.AutomaticFarms;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.material.Crops;
import org.bukkit.material.NetherWarts;
import org.bukkit.scheduler.BukkitRunnable;

public class Utils {

    private final static int MAX_CROP = 7;
    private final static int MAX_NETHER_WART = 3;

    public static boolean isNewVersion(){
        return !Bukkit.getVersion().contains("1.8") && !Bukkit.getVersion().contains("1.9") &&
                !Bukkit.getVersion().contains("1.10") && !Bukkit.getVersion().contains("1.11") &&
                !Bukkit.getVersion().contains("1.12");
    }

    public static boolean isFullyGrown(Block block){

        Ageable ageable = (Ageable) block.getBlockData();
        return ageable.getAge() == ageable.getMaximumAge();

    }

    public static boolean isFullyGrown(BlockState state){

        if(state.getData() instanceof Crops){
            Crops crops = (Crops) state.getData();
            return crops.getData() == MAX_CROP;
        }

        if(state.getData() instanceof NetherWarts){
            NetherWarts warts = (NetherWarts) state.getData();
            return warts.getData() == MAX_NETHER_WART;
        }

        return false;
    }

    public static void summonParticle(AutomaticFarms plugin, Location location) {
        for (int d = 0; d <= 90; d++) {
            Location particleLoc = location.clone();
            particleLoc.setX(location.getX() + Math.cos(d) * 0.4);
            particleLoc.setZ(location.getZ() + Math.sin(d) * 0.4);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                    location.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, particleLoc, 1));
        }
    }

    public static void regrowsNewVersion(AutomaticFarms plugin, Block block, int targetAge, Location location){
        new BukkitRunnable(){
            @Override
            public void run(){

                if(!(block.getBlockData() instanceof Ageable)){
                    this.cancel();
                    return;
                }

                Ageable finalAgeable = (Ageable) block.getBlockData();
                int age = finalAgeable.getAge();

                if(age >= targetAge){
                    plugin.removeGrowingBlock(block);
                    this.cancel();
                    return;
                }

                finalAgeable.setAge(age + 1);
                block.setBlockData(finalAgeable);

                summonParticle(plugin, location);

            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public static void regrowsOldVersion(AutomaticFarms plugin, BlockState state, Location location){
        new BukkitRunnable(){
            @Override
            public void run() {

                if(!(state.getData() instanceof Crops)){
                    this.cancel();
                    return;
                }

                Crops crop = (Crops) state.getData();

                if(crop.getData() >= MAX_CROP){
                    plugin.removeGrowingBlock(state.getBlock());
                    this.cancel();
                    return;
                }

                crop.setData((byte) (crop.getData() + 1));
                state.update();

                summonParticle(plugin, location);

            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public static void regrowsOldNetherWart(AutomaticFarms plugin, BlockState state, Location location){
        new BukkitRunnable(){
            @Override
            public void run(){

                if(!(state.getData() instanceof NetherWarts)){
                    this.cancel();
                    return;
                }

                NetherWarts warts = (NetherWarts) state.getData();

                if(warts.getData() >= MAX_NETHER_WART){
                    plugin.removeGrowingBlock(state.getBlock());
                    this.cancel();
                    return;
                }

                warts.setData((byte) (warts.getData() + 1));
                state.update();

                summonParticle(plugin, location);

            }
        }.runTaskTimer(plugin, 0L, 20L);
    }


}
