package com.govans.atm.producer.repository;

import com.govans.atm.producer.entity.AtmTransaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageProducerRepository extends CrudRepository<AtmTransaction, Integer> {
}
