package com.govans.atm.producer.engine;

import org.springframework.stereotype.Component;


public class ClientAccount {
    enum AccountType {
        CURRENT,
        SAVINGS
    }

    private String accountId;
    private double accountAmount;
    private AccountType typeOfAccount;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public double getAccountAmount() {
        return accountAmount;
    }

    public void setAccountAmount(double accountAmount) {
        this.accountAmount = accountAmount;
    }

    public AccountType getTypeOfAccount() {
        return typeOfAccount;
    }

    public void setTypeOfAccount(AccountType typeOfAccount) {
        this.typeOfAccount = typeOfAccount;
    }
}


