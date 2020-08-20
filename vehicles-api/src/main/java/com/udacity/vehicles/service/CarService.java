package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.domain.manufacturer.ManufacturerRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.udacity.vehicles.constants.ApplicationConstants.*;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {


    private final CarRepository repository;
    private final MapsClient mapsClient;
    private final PriceClient priceClient;
    private final ManufacturerRepository manufacturerRepository;

    public CarService(CarRepository repository, MapsClient mapsClient, PriceClient priceClient, ManufacturerRepository manufacturerRepository) {
        this.repository = repository;
        this.mapsClient = mapsClient;
        this.priceClient = priceClient;
        this.manufacturerRepository = manufacturerRepository;
    }

    /**
     * Gathers a list of all vehicles
     *
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        List<Car> carList = repository.findAll();
        Set<Long> VehicleIds = carList.stream().map(x -> x.getId()).collect(Collectors.toSet());
        return carList;
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     *
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {
        Car result;
        Optional<Car> car = this.repository.findById(id);
        if (car.isPresent()) {
            result = car.get();
        } else {
            throw new CarNotFoundException(CAR_NOT_FOUND);
        }

        String carPrice = priceClient.getPrice(result.getId());
        result.setPrice(carPrice);

        Location address = mapsClient.getAddress(result.getLocation());
        result.setLocation(address);

        return result;
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     *
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        Car carToSave = car;
        if (car.getId() != null && car.getId() > 0) {
            carToSave =  repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(car.getLocation());
                        return carToBeUpdated;
                    }).orElseThrow(CarNotFoundException::new);
        } else {
            Manufacturer manufacturer = car.getDetails().getManufacturer();
            manufacturerRepository.save(manufacturer);
        }
        Car savedCar = repository.save(carToSave);
        if(car.getPrice() != null){
            String carPrice = priceClient.getNewPrice(savedCar.getId(),null,new BigDecimal(car.getPrice()));
            savedCar.setPrice(carPrice);
        }else{
            String carPrice = priceClient.getPrice(savedCar.getId());
            savedCar.setPrice(carPrice);
        }
        Location address = mapsClient.getAddress(savedCar.getLocation());
        savedCar.setLocation(address);
        return savedCar;
    }

    /**
     * Deletes a given car by ID
     *
     * @param id the ID number of the car to delete
     */
    public String delete(Long id) {
        try {
            Car result;
            Optional<Car> car = this.repository.findById(id);
            if (car.isPresent()) {
                result = car.get();
            } else {
                throw new CarNotFoundException(CAR_NOT_FOUND);
            }
            repository.delete(result);
            priceClient.deletePrice(id);
        } catch (CarNotFoundException ex) {
            throw new CarNotFoundException(CAR_NOT_FOUND);
        } catch (Exception ex) {
            return FAILURE;
        }
        return SUCCESS;
    }
}
