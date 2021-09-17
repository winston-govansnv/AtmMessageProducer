package com.govans.atm.producer.engine.runner;


import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ShutdownThread extends Thread {
    private List<AtmThread> atmThreads;

    public ShutdownThread(List<AtmThread> atmThreads){
        this.atmThreads= atmThreads;
    }

    public void run() {
        log.info("Just before going down..");
        //Now, let's stop the threads
        for (AtmThread thread : atmThreads) {
            log.info(thread.getName() + " is stopping");
            thread.interrupt();
        }

        for (AtmThread thread : atmThreads) {
            while(!thread.isHasStopped()){
                log.info("Waiting....");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for (AtmThread thread : atmThreads) {
            log.info(thread.getName() + " stopped");
        }
    }
}

