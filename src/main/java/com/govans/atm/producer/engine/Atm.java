package com.govans.atm.producer.engine;

import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
@Component
public class Atm {
    private int id;
    private String locationName;
    private String country;
    private String lat;
    private String lng;


}
