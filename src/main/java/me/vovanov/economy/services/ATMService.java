package me.vovanov.economy.services;

import me.vovanov.economy.model.ATM;
import me.vovanov.economy.repositories.ATMRepository;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ATMService {
    private final ArrayList<ATM> ATMList = new ArrayList<>();
    private final ATMRepository repository;

    public ATMService(ATMRepository repo) {
        this.repository = repo;
        ATMList.addAll(repo.loadAllATMs());
    }

    public boolean isNearATM(Player player) {
        if (ATMList.isEmpty()) return false;
        int ATMRadius = ConfigService.getInstance().getAtmRadius();
        long ATMRadiusSquared = (long) ATMRadius *ATMRadius;
        Location playerLocation = player.getLocation();
        for (ATM atm : ATMList) {
            if (!playerLocation.getWorld().getName().equals(atm.worldName())) continue;
            Location atmLocation = new Location(Bukkit.getWorld(atm.worldName()), atm.x(), atm.y(), atm.z());
            if (playerLocation.distanceSquared(atmLocation) <= ATMRadiusSquared) {
                return true;
            }
        }
        return false;
    }

    public void createATM(int x, int y, int z, String worldName) {
        ATM newATM = repository.createATM(worldName, x, y, z);
        ATMList.add(newATM);
    }

    public void removeATM(int id) {
        repository.deleteATM(id);
        ATMList.removeIf(atm -> atm.id() == id);
    }

    public List<ATM> getATMList() {
        return Collections.unmodifiableList(ATMList);
    }
}
