package com.example.silvercrest;

public class TransactionListModel {


    public String name;
    public String number;
    public String bank;
    public String amount;
    public String swift;
    public String purpose;

    public TransactionListModel(String name, String number, String bank, String amount, String swift, String purpose) {
        this.name = name;
        this.number = number;
        this.bank = bank;
        this.amount = amount;
        this.swift = swift;
        this.purpose = purpose;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getBank() {
        return bank;
    }

    public String getAmount() {
        return amount;
    }

    public String getSwift() {
        return swift;
    }

    public String getPurpose() {
        return purpose;
    }
}
