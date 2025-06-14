package me.vovanov.economy.repositories;

import me.vovanov.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class BankAccountRepo {
    private static final File PLUGIN_DATA_FOLDER = Economy.getInstance().getDataFolder();
    private static final File STORAGE_FILE = new File(PLUGIN_DATA_FOLDER, "EconomyUserData.yml");
    private static final FileConfiguration STORAGE_CONTENTS = YamlConfiguration.loadConfiguration(STORAGE_FILE);

    private final OfflinePlayer PLAYER;
    private final ConfigurationSection CONFIG_SECTION;
    private final Set<Integer> CREDIT_ID_SET;

    public static void setup() {
        Economy plugin = Economy.getInstance();
        try {
            if (STORAGE_FILE.createNewFile()) {
                plugin.getLogger().info("Successfully created user data file");
            }
        } catch (IOException e) {
            plugin.getLogger().severe("An I/O exception occurred while creating user data file\nDetails: "+e.getCause());
        }
    }

    private BankAccountRepo(OfflinePlayer player, ConfigurationSection section) {
        this.PLAYER = player;
        this.CONFIG_SECTION = section;
        this.CREDIT_ID_SET = new HashSet<>(section.getIntegerList("credits"));
    }

    /**
     * Gets the repository to get or modify the economy data of a player
     * @param player the player to get the data for
     * @return the data or null if the player doesn't have a bank account
     */
    public static BankAccountRepo getRepo(OfflinePlayer player) {
        ConfigurationSection section = STORAGE_CONTENTS.getConfigurationSection(player.getUniqueId().toString());
        if (section == null) return null;
        return new BankAccountRepo(player, section);
    }

    public static void createNewAccount(OfflinePlayer player) {
        Economy plugin = Economy.getInstance();
        String uuid = player.getUniqueId().toString();
        ConfigurationSection existingSection = STORAGE_CONTENTS.getConfigurationSection(uuid);
        if (existingSection != null) return;
        ConfigurationSection section = STORAGE_CONTENTS.createSection(uuid);
        section.set("balance", 0);
        section.set("credits", Set.of());
        try {
            STORAGE_CONTENTS.save(STORAGE_FILE);
        } catch (IOException e) {
            plugin.getLogger().severe("An I/O exception occurred while creating an account for '"+player.getName()+"' ("+uuid+")\nDetails: "+e.getCause());
        }
    }

    private void saveSection() {
        Economy plugin = Economy.getInstance();
        OfflinePlayer holder = this.PLAYER;
        String uuid = holder.getUniqueId().toString();
        STORAGE_CONTENTS.set(uuid, CONFIG_SECTION);
        try {
            STORAGE_CONTENTS.save(STORAGE_FILE);
        } catch (IOException e) {
            plugin.getLogger().severe("An I/O exception occurred while saving the data for '"+holder.getName()+"' ("+uuid+")\nDetails: "+e.getCause());
        }
    }

    public OfflinePlayer getPlayer() {
        return PLAYER;
    }

    public int getBalance() {
        return CONFIG_SECTION.getInt("balance");
    }

    public void saveBalance(int newBalance) {
        CONFIG_SECTION.set("balance", newBalance);
        saveSection();
    }

    public Set<Integer> getCreditIDSet() {
        return this.CREDIT_ID_SET;
    }

    public void removeCreditID(Integer id) {
        this.CREDIT_ID_SET.remove(id);
        CONFIG_SECTION.set("credits", CREDIT_ID_SET);
        saveSection();
    }

    public void addCreditID(Integer id) {
        this.CREDIT_ID_SET.add(id);
        CONFIG_SECTION.set("credits", CREDIT_ID_SET);
        saveSection();
    }

}
