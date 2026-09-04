package me.vovanov.economy.api;

import me.vovanov.economy.model.ATM;
import me.vovanov.economy.model.enums.TransactionResult;
import org.bukkit.entity.Player;

import java.util.List;
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
     * Transfers an amount of money from user to another
     * @param senderID the sender player UUID
     * @param receiverID the receiver playerUUID
     * @param amount the amount to transfer
     * @return the result of the transaction
     * */
    TransactionResult transferMoney(UUID senderID, UUID receiverID, int amount);

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
     * Creates a bank account for the player (or just doesn't if it already exists)
     * @param userID the players UUID
     * */
    void createBankAccount(UUID userID);

    /**
     * atm create
     * */
    ATM createATM(int x, int y, int z, String worldName);

    /**
     * atm remove
     * */
    void removeATM(int id);

    /**
     * atm immutable list gete
     * */
    List<ATM> getATMList();

    /**
     * @param player the player
     * @return true if a player is near an ATM ("near" is defined in the config)
     * */
    boolean isNearATM(Player player);

    /**
     * get if thingy existe
     * */
    boolean doesATMExist(int x, int y, int z, String worldName);

    /**
     * get if thingy existe
     * */
    boolean doesATMExist(int id);
}
