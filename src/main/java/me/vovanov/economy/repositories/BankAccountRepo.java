package me.vovanov.economy.repositories;

import me.vovanov.economy.Economy;
import me.vovanov.economy.model.EconomyUser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class BankAccountRepo {
    private static final File PLUGIN_DATA_FOLDER = Economy.getInstance().getDataFolder();
    private final File STORAGE_FILE = new File(PLUGIN_DATA_FOLDER, "EconomyUserData.yml");
    private FileConfiguration STORAGE_CONTENTS;

    private static final BankAccountRepo repo = new BankAccountRepo();

    public void setup() {
        Economy plugin = Economy.getInstance();
        try {
            if (STORAGE_FILE.createNewFile()) {
                plugin.getLogger().info("Successfully created user data file");
            }
            STORAGE_CONTENTS = YamlConfiguration.loadConfiguration(STORAGE_FILE);
        } catch (IOException e) {
            plugin.getLogger().severe("An I/O exception occurred while creating user data file\nDetails: "+e.getCause());
        }
    }

    synchronized public static BankAccountRepo getInstance() {
        return repo;
    }

    public void createNewAccount(UUID playerUUID) {
        Economy plugin = Economy.getInstance();
        String playerUUIDString = playerUUID.toString();
        ConfigurationSection existingSection = STORAGE_CONTENTS.getConfigurationSection(playerUUIDString);
        if (existingSection != null) return;
        ConfigurationSection section = STORAGE_CONTENTS.createSection(playerUUIDString);
        section.set("balance", 0);
        try {
            STORAGE_CONTENTS.save(STORAGE_FILE);
        } catch (IOException e) {
            plugin.getLogger().severe("An I/O exception occurred while creating an account for '"+ Bukkit.getOfflinePlayer(playerUUID).getName()+"' ("+playerUUIDString+")\nDetails: "+e.getCause());
        }
    }

    public EconomyUser load(UUID playerUUID) {
        ConfigurationSection playerEconomyData = STORAGE_CONTENTS.getConfigurationSection(playerUUID.toString());
        if (playerEconomyData == null) return null;
        int balance = playerEconomyData.getInt("balance");
        return new EconomyUser(balance, playerUUID);
    }

    public void save(EconomyUser user) {
        Economy plugin = Economy.getInstance();
        UUID playerUUID = user.getPlayerUUID();
        ConfigurationSection playerEconomyData = STORAGE_CONTENTS.getConfigurationSection(playerUUID.toString());
        OfflinePlayer holder = Bukkit.getOfflinePlayer(playerUUID);

        if (playerEconomyData == null) {
            plugin.getLogger().severe("Couldn't save data for '"+holder.getName()+"' ("+playerUUID+") because they don't have an account saved on a disc");
            return;
        }

        playerEconomyData.set("balance", user.getBalance());
        STORAGE_CONTENTS.set(playerUUID.toString(), playerEconomyData);
        try {
            STORAGE_CONTENTS.save(STORAGE_FILE);
        } catch (IOException e) {
            plugin.getLogger().severe("An I/O exception occurred while saving the data for '"+holder.getName()+"' ("+playerUUID+")\nDetails: "+e.getCause());
        }
    }

}
