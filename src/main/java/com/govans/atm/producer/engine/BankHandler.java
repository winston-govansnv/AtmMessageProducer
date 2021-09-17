package com.govans.atm.producer.engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BankHandler {
    private List<ClientAccount> clientAccounts;
    private List<Atm> atms;
    private int numOfClients;

    public void generateClientAccounts(int numOfClients) throws JsonProcessingException {
        this.numOfClients = numOfClients;
        //Generate atm list;
        generateATMList();
        //Generate client accounts
        clientAccounts = new ArrayList<>();

        for (int i = 0; i < numOfClients; i++) {
            ClientAccount acc = new ClientAccount();
            acc.setAccountAmount(5000);
            acc.setTypeOfAccount(ClientAccount.AccountType.CURRENT);
            acc.setAccountId("Acc" + (i + 1));
            clientAccounts.add(acc);
        }
    }

    private void generateATMList() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        String json = " [{\"id\":\"1\", \"locationName\":\"Airport Hotel Hato\",\"country\":\"Aruba\", \"lat\":\"12.184680\", \"lng\":\"-68.950702\"}, " +
                "{\"id\":\"2\", \"locationName\":\"AllSigns (Dr. J.P. Maalweg)\",\"country\":\"Aruba\", \"lat\":\"12.107244\", \"lng\":\"-68.904262\"}," +
                "{\"id\":\"3\", \"locationName\":\"Avila Beach Hotel\",\"country\":\"Aruba\", \"lat\":12.101193, \"lng\":-68.921772}," +
                "{\"id\":\"4\", \"locationName\":\"Barberia Jopi (Breedestraat/ Klipstraat)\",\"country\":\"Aruba\", \"lat\":12.107889, \"lng\":-68.935642}," +
                "{\"id\":\"5\", \"locationName\":\"Best Buy Supermarket (Strada)\",\"country\":\"Aruba\", \"lat\":12.110922, \"lng\":-68.905164}," +
                "{\"id\":\"6\", \"locationName\":\"Botika Janwe\",\"country\":\"Aruba\", \"lat\":12.105963, \"lng\":-68.891153}," +
                "{\"id\":\"7\", \"locationName\":\"Breedestraat Punda\",\"country\":\"Aruba\", \"lat\":12.105218, \"lng\":-68.933985}," +
                "{\"id\":\"8\", \"locationName\":\"Bugs Bunny Snack (Mahaai)\",\"country\":\"Aruba\", \"lat\":12.131759, \"lng\":-68.897944}" +
                "]";

        // 1. convert JSON array to Array objects
        Atm[] atmList = mapper.readValue(json, Atm[].class);

        System.out.println("JSON array to Array objects...");
        for (Atm atm : atmList) {
            System.out.println(atm);
        }
        System.out.println("\n");
        this.atms = Arrays.asList(atmList);
    }


    public synchronized List<ClientAccount>  getClientAccounts() {
        return clientAccounts;
    }

    public synchronized void withDraw(int index, double amount) throws InsufficientException {
        double currentAmount ;
        ClientAccount acc = clientAccounts.get(index);
        if (acc.getAccountAmount() >= amount) {
            currentAmount = acc.getAccountAmount();
            acc.setAccountAmount(currentAmount - (double) amount);
        } else {
            throw new InsufficientException("Account #" + acc.getAccountId() + " has insufficient balance for the transaction. Current balance is:" + acc.getAccountAmount());
        }
    }



    public List<Atm> getAtms() {
        return atms;
    }
}
