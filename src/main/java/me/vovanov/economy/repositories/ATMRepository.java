package me.vovanov.economy.repositories;

import me.vovanov.economy.model.ATM;

import java.util.List;

public interface ATMRepository {
    /**
     * Creates a table for ATMs in the database if it doesn't exist
     */
    void setup() throws Exception;

    /**
     * Returns a list of all ATMs from the repo
     */
    List<ATM> loadAllATMs();

    /**
     * Saves a new ATM in the database and returns itself with an assigned ID
     */
    ATM createATM(String worldName, int x, int y, int z);

    /**
     * Deletes an ATM from the DB by its ID
     */
    void deleteATM(int id);
}
