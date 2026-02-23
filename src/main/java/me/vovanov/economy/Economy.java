package me.vovanov.economy;

import me.vovanov.economy.commands.EcoReloadCommand;
import me.vovanov.economy.repositories.BankAccountRepo;
import me.vovanov.economy.services.DiscordIntegrationService;
import me.vovanov.economy.services.ConfigService;
import me.vovanov.economy.services.EconomyUserService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class Economy extends JavaPlugin implements Listener {
    private static Economy instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        ConfigService config = ConfigService.getInstance();
        config.reloadConfig();
        registerCommands();
        registerEventListeners();

        DiscordIntegrationService discord = DiscordIntegrationService.getInstance();
        discord.initialiseBot(config.getBotToken(), config.getChannelId());

        BankAccountRepo.getInstance().setup();

        this.getLogger().info("Plugin enabled");
    }

    @Override
    public void onDisable() {
        DiscordIntegrationService.getInstance().shutDownJDA();
        this.getLogger().info("Plugin disabled");
        //potassium
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
    }

    public static Economy getInstance() {
        return instance;
    }
}
