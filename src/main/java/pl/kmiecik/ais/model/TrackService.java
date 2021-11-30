package pl.kmiecik.ais.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TrackService {
    RestTemplate restTemplate = new RestTemplate();

    private final WeatherService weatherService;

    public List<Point> getTracks() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization",
                "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjBCM0I1NEUyRkQ5OUZCQkY5NzVERDMxNDBDREQ4OEI1QzA5RkFDRjMiLCJ0eXAiOiJhdCtqd3QiLCJ4NXQiOiJDenRVNHYyWi03LVhYZE1VRE4ySXRjQ2ZyUE0ifQ.eyJuYmYiOjE2MzgzMTQxMzMsImV4cCI6MTYzODMxNzczMywiaXNzIjoiaHR0cHM6Ly9pZC5iYXJlbnRzd2F0Y2gubm8iLCJhdWQiOiJhcGkiLCJjbGllbnRfaWQiOiJLcnp5c3p0b2Zfa21pZWNpa0B3cC5wbDprcnp5c3p0b2Zfa21pZWNpa0B3cC5wbCIsInNjb3BlIjpbImFwaSJdfQ.Ex6dWf7t-rJM8QuyhABKcwAo_3eGKQGxOgCrtCqQhQwA7s_-haLyTNexoeMdfG5k5gWR9CuR3PS95i-K94-Kv3iRGCkdy7LRNLS-h1C4yEBi8VcNbb7w0FJfeerBPSdsI1DlZo77rbBORKovD8bYK_kPMhaxY2EmSUCiq8cPgI8kCkdFKQpZnGH2A2_bn4z_Fk6OF_N3DS3oRKkr4MheKrjM2DsPuB5r8pw3FqQRIZaZd1tNbDQW-3tYegMDv8NopTaKvCkMMOArRhSrYz-ziRcXrn-jprohayorZLsv8BMbPQFyjoPscRzMKIKmVYB2-hhYKyDbLTEe7JxxegKymN2zkKfVOxV_KmyjDQNCl4O4bTZdbLF8AghiQIcazCc6PNh_dg_2feV1FTzQsGbJfKRXysdFEdhkRj1VmFP7ZPEjXfuKPhjlPoEg7NeqIkkTjyDQDHW2D4EKRkRqDIwTMeFw1wBxTlP38hGcsNy0bo8bt-v4hnDXWh_uaYJ62EMR2Kcop_wvlQbuJP9CA9dqOPFtTMZXZ-2fb22bMFpAOQaLVp2VOzhYoHfSTuG-KaICdTMszbhEDQwyeq2UVF8vvwti2PrrmH5KRXJUDJgDi1YFNoxjRgo4Ge5W6utSLGdwZVN7QJ7mPSSdhe-8-zyk1JDC2Z1jFDMj9yjIOUvIRoI");
        HttpEntity httpEntity = new HttpEntity(httpHeaders);

        ResponseEntity<Track[]> exchange = restTemplate.exchange("https://www.barentswatch.no/bwapi/v2/geodata/ais/openpositions?Xmin=10.09094&Xmax=10.67047&Ymin=63.3989&Ymax=63.58645",
                HttpMethod.GET,
                httpEntity,
                Track[].class);

        List<Point> collect = Stream.of(exchange.getBody()).map(
                track -> new Point(
                        track.getGeometry().getCoordinates().get(0),
                        track.getGeometry().getCoordinates().get(1),
                        track.getName(),
                        getDestination(track.getDestination(), track.getGeometry().getCoordinates()).getLongitude(),
                        getDestination(track.getDestination(), track.getGeometry().getCoordinates()).getLatitude(),
                        weatherService.getVisibility(track.getGeometry().getCoordinates().get(0), track.getGeometry().getCoordinates().get(1)).intValue()
                )
        ).collect(Collectors.toList());
        return collect;
    }

    public Datum getDestination(String destinationName, List<Double> coordinates) {
        try {
            String url = "http://api.positionstack.com/v1/forward?access_key=f9aae45e031a1e66eac64db90ffda427&query=" + destinationName;
            JsonNode data = restTemplate.getForObject(url, JsonNode.class).get("data").get(0);
            double latitude = data.get("latitude").asDouble();
            double longitude = data.get("longitude").asDouble();
            return new Datum(latitude, longitude);

        } catch (Exception ex) {
            return new Datum(coordinates.get(1), coordinates.get(0));
        }
    }
}
