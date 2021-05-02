package me.aglerr.automaticfarms;

import com.cryptomorin.xseries.XMaterial;
import me.aglerr.automaticfarms.listeners.BreakNewVersion;
import me.aglerr.automaticfarms.listeners.BreakOldVersion;
import me.aglerr.automaticfarms.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class AutomaticFarms extends JavaPlugin {

    private final Set<Material> crops = new HashSet<>();
    private final Set<Block> growing = new HashSet<>();

    @Override
    public void onEnable(){

        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);

        initializeCrops();
        registerListeners();

    }

    private void registerListeners(){
        if(Utils.isNewVersion()) {
            Bukkit.getPluginManager().registerEvents(new BreakNewVersion(this), this);
            return;
        }

        Bukkit.getPluginManager().registerEvents(new BreakOldVersion(this), this);
    }

    private void initializeCrops(){
        FileConfiguration config = this.getConfig();

        if(config.getBoolean("crops.carrots")){
            crops.add(XMaterial.CARROTS.parseMaterial());
        }

        if(config.getBoolean("crops.potato")){
            crops.add(XMaterial.POTATOES.parseMaterial());
        }

        if(config.getBoolean("crops.wheat")){
            crops.add(XMaterial.WHEAT.parseMaterial());
        }

        if(config.getBoolean("crops.netherWart")){
            crops.add(XMaterial.NETHER_WART.parseMaterial());
        }

    }

    public void removeGrowingBlock(Block block){
        growing.remove(block);
    }

    public void addGrowingBlock(Block block){
        growing.add(block);
    }

    public boolean isBlockGrowing(Block block){
        return growing.contains(block);
    }

    public Set<Material> getCrops() { return crops; }

}
