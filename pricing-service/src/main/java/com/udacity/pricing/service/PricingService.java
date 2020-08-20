package com.udacity.pricing.service;

import com.udacity.pricing.domain.price.Price;
import com.udacity.pricing.domain.price.PriceRepository;
import com.udacity.pricing.exceptions.PriceException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * Implements the pricing service to get prices for each vehicle.
 */
@Service
public class PricingService {

    private static final String FAILURE = "Failure";
    private static final String SUCCESS = "Success";
    private PriceRepository priceRepository;

    public PricingService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    /**
     * Holds {ID: Price} pairings (current implementation allows for 20 vehicles)
     */
    private static final Map<Long, Price> PRICES = LongStream
            .range(1, 20)
            .mapToObj(i -> new Price(i, "USD", randomPrice()))
            .collect(Collectors.toMap(Price::getVehicleId, p -> p));

    /**
     * If a valid vehicle ID, gets the price of the vehicle from the stored array.
     *
     * @param vehicleId ID number of the vehicle the price is requested for.
     * @return price of the requested vehicle
     * @throws PriceException vehicleID was not found
     */
    public Price getPrice(Long vehicleId) throws PriceException {
        Optional<Price> price = priceRepository.findById(vehicleId);
        if (price.isEmpty()) {
            throw new PriceException("Cannot find price for Vehicle " + vehicleId);
        }

        return price.get();
    }

    /**
     * Saves price in DB
     *
     * @param price  Object.
     * @return saved Price object
     */
    public Price setPrice(Price price)  {
        return priceRepository.save(price);
    }

    /**
     * Creates new random price
     *
     * @param vehicleId ID number of the vehicle the price is requested for.
     * @return saved Price object
     */
    public Price setNewPrice(Long vehicleId, String currency, Optional<BigDecimal> amount) {
        Price price = new Price(vehicleId, (currency!=null && currency.trim().length()>0)?currency:"USD", (amount.isPresent())?amount.get():randomPrice());
        return priceRepository.save(price);
    }

    /**
     * Gets a random price to fill in for a given vehicle ID.
     *
     * @return random price for a vehicle
     */
    private static BigDecimal randomPrice() {
        return new BigDecimal(ThreadLocalRandom.current().nextDouble(1, 5))
                .multiply(new BigDecimal(5000d)).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Deletes the price entry for given vehicleId
     *
     * @param vehicleId ID number of the vehicle the price is requested for.
     * @return saved Price object
     */
    public String delete(Long vehicleId) {
        try{
            Optional<Price> price = priceRepository.findById(vehicleId);
            if (price.isEmpty()) {
                throw new PriceException("Cannot find price for Vehicle " + vehicleId);
            }else{
                priceRepository.delete(price.get());
            }
        }catch (Exception ex){
            return FAILURE;
        }
        return SUCCESS;

    }

    public Set<Price> getPriceList(Set<Long> vehicleList) {
        return priceRepository.findByVehicleIdIn(vehicleList);
    }
}
