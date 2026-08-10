package me.vovanov.economy.listeners;

import me.vovanov.economy.model.EconomyUser;
import me.vovanov.economy.services.EconomyUserService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class AsyncPlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        UUID playerID = event.getUniqueId();
        EconomyUser user = EconomyUserService.getUser(playerID);

        if (user == null) {
            EconomyUserService.createBankAccount(playerID);
        }
    }
}
