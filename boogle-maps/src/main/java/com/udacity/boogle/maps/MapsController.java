package com.udacity.boogle.maps;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/maps")
public class MapsController {

    @GetMapping
    public Address get(@RequestParam Double lat, @RequestParam Double lon) {
        return MockAddressRepository.getRandom();
    }

    @GetMapping
    public Map<String,Address> getAll(@RequestParam List<String> locationList) {
        Map<String,Address> resultMap = new HashMap<>();
        for(String location:locationList){
            resultMap.putIfAbsent(location,MockAddressRepository.getRandom());
        }
        return resultMap;
    }
}
