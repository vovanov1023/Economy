package me.vovanov.economy.commands;

import me.vovanov.economy.api.EconomyAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class BalanceCommand implements CommandExecutor {

    private final EconomyAPI api;

    public BalanceCommand(EconomyAPI api) {
        this.api = api;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NonNull @NotNull String[] args) {

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("You may only see other's balances (/balance [name])");
                return true;
            }
            int balance = api.getUserBalance(player.getUniqueId());
            if (balance == -1) {
                player.sendMessage("You need to have a bank account to use this command!");
                return true;
            }
            player.sendMessage("Your balance is: "+balance);
        }

        if (!sender.hasPermission("economy.balance.others")) {
            sender.sendMessage("You don't have permission to see other balances!");
            return true;
        }

        String name = args[0];
        OfflinePlayer queried = Bukkit.getOfflinePlayerIfCached(name);
        if (queried == null) {
            sender.sendMessage(name+" had never joined the server before");
            return true;
        }
        int balance = api.getUserBalance(queried.getUniqueId());
        if (balance == -1) {
            sender.sendMessage(name+" doesn't have a bank account");
            return true;
        }
        sender.sendMessage(name+"'s balance is: "+balance);
        return true;
    }
}
