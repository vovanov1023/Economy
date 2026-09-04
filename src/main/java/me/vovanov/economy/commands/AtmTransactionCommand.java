package me.vovanov.economy.commands;

import me.vovanov.economy.Economy;
import me.vovanov.economy.api.EconomyAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class AtmTransactionCommand implements CommandExecutor {
    private final EconomyAPI api;

    public AtmTransactionCommand(EconomyAPI api) {
        this.api = api;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Economy plugin = Economy.getInstance();
        if (!(sender instanceof Player player)) {
            plugin.getLogger().severe("This command is only available to players!");
            return true;
        }
        if (args.length == 0) {
            player.sendMessage("Not enough arguments");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid value");
            return true;
        }

        if (!api.hasBankAccount(player.getUniqueId())) {
            player.sendMessage("You need to have a bank account to use this command!");
            return true;
        }

        if (amount <= 0) {
            player.sendMessage("The amount should be greater than zero!");
            return true;
        }

        if (!api.isNearATM(player)) {
            player.sendMessage("You need to be near an ATM to use this command!");
            return true;
        }
        return executeTransaction(player, amount);
    }
    protected abstract boolean executeTransaction(Player player, int amount);
}
