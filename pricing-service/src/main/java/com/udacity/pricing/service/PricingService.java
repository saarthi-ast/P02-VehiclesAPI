package com.udacity.pricing.service;

import com.udacity.pricing.domain.price.Price;
import com.udacity.pricing.domain.price.PriceRepository;
import com.udacity.pricing.exceptions.PriceException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implements the pricing service to get prices for each vehicle.
 */
@Service
public class PricingService {

    private PriceRepository priceRepository;

    public PricingService(PriceRepository priceRepository){
        this.priceRepository = priceRepository;
    }

    /**
     * If a valid vehicle ID, gets the price of the vehicle from the inmemory DB.
     * @param vehicleId ID number of the vehicle the price is requested for.
     * @return price of the requested vehicle
     * @throws PriceException vehicleID was not found
     */
    public Price getPrice(Long vehicleId) throws PriceException {
        Optional<Price> price = priceRepository.findById(vehicleId);
        if (!price.isPresent()) {
            throw new PriceException("Cannot find price for Vehicle " + vehicleId);
        }

        return price.get();
    }

}
