package me.vovanov.economy.commands;

import me.vovanov.economy.api.EconomyAPI;
import me.vovanov.economy.model.enums.TransactionResult;
import me.vovanov.economy.services.ConfigService;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class WithdrawCommand extends AtmTransactionCommand {

    private final EconomyAPI api;

    public WithdrawCommand(EconomyAPI api) {
        super(api);
        this.api = api;
    }

    @Override
    protected boolean executeTransaction(Player player, int amount) {
        ConfigService config = ConfigService.getInstance();
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
