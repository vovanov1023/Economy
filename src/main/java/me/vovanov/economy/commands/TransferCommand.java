package me.vovanov.economy.commands;

import me.vovanov.economy.api.EconomyAPI;
import me.vovanov.economy.model.enums.TransactionResult;
import me.vovanov.economy.services.ConfigService;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TransferCommand implements CommandExecutor {
    EconomyAPI api;
    public TransferCommand(EconomyAPI api) {
        this.api = api;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = sender instanceof Player ? (Player) sender : null;
        if (player == null) {
            sender.sendMessage(Component.text("You need to be a player to use this command!"));
            return true;
        }
        if (args.length < 2) {
            player.sendMessage(Component.text("Not enough arguments!"));
            return true;
        }
        OfflinePlayer receiver = Bukkit.getOfflinePlayerIfCached(args[0]);
        if (receiver == null) {
            sender.sendMessage(Component.text("This player had never played before!"));
            return true;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid value");
            return true;
        }
        String comment = args.length < 3 ? "Transfer of own funds" : Arrays.stream(args).skip(2).collect(Collectors.joining(" "));
        TransactionResult result = api.transferMoney(player.getUniqueId(), receiver.getUniqueId(), amount);
        if (result == TransactionResult.RECEIVER_NOT_FOUND) {
            player.sendMessage(Component.text("The receiver doesn't have a bank account"));
            return true;
        }
        if (result == TransactionResult.SENDER_NOT_FOUND) {
            player.sendMessage(Component.text("You don't have a bank account"));
            return true;
        }
        if (result == TransactionResult.INSUFFICIENT_FUNDS) {
            player.sendMessage(Component.text("You don't have enough money"));
            return true;
        }
        if (result == TransactionResult.INVALID_AMOUNT) {
            player.sendMessage(Component.text("The amount is invalid"));
            return true;
        }
        if (result == TransactionResult.SUCCESS) {
            String senderName = player.getName();
            String receiverName = receiver.getName();
            String curAl = ConfigService.getInstance().getCurrencyAlias();
            player.sendMessage(Component.text("Successfully transferred " + amount + " " + curAl + " to " + receiverName));
            if (receiver.isOnline())
                ((Player) receiver).sendMessage(
                        Component.text("Received "+amount + " " + curAl + " from " + senderName + " Comment: "+comment)
                );
        }
        return true;
    }
}
