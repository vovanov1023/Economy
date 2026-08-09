package me.vovanov.economy;

import me.vovanov.economy.commands.EcoReloadCommand;
import me.vovanov.economy.repositories.BankAccountRepository;
import me.vovanov.economy.repositories.DatabaseManager;
import me.vovanov.economy.repositories.SQLiteBankAccountRepository;
import me.vovanov.economy.services.DiscordIntegrationService;
import me.vovanov.economy.services.ConfigService;
import me.vovanov.economy.services.EconomyUserService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class Economy extends JavaPlugin implements Listener {
    private static Economy instance;
    private BankAccountRepository repository;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        ConfigService config = ConfigService.getInstance();
        config.reloadConfig();
        registerCommands();
        registerEventListeners();

        setupVault();

        BukkitRunnable autoSave = new BukkitRunnable() {@Override public void run() {EconomyUserService.saveUsers();}};
        autoSave.runTaskTimerAsynchronously(this, 6000, 6000);

        DiscordIntegrationService discord = DiscordIntegrationService.getInstance();
        discord.initialiseBot(config.getBotToken(), config.getChannelId());

        // Инициализация базы данных SQLite через репозиторий
        DatabaseManager dbManager = new DatabaseManager(getDataFolder());
        this.repository = new SQLiteBankAccountRepository(dbManager);
        try {
            this.repository.setup();
            this.getLogger().info("База данных успешно инициализирована");
        } catch (Exception e) {
            this.getLogger().severe("Не удалось инициализировать базу данных SQLite!");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.getLogger().info("Plugin enabled");
    }

    @Override
    public void onDisable() {
        DiscordIntegrationService.getInstance().shutDownJDA();
        if (this.repository != null) {
            this.repository.close();
            this.getLogger().info("Соединение с базой данных закрыто");
        }
        this.getLogger().info("Plugin disabled");
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        EconomyUserService.unloadUser(event.getPlayer());
    }

    private void registerCommands() {
        getCommand("economyreload").setExecutor(new EcoReloadCommand());
    }

    private void registerEventListeners() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new me.vovanov.economy.listeners.AsyncPlayerJoinListener(), this);
    }

    private void setupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        getServer().getServicesManager().register(
                net.milkbowl.vault.economy.Economy.class,
                new me.vovanov.economy.api.VaultEconomyProvider(),
                this,
                org.bukkit.plugin.ServicePriority.Highest
        );
    }

    public static Economy getInstance() {
        return instance;
    }

    public BankAccountRepository getRepository() {
        return this.repository;
    }
}
