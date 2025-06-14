package me.vovanov.economy.services;

import me.vovanov.economy.model.Credit;
import me.vovanov.economy.model.EconomyUser;

public class CreditService {

    public void issueCredit(EconomyUser user, Credit credit) {

    }

    public static Credit getCredit(Integer id) {
        return new Credit(0, 0L, 0);
    }
}
