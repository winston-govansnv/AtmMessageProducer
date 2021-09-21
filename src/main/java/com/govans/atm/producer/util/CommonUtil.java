package com.govans.atm.producer.util;

import lombok.extern.slf4j.Slf4j;

import static java.lang.Thread.sleep;

@Slf4j
public class CommonUtil {


    public static void delay(long delayMilliSeconds)  {
        try{
            sleep(delayMilliSeconds);
        }catch (Exception e){
            log.error("Exception is :" + e.getMessage());
        }

    }

}
