package me.vovanov.economy.commands;

import me.vovanov.economy.Economy;
import me.vovanov.economy.api.EconomyAPI;
import me.vovanov.economy.model.enums.TransactionResult;
import me.vovanov.economy.services.ConfigService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class WithdrawCommand implements CommandExecutor {

    private final EconomyAPI api;

    public WithdrawCommand(EconomyAPI api) {
        this.api = api;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NonNull @NotNull String[] args) {
        Economy plugin = Economy.getInstance();
        if (!(sender instanceof Player player)) {
            plugin.getLogger().severe("This command is only available to players!");
            return true;
        }
        if (args.length == 0) {
            player.sendMessage("Not enough arguments");
            return false;
        }

        ConfigService config = ConfigService.getInstance();
        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid value");
            return false;
        }

        if (!api.hasBankAccount(player.getUniqueId())) {
            player.sendMessage("You need to have a bank account to use this command!");
            return true;
        }

        if (amount <= 0) {
            player.sendMessage("The amount should be greater than zero!");
            return false;
        }

        if (!api.isNearATM(player)) {
            player.sendMessage("You need to be near an ATM to use this command!");
            return true;
        }

        TransactionResult result = api.withdrawUser(player.getUniqueId(), amount);

        if (result == TransactionResult.SUCCESS) {
            HashMap<Integer, ItemStack> didNotFitMap = player.getInventory().addItem(new ItemStack(config.getCurrencyMaterial(), amount));
            player.sendMessage("Successfully withdrew "+amount+" "+config.getCurrencyAlias()+" to your bank account!");
            if (!didNotFitMap.isEmpty()) {
                AtomicInteger didNotFitAmount = new AtomicInteger();
                didNotFitMap.values().forEach(item -> {didNotFitAmount.addAndGet(item.getAmount()); player.getLocation().getWorld().dropItem(player.getLocation(), item);});
                // TODO config option to send the cash that didn't fit back to your bank account
                player.sendMessage("Though some cash didn't fit in your inventory and was dropped to the ground ("+didNotFitAmount+" "+config.getCurrencyAlias()+")");
            }
            player.sendMessage("Your new balance is: "+api.getUserBalance(player.getUniqueId()));
            return true;
        }
        if (result == TransactionResult.INSUFFICIENT_FUNDS) {
            player.sendMessage("You have not enough money on your bank account!");
            return true;
        }

        return true;
    }
}
