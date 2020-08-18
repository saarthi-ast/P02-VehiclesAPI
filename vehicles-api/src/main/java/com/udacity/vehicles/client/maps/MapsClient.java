package com.udacity.vehicles.client.maps;

import com.udacity.vehicles.domain.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Implements a class to interface with the Maps Client for location data.
 */
@Component
public class MapsClient {

    private static final Logger log = LoggerFactory.getLogger(MapsClient.class);

    private final WebClient client;
    private final ModelMapper mapper;

    public MapsClient(WebClient maps,
            ModelMapper mapper) {
        this.client = maps;
        this.mapper = mapper;
    }

    /**
     * Gets an address from the Maps client, given latitude and longitude.
     * @param location An object containing "lat" and "lon" of location
     * @return An updated location including street, city, state and zip,
     *   or an exception message noting the Maps service is down
     */
    public Location getAddress(Location location) {
        try {
            Address address = client
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/maps/")
                            .queryParam("lat", location.getLat())
                            .queryParam("lon", location.getLon())
                            .build()
                    )
                    .retrieve().bodyToMono(Address.class).block();

            mapper.map(Objects.requireNonNull(address), location);

            return location;
        } catch (Exception e) {
            log.warn("Map service is down");
            return location;
        }
    }

    /**
     * Gets all addresses from the Maps client, given a list of comma seperated latitude and longitude.
     * @param locations A List of locations
     * @return A Map that has the updated location including street, city, state and zip,
     *   or an exception message noting the Maps service is down
     */
    public Map<String,Location> getAllAddress(List<Location> locations) {
        Map<String,Location> resultMap = new HashMap<>();
        try {
            List<String> locationList = locations.stream().map(x -> x.getLat() + ":" + x.getLon()).collect(Collectors.toList());
            Map<String, Address> addressMap = client
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/maps/")
                            .queryParam("location", locationList)
                            .build()
                    )
                    .retrieve().bodyToMono(Map.class).block();

            for(Location loc:locations){
                String key = loc.getLat() + ":" + loc.getLon();
                if(addressMap.containsKey(key)){
                    mapper.map(Objects.requireNonNull(addressMap.get(key)), loc);
                    resultMap.putIfAbsent(key,loc);
                }
            }
            return resultMap;
        } catch (Exception e) {
            log.warn("Map service is down");
            return resultMap;
        }
    }
}
