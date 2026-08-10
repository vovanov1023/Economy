package me.vovanov.economy.commands;

import me.vovanov.economy.Economy;
import me.vovanov.economy.api.EconomyAPI;
import me.vovanov.economy.model.enums.TransactionResult;
import me.vovanov.economy.services.ATMService;
import me.vovanov.economy.services.ConfigService;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class DepositCommand implements CommandExecutor {

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

        if (!EconomyAPI.hasBankAccount(player.getUniqueId())) {
            player.sendMessage("You need to have a bank account to use this command!");
            return true;
        }

        if (amount <= 0) {
            player.sendMessage("The amount should be greater than zero!");
            return false;
        }

        if (!ATMService.isNearATM(player)) {
            player.sendMessage("You need to be near an ATM to use this command!");
            return true;
        }

        int cash = removeCashFromInventory(player, amount);
        if (cash != -1) {
            player.sendMessage("You have not enough cash in your inventory! (You have " + cash + " " + config.getCurrencyAlias() + ")");
            return true;
        }

        TransactionResult result = EconomyAPI.depositUser(player.getUniqueId(), amount);

        if (result == TransactionResult.SUCCESS) {
            player.sendMessage("Successfully deposited "+amount+" "+config.getCurrencyAlias()+" to your bank account!");
            player.sendMessage("Your new balance is: "+EconomyAPI.getUserBalance(player.getUniqueId()));
            return true;
        }

        return true;
    }

    private int removeCashFromInventory(Player player, int amount) {
        ConfigService config = ConfigService.getInstance();
        Material currencyMaterial = config.getCurrencyMaterial();
        Inventory inventory = player.getInventory();

        int cash = getCashAmount(player);
        if (cash < amount) return cash;

        int remainingToRemove = amount;

        for (int i = 0; i < inventory.getContents().length; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType() != currencyMaterial) continue;
            if (item.getAmount() <= remainingToRemove) {
                remainingToRemove -= item.getAmount();
                inventory.setItem(i, null);
            } else {
                item.setAmount(item.getAmount() - remainingToRemove);
                break;
            }
        }

        return -1;
    }

    private int getCashAmount(Player player) {
        ConfigService config = ConfigService.getInstance();
        Material currencyMaterial = config.getCurrencyMaterial();
        Inventory inventory = player.getInventory();

        int inventoryAmount = 0;
        for (int i = 0; i < inventory.getContents().length; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType() != currencyMaterial) continue;
            inventoryAmount += item.getAmount();
        }
        return inventoryAmount;
    }

}
