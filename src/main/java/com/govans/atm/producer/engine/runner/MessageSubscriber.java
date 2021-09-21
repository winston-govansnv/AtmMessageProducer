package com.govans.atm.producer.engine.runner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.govans.atm.producer.engine.*;
import com.govans.atm.producer.entity.AtmTransaction;
import com.govans.atm.producer.model.AppProperties;
import com.govans.atm.producer.service.AtmService;
import com.govans.atm.producer.service.MessageProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import static com.govans.atm.producer.util.CommonUtil.delay;

@Component
@Slf4j
public class MessageSubscriber {
    private final AtmService atmService;

    @Autowired
    MessageSubscriber(AppProperties appProperties, MessageProducerService messageProducerService, WebClient.Builder webClientBuilder, ObjectMapper objectMapper) throws JsonProcessingException {
        List<Atm> atmMachines;
        int executionCount = appProperties.getExecutioncount();

        //Create a Bank object with x client accounts
        BankHandler bankHandler = new BankHandler();
        bankHandler.generateClientAccounts(10000);
        atmMachines = bankHandler.getAtms();
        this.atmService = new AtmService(bankHandler, appProperties, messageProducerService, webClientBuilder, objectMapper);

        List<CompletableFuture<AtmTransaction>> listCf = new ArrayList<>();

        IntStream stream = IntStream.range(1, executionCount+1);
        stream.sequential().forEach(a->{
            delay(200);
            for (Atm atm : atmMachines) {
                CompletableFuture<AtmTransaction> cfTrx = CompletableFuture
                        .supplyAsync(() -> atmService.RegisterTrx(atm, true));
                listCf.add(cfTrx);
            }

            CompletableFuture.allOf(listCf.toArray(new CompletableFuture[listCf.size()])).whenComplete((v, th) -> listCf.forEach(cf -> {
                try {
                    log.info(cf.get().getAtmLocation());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }));
        });





    }

}




