package com.govans.atm.producer.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
@ConfigurationProperties("app")
@Getter @Setter
public class AppProperties {
    private String apiurl;
    private int executioncount;
}
