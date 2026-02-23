package me.vovanov.economy.services;

import me.vovanov.economy.Economy;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

public class ConfigService {

    private String botToken = "";
    private long channelId = -1;
    private Material atmBlock = Material.LODESTONE;
    private Material currencyMaterial = Material.DEEPSLATE_DIAMOND_ORE;
    private String currencyAlias = "DO";
    private int atmRadius = 4;

    private static final ConfigService service = new ConfigService();

    public static ConfigService getInstance() {
        return service;
    }

    /**
     * @return Material representing the ATM
     */
    public Material getAtmBlock() {
        return this.atmBlock;
    }

    /**
     * @return Material representing the currency
     */
    public Material getCurrencyMaterial() {
        return this.currencyMaterial;
    }

    /**
     * @return the currency alias
     */
    public String getCurrencyAlias() {
        return this.currencyAlias;
    }

    /**
     * @return the radius in which ATMs should function
     */
    public int getAtmRadius() {
        return this.atmRadius;
    }

    /**
     * @return the Discord Bot token
     */
    public String getBotToken() {
        return this.botToken;
    }

    /**
     * @return the logging channel id
     */
    public long getChannelId() {
        return this.channelId;
    }

    /**
     * Reloads the config and fetches values from the disc.<br>
     * If any of the values are invalid, the default values will be used
     * @apiNote Internal use only
     */
    @ApiStatus.Internal
    public void reloadConfig() {
        Economy plugin = Economy.getInstance();
        plugin.reloadConfig();
        setupConfig(plugin);
    }

    private void setupConfig(JavaPlugin plugin) {
        FileConfiguration config = plugin.getConfig();

        botToken = config.getString("bot-token", botToken);

        channelId = config.getLong("channel-id", channelId);

        currencyMaterial = getMaterial(config, "currency", Material.DEEPSLATE_DIAMOND_ORE);

        currencyAlias = getCurrencyAlias(config);

        atmBlock = getMaterial(config, "atm-block", Material.LODESTONE);

        atmRadius = getAtmRadius(config);
    }

    private Material getMaterial(FileConfiguration config, String path, Material defaultValue) {
        String materialName = config.getString(path);
        if (materialName == null) {
            sendConfigWarning(path, defaultValue.toString());
            return defaultValue;
        }
        Material material = Material.matchMaterial(materialName);
        if (material != null) return material;
        sendConfigWarning(path, defaultValue.toString());
        return defaultValue;
    }

    private String getCurrencyAlias(FileConfiguration config) {
        String string = config.getString("currency-alias");
        if (string == null) {
            sendConfigWarning("currency-alias", "DO");
            return "DO";
        }
        return string;
    }

    private int getAtmRadius(FileConfiguration config) {
        int number = config.getInt("atm-radius");
        if (number < 1) {
            sendConfigWarning("atm-radius", String.valueOf(4));
            return 4;
        }
        return number;
    }

    private void sendConfigWarning(String path, String defaultValue) {
        Economy plugin = Economy.getInstance();
        plugin.getLogger().warning("Value for path '" +path+ "' not found. Using default: "+defaultValue);
    }

}
