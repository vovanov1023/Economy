package me.vovanov.economy.repositories;

import me.vovanov.economy.Economy;

import java.io.File;
import java.io.IOException;

public class BankAccountRepo {
    private final File pluginDataFolder = Economy.getInstance().getDataFolder();
    private final File storageFile = new File(pluginDataFolder, "EconomyUserData.yml");

    private static BankAccountRepo instance;

    public void setup() {
        Economy plugin = Economy.getInstance();
        try {
            if (storageFile.createNewFile()) {
                plugin.getLogger().info("Successfully created user data file");
            }
        } catch (IOException e) {
            plugin.getLogger().severe("An I/O error occurred while creating user data file\nDetails: "+e.getCause());
        }
    }

    public static BankAccountRepo getInstance() {
        if (instance == null) instance = new BankAccountRepo();
        return instance;
    }
}
