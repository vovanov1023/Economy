package me.vovanov.economy.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreditEntry {
    int originalPrincipal;
    int amount;
    final double interestRate;
    final long deadline;
    final UUID id;

    public CreditEntry(int originalPrincipal, int amount, double interestRate, long deadline) {
        this.originalPrincipal = originalPrincipal;
        this.amount = amount;
        this.interestRate = interestRate;
        this.deadline = deadline;
        this.id = UUID.randomUUID();
    }

    public CreditEntry(UUID id, int originalAmount, int amount, double interestRate, long deadline) {
        this.originalPrincipal = originalAmount;
        this.amount = amount;
        this.interestRate = interestRate;
        this.deadline = deadline;
        this.id = id;
    }

    public void applyInterest() {
        this.amount += (int) Math.round(this.amount*interestRate);
    }

    public boolean isOverdue() {
        return deadline < System.currentTimeMillis();
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id.toString());
        map.put("amount", amount);
        map.put("original-principal", originalPrincipal);
        map.put("interest-rate", interestRate);
        map.put("deadline", deadline);
        return map;
    }

    public void withdraw(int toWithdraw) {
        this.amount -= toWithdraw;
    }

    public int getOriginalPrincipal() {
        return this.originalPrincipal;
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
