package me.aglerr.automaticfarms.managers;

import me.aglerr.automaticfarms.AutomaticFarms;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.material.Crops;
import org.bukkit.material.NetherWarts;

import java.util.HashSet;
import java.util.Set;

public class GrowingManager {

    private final Set<Block> growing = new HashSet<>();

    public void removeGrowingBlock(Block block){
        growing.remove(block);
    }

    public void addGrowingBlock(Block block){
        growing.add(block);
    }

    public boolean isBlockGrowing(Block block){
        return growing.contains(block);
    }

    public boolean isFullyGrown(Block block){

        Ageable ageable = (Ageable) block.getBlockData();
        return ageable.getAge() == ageable.getMaximumAge();

    }

    public boolean isFullyGrown(BlockState state){

        if(state.getData() instanceof Crops){
            Crops crops = (Crops) state.getData();
            return crops.getData() == AutomaticFarms.MAX_CROP;
        }

        if(state.getData() instanceof NetherWarts){
            NetherWarts warts = (NetherWarts) state.getData();
            return warts.getData() == AutomaticFarms.MAX_NETHER_WART;
        }

        return false;
    }

}
