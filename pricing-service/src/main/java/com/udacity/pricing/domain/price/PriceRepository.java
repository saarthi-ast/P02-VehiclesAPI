package com.udacity.pricing.domain.price;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PriceRepository extends CrudRepository<Price,Long> {
    Set<Price> findByVehicleIdIn(Set<Long> vehicleIds);
}
