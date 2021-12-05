package pl.kmiecik.ais.weatherAPI.application;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.kmiecik.ais.Config.CustomProperties;
import pl.kmiecik.ais.weatherAPI.domain.Example;

import java.util.Optional;
import java.util.stream.Stream;

@Service
@EnableConfigurationProperties(CustomProperties.class)
public class WeatherService {


    @Autowired
    private final CustomProperties customProperties;

    public WeatherService(CustomProperties customProperties) {
        this.customProperties = customProperties;
    }

    RestTemplate restTemplate = new RestTemplate();
    private final static String WEATHER_URL = "https://api.stormglass.io/v2/weather/point?";



    public Double getVisibility(final Double lat, final Double lon) {
        Double visibility;
        if (false) {
            visibility = null;
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization",
                    customProperties.getWeatherapikey());
            HttpEntity httpEntity = new HttpEntity(httpHeaders);

            ResponseEntity<Example> exchange = restTemplate.exchange(WEATHER_URL + "?lat=" + lat + "&lng=" + lon + "&params=visibility",
                    HttpMethod.GET,
                    httpEntity,
                    Example.class);
            visibility = exchange.getBody().getHours().get(0).getVisibility().getNoaa();
        } else {
            double min = 0.01;
            double max = 50.03;
            visibility = min + Math.random() * (max - min + 1);
        }
        return visibility;
    }


    private Optional<Double> getVisibilityFromRestApi(final String url, final Double lat, final Double lon) {
        Optional<JsonNode> jsonNode = Stream.of(restTemplate.getForObject(
                url.concat("lat=").concat(String.valueOf(lat)).concat("&lng=").concat(String.valueOf(lon)).concat("&params=visibility"), JsonNode.class)).findFirst();

        return jsonNode.map(node -> {
            double v = node.get("hours").get(0).get("visibility").get("noaa").asDouble();
            return v;
        });
    }
}
