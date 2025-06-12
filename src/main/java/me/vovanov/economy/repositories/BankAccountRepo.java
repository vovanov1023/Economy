package me.vovanov.economy.repositories;

import me.vovanov.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class BankAccountRepo {
    private static final File PLUGIN_DATA_FOLDER = Economy.getInstance().getDataFolder();
    private static final File STORAGE_FILE = new File(PLUGIN_DATA_FOLDER, "EconomyUserData.yml");
    private static final FileConfiguration STORAGE_CONTENTS = YamlConfiguration.loadConfiguration(STORAGE_FILE);

    private final OfflinePlayer PLAYER;
    private final ConfigurationSection CONFIG_SECTION;
    private int balance;
    private final List<Integer> creditIDList;

    public static void setup() {
        Economy plugin = Economy.getInstance();
        try {
            if (STORAGE_FILE.createNewFile()) {
                plugin.getLogger().info("Successfully created user data file");
            }
        } catch (IOException e) {
            plugin.getLogger().severe("An I/O error occurred while creating user data file\nDetails: "+e.getCause());
        }
    }

    private BankAccountRepo(OfflinePlayer player, ConfigurationSection section) {
        this.PLAYER = player;
        this.CONFIG_SECTION = section;
        this.balance = section.getInt("balance");
        this.creditIDList = section.getIntegerList("credits");
    }

    /**
     * Gets the repository to get or modify the economy data of a player
     * @param player the player to get the data for
     * @return the data or null if the player doesn't have a bank account
     */
    public static BankAccountRepo getData(OfflinePlayer player) {
        ConfigurationSection section = STORAGE_CONTENTS.getConfigurationSection(player.getUniqueId().toString());
        if (section == null) return null;
        return new BankAccountRepo(player, section);
    }

    private void saveSection() {
        Economy plugin = Economy.getInstance();
        OfflinePlayer holder = this.PLAYER;
        STORAGE_CONTENTS.set(holder.getUniqueId().toString(), CONFIG_SECTION);
        try {
            STORAGE_CONTENTS.save(STORAGE_FILE);
        } catch (IOException e) {
            plugin.getLogger().severe("An I/O error occurred while saving the data for '"+holder.getName()+"' ("+holder.getUniqueId()+")\nDetails: "+e.getCause());
        }
    }

    public OfflinePlayer getPlayer() {
        return PLAYER;
    }

    public int getBalance() {
        return balance;
    }

    public void saveBalance(int newBalance) {
        this.balance = newBalance;
        CONFIG_SECTION.set("balance", newBalance);
        saveSection();
    }

    public List<Integer> getCreditIDList() {
        return this.creditIDList;
    }

    public void removeCreditID(Integer id) {
        this.creditIDList.remove(id);
        CONFIG_SECTION.set("credits", creditIDList);
        saveSection();
    }

    public void addCreditID(Integer id) {
        this.creditIDList.add(id);
        CONFIG_SECTION.set("credits", creditIDList);
        saveSection();
    }

}
