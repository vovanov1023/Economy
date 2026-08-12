package me.vovanov.economy.api;

import me.vovanov.economy.model.enums.TransactionResult;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface EconomyAPI {
    /**
     * Withdraws an amount of money from the user
     * @param userID the players UUID
     * @param amount the amount to withdraw
     * @return the result of the transaction
     * */
    TransactionResult withdrawUser(UUID userID, int amount);

    /**
     * Deposits an amount of money to the user
     * @param userID the players UUID
     * @param amount the amount to deposit
     * @return the result of the transaction
     * */
    TransactionResult depositUser(UUID userID, int amount);

    /**
     * Gets the users balance
     * @param userID the players UUID
     * @return the balance or -1 if the user doesn't exist
     * */
    int getUserBalance(UUID userID);

    /**
     * @param userID the players UUID
    * @return true if the user has a bank account
    * */
    boolean hasBankAccount(UUID userID);

    /**
     * @param player the player
     * @return true if a player is near an ATM ("near" is defined in the config)
     * */
    boolean isNearATM(Player player);

    /**
     * Creates a bank account for the player (or just doesn't if it already exists)
     * @param userID the players UUID
     * */
    void createBankAccount(UUID userID);
}
