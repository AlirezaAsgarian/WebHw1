package ir.aa.weatherprediction.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.List;

@RestController
public class CountryController {

    private static final String countriesApiUri = "https://countriesnow.space/api/v0.1/countries";
    private static final String apiNinjasUri = "https://api.api-ninjas.com/v1";

    private static String ninjasApiKey = "0yvb2E7SNK40wA0iseRj6Q==D0qe6dl6L9zszLh3";

    private static final RestClient restClient = RestClient.create();

    @GetMapping("/countries")
    ObjectNode getCountriesList() {
        JsonNode jsonResponse = restClient.get()
                .uri(countriesApiUri)
                .retrieve().body(JsonNode.class);
        ObjectNode resultObjectNode = new ObjectMapper().createObjectNode();
        ArrayNode countries = resultObjectNode.putArray("countries");
        int count = 0;
        for (JsonNode country : jsonResponse.get("data")) {
            count++;
            countries.addObject().put("name", country.get("country").textValue());
        }
        resultObjectNode.put("count", count);
        return resultObjectNode;
    }

    @GetMapping("/countries/{name}")
    ObjectNode getCountryInfo(@PathVariable("name") String countryName) {
        JsonNode jsonResponse = restClient.get()
                .uri("%s/country?name=%s".formatted(apiNinjasUri, countryName))
                .header("X-Api-Key", ninjasApiKey)
                .retrieve().body(JsonNode.class);
        JsonNode countryInfo = jsonResponse.get(0);
        ObjectNode resultObjectNode = new ObjectMapper().createObjectNode();
        List.of("name", "capital", "iso2", "population", "pop_growth", "currency")
                .forEach(x -> resultObjectNode.put(x, countryInfo.get(x)));
        return resultObjectNode;
    }

    @GetMapping("/countries/{name}/weather")
    ObjectNode getCapitalWeatherInfo(@PathVariable("name") String countryName) {
        JsonNode jsonResponseOfCountry = restClient.get()
                .uri("%s/country?name=%s".formatted(apiNinjasUri, countryName))
                .header("X-Api-Key", ninjasApiKey)
                .retrieve().body(JsonNode.class);
        String capital = jsonResponseOfCountry.get(0).get("capital").textValue();
        JsonNode jsonResponseOfWeather = restClient.get()
                .uri("%s/weather?city=%s".formatted(apiNinjasUri, capital))
                .header("X-Api-Key", ninjasApiKey)
                .retrieve().body(JsonNode.class);
        ObjectNode resultObjectNode = new ObjectMapper().createObjectNode();
        resultObjectNode.put("country_name", countryName);
        resultObjectNode.put("capital", capital);
        List.of("wind_speed", "wind_degrees", "temp", "humidity")
                .forEach(x -> resultObjectNode.put(x, jsonResponseOfWeather.get(x)));
        return resultObjectNode;
    }

}