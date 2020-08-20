package com.udacity.pricing.controller;

import com.udacity.pricing.domain.price.Price;
import com.udacity.pricing.exceptions.PriceException;
import com.udacity.pricing.service.PricingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

/**
 * Implements a REST-based controller for the pricing service.
 */
@RestController
@RequestMapping("/services/price")
public class PricingController {
    private PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    /**
     * Gets the price for a requested vehicle.
     *
     * @param vehicleId ID number of the vehicle for which the price is requested
     * @return price of the vehicle, or error that it was not found.
     */
    @GetMapping
    public Price get(@RequestParam(name = "vehicleId") Long vehicleId) {
        try {
            return pricingService.getPrice(vehicleId);
        } catch (PriceException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Price Not Found", ex);
        }

    }

    /**
     * Gets the price for a requested list of vehicles.
     *
     * @param vehicleList Set of Id numbers of the vehicle for which the price is requested
     * @return price of the vehicle, or error that it was not found.
     */
    @GetMapping("/getPrices")
    public Set<Price> getList(@RequestParam(name = "vehicleList") Set<Long> vehicleList) {
        return pricingService.getPriceList(vehicleList);
    }

    /**
     * Sets the price for a requested vehicle.
     *
     * @param price object
     * @return price of the vehicle, or error that it was not found.
     */
    @PostMapping
    public Price setPrice(@RequestBody Price price) {
        return pricingService.setPrice(price);
    }

    /**
     * Sets the price for a requested vehicle.
     * @param vehicleId ID number of the vehicle for which the price is requested
     * @return price of the vehicle, or error that it was not found.
     */
    @GetMapping("/getNewPrice")
    public Price getNewPrice(@RequestParam(name = "vehicleId") Long vehicleId,
                             @RequestParam(name = "currency")String currency,
                             @RequestParam(name = "amount")Optional<BigDecimal> amount) {
        return pricingService.setNewPrice(vehicleId, currency, amount);
    }

    /**
     * Sets the price for a requested vehicle.
     *
     * @param vehicleId ID number of the vehicle for which the price is to be deleted
     * @return price of the vehicle, or error that it was not found.
     */
    @DeleteMapping
    public String delete(@RequestParam(name = "vehicleId") Long vehicleId) {
        return pricingService.delete(vehicleId);
    }
}
