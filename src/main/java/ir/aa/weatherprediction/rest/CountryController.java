package ir.aa.weatherprediction.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/countries")
public class CountryController {

    private static final String countriesApiUri = "https://countriesnow.space/api/v0.1/countries";

    @GetMapping("")
    ObjectNode getCountries() {
        RestClient restClient = RestClient.create();
        JsonNode jsonResponse = restClient.get().uri(countriesApiUri).retrieve().body(JsonNode.class);
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

}