package ir.aa.weatherprediction.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class CountryService {

    private static final int pageSize = 10;

    private static final String countriesApiUri = "https://countriesnow.space/api/v0.1/countries";
    private static final String apiNinjasUri = "https://api.api-ninjas.com/v1";

    private static final String ninjasApiKey = "0yvb2E7SNK40wA0iseRj6Q==D0qe6dl6L9zszLh3";

    private static final RestClient restClient = RestClient.create();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ObjectNode getAllCountries() {
        List<String> countriesList = fetchAllCountriesList();
        return countryListToJson(countriesList);
    }

    public ObjectNode getCountriesPage(Integer page, String serverSelfUri) {
        if (page != null && page <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid page number %s".formatted(page));
        }
        JsonNode jsonResponse = restClient.get()
                .uri("%s/countries/all".formatted(serverSelfUri))
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
        rootNode.put("allCount", allCountriesList.size());
        rootNode.put("lastPage", pageCount);
        ObjectNode linksNode = rootNode.putObject("_links");
        String commonUri = serverSelfUri + "/countries?page=%s";
        linksNode.putObject("self").put("href", commonUri.formatted(page));
        if (page > 1) {
            linksNode.putObject("prev").put("href", commonUri.formatted(page - 1));
        }
        if (page < pageCount) {
            linksNode.putObject("next").put("href", commonUri.formatted(page + 1));
        }
        return rootNode;
    }

    public ObjectNode getCountryInfo(String countryName) {
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

    public ObjectNode getCapitalWeatherInfo(String countryName, String serverSelfUri) {
        JsonNode jsonResponseOfCountry = restClient.get()
                .uri("%s/countries/%s".formatted(serverSelfUri, countryName))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such country name");
                }).body(JsonNode.class);
        String capital = jsonResponseOfCountry.get("capital").textValue();
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
}
