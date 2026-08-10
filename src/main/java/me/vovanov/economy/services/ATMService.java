package me.vovanov.economy.services;

import me.vovanov.economy.model.ATM;
import me.vovanov.economy.repositories.ATMRepository;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ATMService {
    private static final ArrayList<ATM> ATMList = new ArrayList<>();

    public static void setup(ATMRepository repository) {
        ATMList.addAll(repository.loadAllATMs());
    }

    public static boolean isNearATM(Player player) {
        int ATMRadius = ConfigService.getInstance().getAtmRadius();
        long ATMRadiusSquared = (long) ATMRadius *ATMRadius;
        Location playerLocation = player.getLocation();
        for (ATM atm : ATMList) {
            if (playerLocation.getWorld().getName().equals(atm.getWorldName())) continue;
            Location atmLocation = new Location(Bukkit.getWorld(atm.getWorldName()), atm.getX(), atm.getY(), atm.getZ());
            if (playerLocation.distanceSquared(atmLocation) <= ATMRadiusSquared) {
                return true;
            }
        }
        return false;
    }

    public static void addATM(ATM atm) {
        ATMList.add(atm);
    }

    public static void removeATM(int id) {
        ATMList.removeIf(atm -> atm.getId() == id);
    }
}
