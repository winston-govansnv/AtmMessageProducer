package com.govans.atm.producer.engine;

public class InsufficientException extends Exception {
    InsufficientException(String s) {
        super(s);
    }
}
