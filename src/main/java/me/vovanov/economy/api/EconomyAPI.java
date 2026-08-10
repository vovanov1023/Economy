package me.vovanov.economy.api;

import me.vovanov.economy.model.EconomyUser;
import me.vovanov.economy.model.enums.TransactionResult;
import me.vovanov.economy.services.EconomyUserService;

import java.util.UUID;

public class EconomyAPI {
    /**
     * Withdraws an amount of money from the user
     * @param userID the users UUID
     * @param amount the amount to withdraw
     * @return the result of the transaction
     * */
    public static TransactionResult withdrawUser(UUID userID, int amount) {
        EconomyUser user = EconomyUserService.getUser(userID);
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
    public static TransactionResult depositUser(UUID userID, int amount) {
        EconomyUser user = EconomyUserService.getUser(userID);
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
    public static int getUserBalance(UUID userID) {
        EconomyUser user = EconomyUserService.getUser(userID);
        if (user == null) return -1;
        return user.getBalance();
    }

    /**
     * @param userID the users UUID
    * @return true if the user has a bank account
    * */
    public static boolean hasBankAccount(UUID userID) {
        return EconomyUserService.getUser(userID) != null;
    }
}
