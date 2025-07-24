package com.bkb.scanner.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Address {
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
}