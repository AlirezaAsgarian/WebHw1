package ir.aa.weatherprediction.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CountryController {

    @Autowired
    private CountryService service;

    @Cacheable("countries")
    @GetMapping("/countries/all")
    ObjectNode getAllCountries() {
        return service.getAllCountries();
    }

    @GetMapping("/countries")
    ObjectNode getCountriesPage(@Param("page") Integer page, HttpServletRequest request) {
        String serverSelfUri = "http://%s:%s".formatted(request.getServerName(), request.getServerPort());
        return service.getCountriesPage(page, serverSelfUri);
    }

    @Cacheable("country")
    @GetMapping("/countries/{name}")
    ObjectNode getCountryInfo(@PathVariable("name") String countryName) {
        return service.getCountryInfo(countryName);
    }

    @Cacheable("weather")
    @GetMapping("/countries/{name}/weather")
    ObjectNode getCapitalWeatherInfo(@PathVariable("name") String countryName, HttpServletRequest request) {
        String serverSelfUri = "http://%s:%s".formatted(request.getServerName(), request.getServerPort());
        return service.getCapitalWeatherInfo(countryName, serverSelfUri);
    }

    @Caching(evict = {
            @CacheEvict(value = "countries", allEntries = true),
            @CacheEvict(value = "country", allEntries = true),
            @CacheEvict(value = "weather", allEntries = true)
    })
    @Scheduled(fixedRateString = "${caching.spring.ttl}")
    public void clearAllCaches() {
        System.out.println("Cache cleared");
    }

}