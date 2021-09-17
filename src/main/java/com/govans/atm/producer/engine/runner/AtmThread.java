package com.govans.atm.producer.engine.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.govans.atm.producer.engine.Atm;
import com.govans.atm.producer.engine.BankHandler;
import com.govans.atm.producer.engine.ClientAccount;
import com.govans.atm.producer.engine.InsufficientException;
import com.govans.atm.producer.entity.AtmTransaction;
import com.govans.atm.producer.service.MessageProducerService;
import com.govans.atm.producer.util.DateTimeHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Random;

@Slf4j
public class AtmThread extends Thread {
    private boolean exit = false;
    private volatile boolean hasStopped=false;
    private volatile BankHandler bankHandler;
    private volatile Atm atm;
    private volatile DateTimeHelper timeHelper;
    private volatile String POST_OF_API = "http://localhost:8080/v1/atm-transaction";

    private volatile MessageProducerService messageProducerService;
    private volatile WebClient.Builder webClientBuilder;
    private volatile ObjectMapper objectMapper;

    public AtmThread(Atm atm, BankHandler bankHandler, MessageProducerService messageProducerService, WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.messageProducerService= messageProducerService;
        this.atm = atm;
        this.bankHandler = bankHandler;
        this.webClientBuilder= webClientBuilder;
        this.objectMapper=objectMapper;
    }

    @Override
    public void run() {
        while (!exit) {
            //Code that will run in a new thread
            try {
                Random randomAc = new Random();
                int accRandInt = randomAc.nextInt(bankHandler.getClientAccounts().size());
                ClientAccount client = bankHandler.getClientAccounts().get(accRandInt);

                //Random amounts to be withdraw from the account
                Random random = new Random();
                int randomInt = random.nextInt(10);
                if (randomInt == 0)
                    randomInt = 1;
                double amount = randomInt * 25;

                try {
                    bankHandler.withDraw(accRandInt, amount);
                    log.info("Customer #" + client.getAccountId() + " withdraw: " + amount + " from ATM at location:" + atm.getLocationName() + ". New balance is: " + client.getAccountAmount() + ". Running on thread " + Thread.currentThread().getName());
                    AtmTransaction trx = new AtmTransaction();
                    trx.setBankName("MCB Curacao");
                    trx.setAtmLocation(atm.getLocationName());
                    trx.setCountry(atm.getCountry());
                    trx.setLat(atm.getLat());
                    trx.setLng(atm.getLng());
                    trx.setAmount(amount);
                    trx.setAccountNumber(client.getAccountId());

                    if(messageProducerService!=null) {
                        messageProducerService.processMessageEvent(trx);

                        String jsonString = objectMapper.writeValueAsString(trx);
                        log.info("jsonString ::>>>>"+ jsonString);
//                        webClientBuilder.build()
//                                .post()
//                                .uri(POST_OF_API,jsonString)
//                                .retrieve()
//                                .bodyToMono(AtmTransaction.class)
//                                .block();

                        webClientBuilder.build().post()
                                .uri(POST_OF_API)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(Mono.just(trx), AtmTransaction.class)
                                .retrieve()
                                .bodyToMono(AtmTransaction.class).block();
                    }else{
                        log.info("messageProducerService is null");
                    }

                } catch (InsufficientException | IOException e) {
                    log.error(e.getMessage() + ". Tried to withdraw: " + amount + ". Running on thread " + Thread.currentThread().getName());
                }
                this.sleep(500);
            }catch (Exception e) {
                log.error(Thread.currentThread().getName()+ " error :"+ e.getMessage());
                exit();
            }
        }
        log.info(Thread.currentThread().getName()+ " stopped.");
        hasStopped=true;

    }

    public synchronized boolean isHasStopped(){
        return hasStopped;
    }

    public synchronized void exit(){
        exit = true;
    }
}