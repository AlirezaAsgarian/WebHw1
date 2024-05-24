package ir.aa.weatherprediction.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CountryController {

    private static final int pageSize = 10;

    private static final String countriesApiUri = "https://countriesnow.space/api/v0.1/countries";
    private static final String apiNinjasUri = "https://api.api-ninjas.com/v1";

    private static final String ninjasApiKey = "0yvb2E7SNK40wA0iseRj6Q==D0qe6dl6L9zszLh3";

    private static final RestClient restClient = RestClient.create();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Cacheable("countries")
    @GetMapping("/countries/all")
    ObjectNode getAllCountries() {
        List<String> countriesList = fetchAllCountriesList();
        return countryListToJson(countriesList);
    }

    @GetMapping("/countries")
    ObjectNode getCountriesPage(@Param("page") Integer page, HttpServletRequest request) {
        if (page != null && page <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid page number %s".formatted(page));
        }
        JsonNode jsonResponse = restClient.get()
                .uri("http://%s:%s/countries/all".formatted(request.getServerName(), request.getServerPort()))
                .retrieve().body(JsonNode.class);
        if (page == null) {
            return (ObjectNode) jsonResponse;
        }
        List<String> allCountriesList = new ArrayList<>();
        jsonResponse.get("countries").forEach(country -> allCountriesList.add(country.get("name").textValue()));
        int pageCount = (allCountriesList.size() + pageSize - 1) / pageSize;
        if (page > pageCount) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This list has %s pages".formatted(pageCount));
        }
        List<String> countriesList = allCountriesList.subList((page - 1) * pageSize, Integer.min(page * pageSize, allCountriesList.size()));
        ObjectNode rootNode = countryListToJson(countriesList);
        String commonUri = "http://%s:%s".formatted(request.getServerName(), request.getServerPort()) + "/countries?page=%s";
        rootNode.put("allCount", allCountriesList.size());
        rootNode.put("lastPage", pageCount);
        ObjectNode linksNode = rootNode.putObject("_links");
        linksNode.putObject("self").put("href", commonUri.formatted(page));
        if (page > 1) {
            linksNode.putObject("prev").put("href", commonUri.formatted(page - 1));
        }
        if (page < pageCount) {
            linksNode.putObject("next").put("href", commonUri.formatted(page + 1));
        }
        return rootNode;
    }

    List<String> fetchAllCountriesList() {
        JsonNode jsonResponse = restClient.get()
                .uri(countriesApiUri)
                .retrieve().body(JsonNode.class);
        List<String> countries = new ArrayList<>();
        jsonResponse.get("data").forEach(country -> countries.add(country.get("country").textValue()));
        return countries;
    }

    ObjectNode countryListToJson(List<String> countryList) {
        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode countriesNode = rootNode.putArray("countries");
        for (String country : countryList) {
            countriesNode.addObject().put("name", country);
        }
        rootNode.put("count", countryList.size());
        return rootNode;
    }

    @Cacheable("country")
    @GetMapping("/countries/{name}")
    ObjectNode getCountryInfo(@PathVariable("name") String countryName) {
        JsonNode jsonResponse = restClient.get()
                .uri("%s/country?name=%s".formatted(apiNinjasUri, countryName))
                .header("X-Api-Key", ninjasApiKey)
                .retrieve().body(JsonNode.class);
        if (jsonResponse.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such country name");
        }
        JsonNode countryInfo = jsonResponse.get(0);
        ObjectNode resultObjectNode = objectMapper.createObjectNode();
        List.of("name", "capital", "iso2", "population", "pop_growth", "currency")
                .forEach(x -> resultObjectNode.put(x, countryInfo.get(x)));
        return resultObjectNode;
    }

    @Cacheable("weather")
    @GetMapping("/countries/{name}/weather")
    ObjectNode getCapitalWeatherInfo(@PathVariable("name") String countryName) {
        JsonNode jsonResponseOfCountry = restClient.get()
                .uri("%s/country?name=%s".formatted(apiNinjasUri, countryName))
                .header("X-Api-Key", ninjasApiKey)
                .retrieve().body(JsonNode.class);
        if (jsonResponseOfCountry.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such country name");
        }
        String capital = jsonResponseOfCountry.get(0).get("capital").textValue();
        JsonNode jsonResponseOfWeather = restClient.get()
                .uri("%s/weather?city=%s".formatted(apiNinjasUri, capital))
                .header("X-Api-Key", ninjasApiKey)
                .retrieve().body(JsonNode.class);
        ObjectNode resultObjectNode = objectMapper.createObjectNode();
        resultObjectNode.put("country_name", countryName);
        resultObjectNode.put("capital", capital);
        List.of("wind_speed", "wind_degrees", "temp", "humidity")
                .forEach(x -> resultObjectNode.put(x, jsonResponseOfWeather.get(x)));
        return resultObjectNode;
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