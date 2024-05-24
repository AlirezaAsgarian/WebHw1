package ir.aa.weatherprediction.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CountriesTest {

    @LocalServerPort
    private int port;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testCountriesAll() throws Exception {
        HttpUriRequest request = new HttpGet("http://localhost:%s/countries".formatted(port));
        CloseableHttpResponse response = HttpClientBuilder.create().build().execute(request);
        Assertions.assertEquals(response.getCode(), 200);
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
        CloseableHttpResponse response = HttpClientBuilder.create().build().execute(request);
        Assertions.assertEquals(response.getCode(), 200);
        String responseString = EntityUtils.toString(response.getEntity());
        JsonNode jsonNode = objectMapper.readTree(responseString);
        Assertions.assertEquals(
                Set.of("countries", "count", "allCount", "lastPage", "_links"),
                getFieldNamesOfNode(jsonNode));
        Assertions.assertEquals(
                Set.of("self", "next"),
                getFieldNamesOfNode(jsonNode.get("_links")));
    }

    private Set<String> getFieldNamesOfNode(JsonNode node) {
        Set<String> result = new HashSet<>();
        node.fieldNames().forEachRemaining(result::add);
        return result;
    }
}
