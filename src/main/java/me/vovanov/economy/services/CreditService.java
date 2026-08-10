package me.vovanov.economy.services;

import me.vovanov.economy.model.CreditEntry;
import me.vovanov.economy.model.EconomyUser;
import me.vovanov.economy.model.enums.TransactionResult;

import java.util.UUID;

public class CreditService {

    public static TransactionResult issueCredit(EconomyUser debtor, int amount, double interestRate, long deadline) {
        if (deadline < 0) return TransactionResult.INVALID_DEADLINE;
        if (interestRate < 0) return TransactionResult.INVALID_INTEREST;
        if (debtor == null) return TransactionResult.DEBTOR_NOT_FOUND;
        if (amount <= 0) return TransactionResult.INVALID_AMOUNT;

        CreditEntry newCredit = new CreditEntry(amount, amount, interestRate, deadline);
        debtor.deposit(amount);
        debtor.getActiveCredits().add(newCredit);

        return TransactionResult.SUCCESS;
    }

    public static TransactionResult payCredit(EconomyUser debtor, int amountToPay, UUID creditId) {
        if (debtor == null) return TransactionResult.DEBTOR_NOT_FOUND;
        if (amountToPay <= 0) return TransactionResult.INVALID_AMOUNT;

        CreditEntry paidCredit = null;
        for (CreditEntry credit : debtor.getActiveCredits()) {
            if (credit.getId().equals(creditId)) {
                paidCredit = credit;
                break;
            }
        }
        if (paidCredit == null) return TransactionResult.CREDIT_NOT_FOUND;
        if (paidCredit.getAmount() < amountToPay) return TransactionResult.EXCESSIVE_AMOUNT;
        paidCredit.withdraw(amountToPay);

        return TransactionResult.SUCCESS;
    }

    public static void processOverdue() {
        // TODO
    }
}
