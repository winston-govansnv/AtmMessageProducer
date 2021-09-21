package com.govans.atm.producer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.govans.atm.producer.engine.Atm;
import com.govans.atm.producer.engine.BankHandler;
import com.govans.atm.producer.engine.ClientAccount;
import com.govans.atm.producer.engine.InsufficientException;
import com.govans.atm.producer.entity.AtmTransaction;
import com.govans.atm.producer.model.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Random;

@Slf4j
public class AtmService {
    private BankHandler bankHandler;
    private String ApiUrl;
    private MessageProducerService messageProducerService;
    private WebClient.Builder webClientBuilder;
    private ObjectMapper objectMapper;
    private AppProperties appProperties;

    public AtmService(BankHandler bankHandler, AppProperties appProperties, MessageProducerService messageProducerService, WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.bankHandler = bankHandler;
        this.appProperties= appProperties;
        this.messageProducerService = messageProducerService;
        this.webClientBuilder = webClientBuilder;
        this.objectMapper = objectMapper;
    }

    public AtmTransaction RegisterTrx(Atm atm, boolean persistData){
        AtmTransaction trx=null;
        try {

            Random randomAc = new Random();
            int accRandInt = randomAc.nextInt(bankHandler.getClientAccounts().size());
            ClientAccount client = bankHandler.getClientAccounts().get(accRandInt);

            //Random amounts to be withdrawn from the account
            Random random = new Random();
            int randomInt = random.nextInt(10);
            if (randomInt == 0)
                randomInt = 1;
            double amount = randomInt * 25;

            try {
                bankHandler.withDraw(accRandInt, amount);
                log.info("Customer #" + client.getAccountId() + " withdraw: " + amount + " from ATM at location:" + atm.getLocationName() + ". New balance is: " + client.getAccountAmount() + ". Running on thread " + Thread.currentThread().getName());
                trx = new AtmTransaction();
                trx.setBankName("MCB Curacao");
                trx.setAtmLocation(atm.getLocationName());
                trx.setCountry(atm.getCountry());
                trx.setLat(atm.getLat());
                trx.setLng(atm.getLng());
                trx.setAmount(amount);
                trx.setAccountNumber(client.getAccountId());

                if(messageProducerService!=null) {
                    if(persistData){
                        String ApiUrl= appProperties.getApiurl();
                        messageProducerService.processMessageEvent(trx);

                        String jsonString = objectMapper.writeValueAsString(trx);
                        log.info("JSON string message :"+ jsonString);

                        // Post message to Kafka microservice (Producer)
                        webclientbuilder.build().post()
                                .uri(apiurl)
                                .header(httpheaders.content_type, mediatype.application_json_value)
                                .body(mono.just(trx), atmtransaction.class)
                                .retrieve()
                                .bodyToMono(AtmTransaction.class).block();
                    }
                }

            } catch (InsufficientException | IOException e) {
                log.error(e.getMessage() + ". Tried to withdraw: " + amount + ". Running on thread " + Thread.currentThread().getName());
            }
        }catch (Exception e) {
            log.error(Thread.currentThread().getName()+ " error :"+ e.getMessage());
        }

        return trx;
    }
}
