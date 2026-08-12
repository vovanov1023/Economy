package me.vovanov.economy.listeners;

import me.vovanov.economy.api.EconomyAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class AsyncPlayerJoinListener implements Listener {
    private final EconomyAPI api;

    public AsyncPlayerJoinListener(EconomyAPI api) {
        this.api = api;
    }
    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        UUID playerID = event.getUniqueId();
        if (!api.hasBankAccount(playerID)) {
            api.createBankAccount(playerID);
        }
    }
}
