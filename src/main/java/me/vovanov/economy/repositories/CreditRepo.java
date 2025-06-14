package me.vovanov.economy.repositories;

import me.vovanov.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class CreditRepo {
    private static final File PLUGIN_DATA_FOLDER = Economy.getInstance().getDataFolder();
    private static final File STORAGE_FILE = new File(PLUGIN_DATA_FOLDER, "Credits.yml");
    private static final FileConfiguration STORAGE_CONTENTS = YamlConfiguration.loadConfiguration(STORAGE_FILE);


}
