package me.vovanov.economy;

import me.vovanov.economy.commands.EcoReloadCommand;
import me.vovanov.economy.repositories.BankAccountRepo;
import me.vovanov.economy.services.DiscordIntegrationService;
import me.vovanov.economy.services.ConfigService;
import org.bukkit.plugin.java.JavaPlugin;

public final class Economy extends JavaPlugin {
    private static Economy instance;

    @Override
    public void onEnable() {
        instance = this;

        ConfigService service = ConfigService.getInstance();
        service.reloadConfig();
        registerCommands();

        BankAccountRepo.setup();

        this.getLogger().info("Plugin enabled");
    }

    @Override
    public void onDisable() {
        DiscordIntegrationService.getInstance().shutDownJDA();
        this.getLogger().info("Plugin disabled");
        //potassium
    }

    private void registerCommands() {
        getCommand("economyreload").setExecutor(new EcoReloadCommand());
    }

    public static Economy getInstance() {
        return instance;
    }
}
