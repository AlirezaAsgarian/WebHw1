package ir.aa.weatherprediction.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CountryTest {

    @Autowired
    private MockMvc mockMvc;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testCorrectCountry() throws Exception {
        String responseString = mockMvc.perform(get("/countries/Iran"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseString);
        Assertions.assertEquals(
                Set.of("name", "capital", "iso2", "population", "pop_growth", "currency"),
                getFieldNamesOfNode(jsonNode));
        Assertions.assertEquals(
                Set.of("name", "code"),
                getFieldNamesOfNode(jsonNode.get("currency")));
        Assertions.assertEquals(
                "Iran, Islamic Republic Of",
                jsonNode.get("name").asText());
        Assertions.assertEquals(
                "Tehran",
                jsonNode.get("capital").asText());
    }

    @Test
    void testIncorrectCountryNotFound() throws Exception {
        mockMvc.perform(get("/countries/Fake"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCorrectWeather() throws Exception {
        String responseString = mockMvc.perform(get("/countries/Iran/weather"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseString);
        Assertions.assertEquals(
                Set.of("country_name", "capital", "temp", "wind_speed", "wind_degrees", "humidity"),
                getFieldNamesOfNode(jsonNode));
        Assertions.assertEquals(
                "Iran",
                jsonNode.get("country_name").asText());
        Assertions.assertEquals(
                "Tehran",
                jsonNode.get("capital").asText());
    }

    @Test
    void testIncorrectWeatherNotFound() throws Exception {
        mockMvc.perform(get("/countries/Fake/weather"))
                .andExpect(status().isNotFound());
    }

    private Set<String> getFieldNamesOfNode(JsonNode node) {
        Set<String> result = new HashSet<>();
        node.fieldNames().forEachRemaining(result::add);
        return result;
    }
}
