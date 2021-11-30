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
                "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjBCM0I1NEUyRkQ5OUZCQkY5NzVERDMxNDBDREQ4OEI1QzA5RkFDRjMiLCJ0eXAiOiJhdCtqd3QiLCJ4NXQiOiJDenRVNHYyWi03LVhYZE1VRE4ySXRjQ2ZyUE0ifQ.eyJuYmYiOjE2MzgyMzUxNjEsImV4cCI6MTYzODIzODc2MSwiaXNzIjoiaHR0cHM6Ly9pZC5iYXJlbnRzd2F0Y2gubm8iLCJhdWQiOiJhcGkiLCJjbGllbnRfaWQiOiJLcnp5c3p0b2Zfa21pZWNpa0B3cC5wbDprcnp5c3p0b2Zfa21pZWNpa0B3cC5wbCIsInNjb3BlIjpbImFwaSJdfQ.JYKRNgp6i106Pb85OeJ84BlGwFcQEqYxTC-NI9zsiuHxm_oNLHbN654KUycw4buSBbpRuzwVV8CExy6xije-Z7MFNxRFH-FCXEjw7HzqlhFNVyURn-A2W5OgeDmIrnPRC_5KnLH6ciTrSk7iLWHJf0cx9rGxBkS84xIH-HG3I-0SO_JqiU1b2BkDSakVIim_fDSdXrpu9eZLA5M_fNGoqLZnHq2INyGiGHn0p_yC1kdJNS6PohmqASY7KWdRb6gSJI7zmkiVrD4aKYNeU0hC3Un88NVu1-lc-kI3VYX-N0I5YTsK4mJDOzO0cNCj0FpxhwvrB5fhwywyBw6RG5qim1KsJJMtyLHIVbk-AWU3s7f1nA6ph7TeVd__ZOtUSplGm3U4472sl25ej7d9f2iCMVd4cB8Zd5dNZwghuFoT2859bZ9LPzCShx2lNiebhyEcxMiIib2CM4ua3VZwWuqYIN1W4DSuBMCmg13nnFtEKCiLaUcUT8RoMHI5q7D0vyuus8cETQ_WSW0KDjxFalxAc02U59VQ8aDmE5rop_gbjTIqm6BFeOo16FL6kkEuazuxjhQbeg1DeXBowGUaFnG_TwqB8JSaUdYc_t5X7yNkbExNQkUbHkAAfcqqjbCm0Qw2UOiXUg8axk0AiklTRDHFhpLMOYg3UWao_W63kvBIZYQ");
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
