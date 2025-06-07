package me.vovanov.economy.commands;

import me.vovanov.economy.services.ConfigService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public class EcoReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        ConfigService service = ConfigService.getInstance();
        service.reloadConfig();
        sender.sendMessage(text("Configuration has been reloaded successfully", YELLOW));
        return true;
    }
}
