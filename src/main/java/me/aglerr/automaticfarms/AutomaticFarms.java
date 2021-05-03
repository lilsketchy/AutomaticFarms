package me.aglerr.automaticfarms;

import com.cryptomorin.xseries.XMaterial;
import me.aglerr.automaticfarms.configs.TemporaryDataConfiguration;
import me.aglerr.automaticfarms.listeners.BreakNewVersion;
import me.aglerr.automaticfarms.listeners.BreakOldVersion;
import me.aglerr.automaticfarms.managers.CropsManager;
import me.aglerr.automaticfarms.managers.DataManager;
import me.aglerr.automaticfarms.managers.GrowingManager;
import me.aglerr.automaticfarms.placeholderapi.FarmExpansion;
import me.aglerr.automaticfarms.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


public class AutomaticFarms extends JavaPlugin {

    private final CropsManager cropsManager = new CropsManager(this);;
    private final GrowingManager growingManager = new GrowingManager();;
    private final DataManager dataManager = new DataManager(this);

    private final TemporaryDataConfiguration dataConfiguration = new TemporaryDataConfiguration(this);

    public static boolean IS_NEW_VERSION;

    public final static int MAX_CROP = 7;
    public final static int MAX_NETHER_WART = 3;

    @Override
    public void onEnable(){

        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        dataConfiguration.setup();

        IS_NEW_VERSION = Utils.isNewVersion();

        cropsManager.initializeCrops();
        registerListeners();

        dataManager.loadPlayerData();

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new FarmExpansion(this).register();
        }

    }

    @Override
    public void onDisable(){
        dataManager.savePlayerDataToConfig();
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

    public DataManager getDataManager() {
        return dataManager;
    }

    public TemporaryDataConfiguration getDataConfiguration() {
        return dataConfiguration;
    }
}
