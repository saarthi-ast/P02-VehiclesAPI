package com.udacity.vehicles.client.maps;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Declares a class to store an address, city, state and zip code.
 */
@Data
@NoArgsConstructor
public class Address {
    private String address;
    private String city;
    private String state;
    private String zip;
}
