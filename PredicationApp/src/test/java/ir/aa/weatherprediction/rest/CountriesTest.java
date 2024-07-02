package ir.aa.weatherprediction.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CountriesTest {

    @TestConfiguration
    static class Maw {
        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
            return (web) -> web.ignoring().requestMatchers("/**").anyRequest();
        }
    }

    @LocalServerPort
    private int port;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final CloseableHttpClient httpClient = HttpClientBuilder.create().build();

    @Test
    void testCountriesAll() throws Exception {
        HttpUriRequest request = new HttpGet("http://localhost:%s/countries".formatted(port));
        CloseableHttpResponse response = httpClient.execute(request);
        Assertions.assertEquals(response.getCode(), HttpStatus.OK.value());
        String responseString = EntityUtils.toString(response.getEntity());
        JsonNode jsonNode = objectMapper.readTree(responseString);
        Assertions.assertEquals(
                Set.of("countries", "count"),
                getFieldNamesOfNode(jsonNode));
        int count = 0;
        for (JsonNode country : jsonNode.get("countries")) {
            count++;
            Assertions.assertEquals(Set.of("name"), getFieldNamesOfNode(country));
        }
        Assertions.assertEquals(count, jsonNode.get("count").asInt());
    }

    @Test
    void testCountriesFirstPage() throws Exception {
        HttpUriRequest request = new HttpGet("http://localhost:%s/countries?page=1".formatted(port));
        CloseableHttpResponse response = httpClient.execute(request);
        Assertions.assertEquals(response.getCode(), HttpStatus.OK.value());
        String responseString = EntityUtils.toString(response.getEntity());
        JsonNode jsonNode = objectMapper.readTree(responseString);
        Assertions.assertEquals(
                Set.of("countries", "count", "allCount", "lastPage", "_links"),
                getFieldNamesOfNode(jsonNode));
        Assertions.assertEquals(
                Set.of("self", "next"),
                getFieldNamesOfNode(jsonNode.get("_links")));
    }

    @Test
    void testCorrectCountry() throws Exception {
        HttpUriRequest request = new HttpGet("http://localhost:%s/countries/Iran".formatted(port));
        CloseableHttpResponse response = httpClient.execute(request);
        Assertions.assertEquals(response.getCode(), HttpStatus.OK.value());
        String responseString = EntityUtils.toString(response.getEntity());
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
        HttpUriRequest request = new HttpGet("http://localhost:%s/countries/Fake".formatted(port));
        CloseableHttpResponse response = httpClient.execute(request);
        Assertions.assertEquals(response.getCode(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testCorrectWeather() throws Exception {
        HttpUriRequest request = new HttpGet("http://localhost:%s/countries/Iran/weather".formatted(port));
        CloseableHttpResponse response = httpClient.execute(request);
        Assertions.assertEquals(response.getCode(), HttpStatus.OK.value());
        String responseString = EntityUtils.toString(response.getEntity());
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
        HttpUriRequest request = new HttpGet("http://localhost:%s/countries/Fake/weather".formatted(port));
        CloseableHttpResponse response = httpClient.execute(request);
        Assertions.assertEquals(response.getCode(), HttpStatus.NOT_FOUND.value());
    }

    private Set<String> getFieldNamesOfNode(JsonNode node) {
        Set<String> result = new HashSet<>();
        node.fieldNames().forEachRemaining(result::add);
        return result;
    }
}
