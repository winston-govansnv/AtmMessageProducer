package com.govans.atm.producer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.govans.atm.producer.engine.Atm;
import com.govans.atm.producer.engine.BankHandler;
import com.govans.atm.producer.entity.AtmTransaction;
import com.govans.atm.producer.model.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class AtmServiceTest {
    private final BankHandler bankHandler= new BankHandler();
    private final AppProperties appProperties=null;
    private final MessageProducerService messageProducerService=null;
    private final WebClient.Builder webClientBuilder=null;
    private final ObjectMapper objectMapper;
    {
        objectMapper = new ObjectMapper();
    }

    @Test
    void registerTrx() throws JsonProcessingException {
        //given
        int  numberOfClient=10000;
        bankHandler.generateClientAccounts(numberOfClient);
        List<Atm> atmMachines = bankHandler.getAtms();
        AtmService atmService= new AtmService(bankHandler,appProperties, messageProducerService,webClientBuilder, objectMapper);

        //when
        List<CompletableFuture<AtmTransaction>> listCf= new ArrayList<>();

        for (Atm atm : atmMachines) {
            CompletableFuture<AtmTransaction> cfTrx= CompletableFuture
                    .supplyAsync(()-> atmService.RegisterTrx(atm, false)); //Set persistData parameter to False
            listCf.add(cfTrx);
        }

        CompletableFuture.allOf(listCf.toArray(new CompletableFuture[listCf.size()])).whenComplete((v, th) -> listCf.forEach(cf -> {
            try {
                assertNotNull(cf.get());
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            } catch (ExecutionException e) {
                log.error(e.getMessage());
            }
        }));
    }
}