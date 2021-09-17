package com.govans.atm.producer.service;

import com.govans.atm.producer.entity.AtmTransaction;

import java.io.IOException;

public interface IMessageProducerService {
    public void processMessageEvent(AtmTransaction atmTransaction) throws IOException;

}
