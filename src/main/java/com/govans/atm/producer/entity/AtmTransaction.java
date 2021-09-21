package com.govans.atm.producer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "AtmTransaction")
public class AtmTransaction {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Integer id;
    private String bankName;
    private String atmLocation;
    private String country;
    private double amount;
    private String accountNumber;
    @CreationTimestamp
    private Date datetimeAdded;
    private String lat;
    private String lng;

}
