package me.vovanov.economy.repositories;

import me.vovanov.economy.model.EconomyUser;

import java.util.UUID;

public interface BankAccountRepository {
    
    /**
     * Initializes the repository: connects to the database and creates the necessary tables.
     */
    void setup() throws Exception;

    /**
     * Creates a new bank account for the player by their UUID.
     *
     * @param playerUUID player UUID
     */
    void createNewAccount(UUID playerUUID);

    /**
     * Loads the user data from the database.
     *
     * @param playerUUID UUID of the player
     * @return EconomyUser object or null, if the account hasn't been found
     */
    EconomyUser load(UUID playerUUID);

    /**
     * Saves the user to the database
     *
     * @param user user data needed for saving
     */
    void save(EconomyUser user);
    
    /**
     * Closes the database connection
     */
    void close();
}
