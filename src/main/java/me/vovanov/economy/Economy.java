package me.vovanov.economy;

import me.vovanov.economy.commands.EcoReloadCommand;
import me.vovanov.economy.integrations.DiscordIntegration;
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
        registerEvents();

        this.getLogger().info("Plugin enabled");
    }

    @Override
    public void onDisable() {
        DiscordIntegration.getInstance().shutDownJDA();
        this.getLogger().info("Plugin disabled");
    }

    private void registerCommands() {
        getCommand("economyreload").setExecutor(new EcoReloadCommand());
    }

    private void registerEvents() {

    }

    public static Economy getInstance() {
        return instance;
    }
}
