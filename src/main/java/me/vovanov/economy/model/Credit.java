package me.vovanov.economy.model;

public class Credit {
    private final Integer ID;
    private Integer amount;
    private final Long issueDate;
    private final Integer paymentPeriod;

    public Credit(Integer id, Long issueDate, Integer paymentPeriod) {
        ID = id;
        this.issueDate = issueDate;
        this.paymentPeriod = paymentPeriod;
    }

    public Integer getID() {
        return ID;
    }
}
