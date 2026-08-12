package me.vovanov.economy;

import me.vovanov.economy.api.EconomyAPI;
import me.vovanov.economy.api.EconomyAPIImpl;
import me.vovanov.economy.commands.BalanceCommand;
import me.vovanov.economy.commands.DepositCommand;
import me.vovanov.economy.commands.EcoReloadCommand;
import me.vovanov.economy.commands.WithdrawCommand;
import me.vovanov.economy.listeners.AsyncPlayerJoinListener;
import me.vovanov.economy.repositories.*;
import me.vovanov.economy.services.ATMService;
import me.vovanov.economy.services.DiscordIntegrationService;
import me.vovanov.economy.services.ConfigService;
import me.vovanov.economy.services.EconomyUserService;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Economy extends JavaPlugin implements Listener {
    private static Economy instance;
    private DatabaseManager dbManager;
    private EconomyUserService userService;
    private ATMService atmService;
    private EconomyAPI api;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        ConfigService config = ConfigService.getInstance();
        config.reloadConfig();

        // SQLite database initialization
        dbManager = new DatabaseManager(getDataFolder());

        try {
            BankAccountRepository accountRepo = new SQLiteBankAccountRepository(dbManager);
            accountRepo.setup();
            userService = new EconomyUserService(accountRepo);

            ATMRepository atmRepo = new SQLiteATMRepository(dbManager);
            atmRepo.setup();
            atmService = new ATMService(atmRepo);

            api = new EconomyAPIImpl(userService, atmService);

            getServer().getServicesManager().register(EconomyAPI.class, api, this, ServicePriority.Normal);

            this.getLogger().info("Database initialized successfully");
        } catch (Exception e) {
            this.getLogger().severe("Couldn't initialize the SQLite database!");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerCommands();
        registerEventListeners();

        setupVault();

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, task -> userService.saveUsers(), 6000, 6000);

        DiscordIntegrationService discord = DiscordIntegrationService.getInstance();
        discord.initialiseBot(config.getBotToken(), config.getChannelId());

        this.getLogger().info("Plugin enabled");
    }

    @Override
    public void onDisable() {
        DiscordIntegrationService.getInstance().shutDownJDA();
        userService.saveUsers();
        if (this.dbManager != null) {
            this.dbManager.close();
            this.getLogger().info("Database connection closed");
        }
        this.getLogger().info("Plugin disabled");
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        userService.unloadUser(event.getPlayer());
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("economyreload")).setExecutor(new EcoReloadCommand());
        Objects.requireNonNull(getCommand("withdraw")).setExecutor(new WithdrawCommand(api));
        Objects.requireNonNull(getCommand("deposit")).setExecutor(new DepositCommand(api));
        Objects.requireNonNull(getCommand("balance")).setExecutor(new BalanceCommand(api));
    }

    private void registerEventListeners() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new AsyncPlayerJoinListener(api), this);
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


}
