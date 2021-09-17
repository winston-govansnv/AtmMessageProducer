package com.govans.atm.producer.engine.runner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.govans.atm.producer.engine.*;
import com.govans.atm.producer.service.MessageProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class MessageSubscriber {

    @Autowired
    MessageSubscriber(MessageProducerService messageProducerService,WebClient.Builder webClientBuilder,ObjectMapper objectMapper) throws JsonProcessingException {
        List<Atm> atmMachines;
        List<AtmThread> threads = new ArrayList<>();

        //Create a Bank object with x client accounts
        BankHandler bankHandler = new BankHandler();
        bankHandler.generateClientAccounts(10000);
        atmMachines = bankHandler.getAtms();



        for (Atm atm : atmMachines) {
            threads.add(new AtmThread(atm, bankHandler, messageProducerService, webClientBuilder,objectMapper));
        }

        for (Thread thread : threads) {
            //thread.setDaemon(true);
            thread.start();
        }

        Runtime r1 = Runtime.getRuntime();
        r1.addShutdownHook(new ShutdownThread(threads));
    }
}




