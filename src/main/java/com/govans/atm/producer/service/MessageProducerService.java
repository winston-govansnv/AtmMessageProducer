package com.govans.atm.producer.service;

import com.govans.atm.producer.entity.AtmTransaction;
import com.govans.atm.producer.repository.MessageProducerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;

@Service
@Slf4j
public class MessageProducerService implements IMessageProducerService {

    @Autowired
    private MessageProducerRepository messageProducerRepository;

    public void processMessageEvent(AtmTransaction atmTransaction) throws IOException {
        save(atmTransaction);
    }

    @Transactional
    private void save(AtmTransaction atmTransaction) {
        atmTransaction= messageProducerRepository.save(atmTransaction);
        log.info("Successfully Persisted the Event {} ", atmTransaction);
    }
}
