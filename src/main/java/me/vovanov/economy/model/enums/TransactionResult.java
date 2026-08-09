package me.vovanov.economy.model.enums;

public enum TransactionResult {
    SUCCESS,
    INSUFFICIENT_FUNDS,
    INVALID_AMOUNT,
    SENDER_NOT_FOUND,
    RECEIVER_NOT_FOUND,
    USER_NOT_FOUND,
    INVALID_INTEREST,
    INVALID_DEADLINE,
    DEBTOR_NOT_FOUND,
    CREDIT_NOT_FOUND,
    EXCESSIVE_AMOUNT,
}
