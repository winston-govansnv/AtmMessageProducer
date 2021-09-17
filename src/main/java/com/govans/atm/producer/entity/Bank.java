package com.govans.atm.producer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import javax.persistence.*;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "Bank")
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String bankName;
    private String address1;
    private String address2;
    private String country;
    @OneToMany(mappedBy = "bank", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Set<BankClient> clients;



    public Bank(int id, String bankName, String address1, String address2, String country) {
        this.id = id;
        this.bankName = bankName;
        this.address1 = address1;
        this.address2 = address2;
        this.country = country;
    }
}
