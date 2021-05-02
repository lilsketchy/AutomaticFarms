package me.aglerr.automaticfarms;

import com.cryptomorin.xseries.XMaterial;
import me.aglerr.automaticfarms.listeners.BreakNewVersion;
import me.aglerr.automaticfarms.listeners.BreakOldVersion;
import me.aglerr.automaticfarms.managers.CropsManager;
import me.aglerr.automaticfarms.managers.GrowingManager;
import me.aglerr.automaticfarms.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


public class AutomaticFarms extends JavaPlugin {

    private CropsManager cropsManager;
    private GrowingManager growingManager;

    public static boolean IS_NEW_VERSION;

    public final static int MAX_CROP = 7;
    public final static int MAX_NETHER_WART = 3;

    @Override
    public void onEnable(){

        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);

        IS_NEW_VERSION = Utils.isNewVersion();

        cropsManager = new CropsManager(this);
        growingManager = new GrowingManager();

        cropsManager.initializeCrops();
        registerListeners();

    }

    private void registerListeners(){
        if(IS_NEW_VERSION) {
            Bukkit.getPluginManager().registerEvents(new BreakNewVersion(this), this);
            return;
        }

        Bukkit.getPluginManager().registerEvents(new BreakOldVersion(this), this);
    }

    public CropsManager getCropsManager() {
        return cropsManager;
    }

    public GrowingManager getGrowingManager() {
        return growingManager;
    }
}
