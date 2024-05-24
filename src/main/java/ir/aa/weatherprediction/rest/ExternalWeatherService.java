package ir.aa.weatherprediction.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.aa.weatherprediction.rest.domain.WeatherRequest;
import ir.aa.weatherprediction.rest.domain.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


@Component
public class ExternalWeatherService {

    private static final String countriesApiUri = "https://countriesnow.space/api/v0.1/countries";
    private static final String apiNinjasUri = "https://api.api-ninjas.com/v1";
    private static final String ninjasApiKey = "0yvb2E7SNK40wA0iseRj6Q==D0qe6dl6L9zszLh3";
    private static final RestClient restClient = RestClient.create();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final BlockingQueue<WeatherRequest> weatherRequests;
    private final BlockingQueue<WeatherResponse> weatherResponses;

    @Autowired
    public ExternalWeatherService(
            @Qualifier("getWeatherRequestQueue") ArrayBlockingQueue<WeatherRequest> weatherRequests,
            @Qualifier("getWeatherResponseQueue") ArrayBlockingQueue<WeatherResponse> weatherResponses) {
        this.weatherRequests = weatherRequests;
        this.weatherResponses = weatherResponses;
        new Thread(this::run).start();
    }

    public void run() {
        try {
            while (true) {
                WeatherRequest weatherRequest = weatherRequests.poll();
                if (weatherRequest == null) {
                    Thread.sleep(5_000);
                    continue;
                }
                JsonNode jsonResponse = restClient.get()
                        .uri("%s/country?name=%s".formatted(apiNinjasUri, weatherRequest.getCountryName()))
                        .header("X-Api-Key", ninjasApiKey)
                        .retrieve().body(JsonNode.class);
                weatherResponses.put(new WeatherResponse(weatherRequest.getId(), jsonResponse));
            }
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }


}
