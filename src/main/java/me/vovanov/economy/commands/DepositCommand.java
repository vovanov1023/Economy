package me.vovanov.economy.commands;

import me.vovanov.economy.api.EconomyAPI;
import me.vovanov.economy.model.enums.TransactionResult;
import me.vovanov.economy.services.ConfigService;
import org.bukkit.entity.Player;

import static me.vovanov.economy.utils.InventoryUtils.removeCashFromInventory;

public class DepositCommand extends AtmTransactionCommand {
    private final EconomyAPI api;

    public DepositCommand(EconomyAPI api) {
        super(api);
        this.api = api;
    }

    @Override
    protected boolean executeTransaction(Player player, int amount) {
        ConfigService config = ConfigService.getInstance();
        int cash = removeCashFromInventory(player, amount);
        if (cash != -1) {
            player.sendMessage("You have not enough cash in your inventory! (You have " + cash + " " + config.getCurrencyAlias() + ")");
            return true;
        }

        TransactionResult result = api.depositUser(player.getUniqueId(), amount);

        if (result == TransactionResult.SUCCESS) {
            player.sendMessage("Successfully deposited "+amount+" "+config.getCurrencyAlias()+" to your bank account!");
            player.sendMessage("Your new balance is: "+api.getUserBalance(player.getUniqueId()));
            return true;
        }

        return true;
    }

}
