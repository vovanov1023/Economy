package me.vovanov.economy.model;

import java.util.UUID;

public class CreditEntry {
    int amount;
    final double interestRate;
    final long deadline;
    final UUID id;

    public CreditEntry(int amount, double interestRate, long deadline) {
        this.amount = amount;
        this.interestRate = interestRate;
        this.deadline = deadline;
        this.id = UUID.randomUUID();
    }

    public void applyInterest() {
        this.amount += (int) Math.round(this.amount*interestRate);
    }

    public boolean isOverdue() {
        return deadline > System.currentTimeMillis();
    }

    public int getAmount() {
        return this.amount;
    }

    public long getDeadline() {
        return this.deadline;
    }

    public UUID getId() {
        return this.id;
    }

    public double getInterestRate() {
        return this.interestRate;
    }
}
