package me.vovanov.economy.api;

import me.vovanov.economy.model.EconomyUser;
import me.vovanov.economy.model.enums.TransactionResult;
import me.vovanov.economy.services.ATMService;
import me.vovanov.economy.services.EconomyUserService;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EconomyAPIImpl implements EconomyAPI {
    private final EconomyUserService userService;
    private final ATMService atmService;

    public EconomyAPIImpl(EconomyUserService userService, ATMService atmService) {
        this.userService = userService;
        this.atmService = atmService;
    }

    /**
     * Withdraws an amount of money from the user
     * @param userID the users UUID
     * @param amount the amount to withdraw
     * @return the result of the transaction
     * */
    @Override
    public TransactionResult withdrawUser(UUID userID, int amount) {
        EconomyUser user = userService.getUser(userID);
        if (user == null) return TransactionResult.USER_NOT_FOUND;
        if (amount <= 0) return TransactionResult.INVALID_AMOUNT;
        if (user.getBalance() < amount) return TransactionResult.INSUFFICIENT_FUNDS;
        user.withdraw(amount);
        return TransactionResult.SUCCESS;
    }

    /**
     * Deposits an amount of money to the user
     * @param userID the users UUID
     * @param amount the amount to deposit
     * @return the result of the transaction
     * */
    @Override
    public TransactionResult depositUser(UUID userID, int amount) {
        EconomyUser user = userService.getUser(userID);
        if (user == null) return TransactionResult.USER_NOT_FOUND;
        if (amount <= 0) return TransactionResult.INVALID_AMOUNT;
        user.deposit(amount);
        return TransactionResult.SUCCESS;
    }

    /**
     * Gets the users balance
     * @param userID the users UUID
     * @return the balance or -1 if the user doesn't exist
     * */
    @Override
    public int getUserBalance(UUID userID) {
        EconomyUser user = userService.getUser(userID);
        if (user == null) return -1;
        return user.getBalance();
    }

    /**
     * @param userID the users UUID
    * @return true if the user has a bank account
    * */
    @Override
    public boolean hasBankAccount(UUID userID) {
        return userService.getUser(userID) != null;
    }

    @Override
    public boolean isNearATM(Player player) {
        return atmService.isNearATM(player);
    }

    @Override
    public void createBankAccount(UUID userID) {
        userService.createBankAccount(userID);
    }
}
