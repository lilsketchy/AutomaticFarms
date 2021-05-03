package me.aglerr.automaticfarms.placeholderapi;

import me.aglerr.automaticfarms.AutomaticFarms;
import me.aglerr.automaticfarms.enums.DataType;
import me.aglerr.automaticfarms.managers.DataManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;


public class FarmExpansion extends PlaceholderExpansion {

    private final AutomaticFarms plugin;
    public FarmExpansion(final AutomaticFarms plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "automaticfarms";
    }

    @Override
    public @NotNull String getAuthor() {
        return "aglerr";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){
        if(player == null) return null;

        DataManager dataManager = plugin.getDataManager();

        if(identifier.equalsIgnoreCase("wheat")){
            return String.valueOf(dataManager.getBrokenCrop(player.getUniqueId(), DataType.WHEAT));
        }

        if(identifier.equalsIgnoreCase("carrot")){
            return String.valueOf(dataManager.getBrokenCrop(player.getUniqueId(), DataType.CARROT));
        }

        if(identifier.equalsIgnoreCase("potato")){
            return String.valueOf(dataManager.getBrokenCrop(player.getUniqueId(), DataType.POTATO));
        }

        if(identifier.equalsIgnoreCase("nether_wart")){
            return String.valueOf(dataManager.getBrokenCrop(player.getUniqueId(), DataType.NETHER_WART));
        }

        if(identifier.equalsIgnoreCase("cactus")){
            return String.valueOf(dataManager.getBrokenCrop(player.getUniqueId(), DataType.CACTUS));
        }

        if(identifier.equalsIgnoreCase("total")){
            return String.valueOf(dataManager.getTotalBrokenCrop(player.getUniqueId()));
        }

        if(identifier.equalsIgnoreCase("total_formatted")){
            DecimalFormat df = new DecimalFormat("###,###,###,###,###");
            return df.format(dataManager.getTotalBrokenCrop(player.getUniqueId()));
        }

        return null;
    }

}
