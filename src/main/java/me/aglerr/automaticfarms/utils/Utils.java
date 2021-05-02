package me.aglerr.automaticfarms.utils;

import me.aglerr.automaticfarms.AutomaticFarms;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;

public class Utils {

    public static boolean isNewVersion(){
        return !Bukkit.getVersion().contains("1.8") && !Bukkit.getVersion().contains("1.9") &&
                !Bukkit.getVersion().contains("1.10") && !Bukkit.getVersion().contains("1.11") &&
                !Bukkit.getVersion().contains("1.12");
    }

    public static void summonParticle(Location location) {
        for (int d = 0; d <= 90; d++) {
            Location particleLoc = location.clone();
            particleLoc.setX(location.getX() + Math.cos(d) * 0.4);
            particleLoc.setZ(location.getZ() + Math.sin(d) * 0.4);
            location.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, particleLoc, 1);
        }
    }

}
