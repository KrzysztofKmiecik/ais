package pl.kmiecik.ais.positionAPI.application;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.kmiecik.ais.positionAPI.domain.Datum;
import pl.kmiecik.ais.positionAPI.domain.Track;
import pl.kmiecik.ais.ship.domain.PositionCoordinate;
import pl.kmiecik.ais.ship.domain.Ship;
import pl.kmiecik.ais.ship.domain.ShipStatus;
import pl.kmiecik.ais.weatherAPI.application.WeatherService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
class PositionUseCase implements pl.kmiecik.ais.positionAPI.application.port.PositionService {
    RestTemplate restTemplate = new RestTemplate();
    private final WeatherService weatherService;

    @Override
    public List<Ship> getPositions() {

        ResponseEntity<Track[]> exchange = getPositionsFromAPI();

        List<Ship> collect = Stream.of(exchange.getBody()).map(
                track -> new Ship(
                        track.getGeometry().getCoordinates().get(0),
                        track.getGeometry().getCoordinates().get(1),
                        track.getName(),
                        ShipStatus.ENEMY,
                        getDestination(track.getDestination(), track.getGeometry().getCoordinates()).getLongitude(),
                        getDestination(track.getDestination(), track.getGeometry().getCoordinates()).getLatitude(),
                        weatherService.getVisibility(track.getGeometry().getCoordinates().get(0), track.getGeometry().getCoordinates().get(1)).intValue(),
                        null

                )
        ).collect(Collectors.toList());
        return collect;
    }
    private ResponseEntity<Track[]> getPositionsFromAPI() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization",
                "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjBCM0I1NEUyRkQ5OUZCQkY5NzVERDMxNDBDREQ4OEI1QzA5RkFDRjMiLCJ0eXAiOiJhdCtqd3QiLCJ4NXQiOiJDenRVNHYyWi03LVhYZE1VRE4ySXRjQ2ZyUE0ifQ.eyJuYmYiOjE2Mzg3MDU0NTgsImV4cCI6MTYzODcwOTA1OCwiaXNzIjoiaHR0cHM6Ly9pZC5iYXJlbnRzd2F0Y2gubm8iLCJhdWQiOiJhcGkiLCJjbGllbnRfaWQiOiJLcnp5c3p0b2Zfa21pZWNpa0B3cC5wbDprcnp5c3p0b2Zfa21pZWNpa0B3cC5wbCIsInNjb3BlIjpbImFwaSJdfQ.FrMX9KOFY85gs0CraJr6Ce1pLkd-xX4O_RnAwEqIfn4VFKELWmYyzkVI8hLD869pgxe-9EPACcxeIeyhBLpvUTcNyMfijm40I6Ge9-ywGRbZqcN29AahRf0IOHfiqjBRbqaIFAdnbmbbIdvpzwYRoI-0wtBEuT1CMKv7A3Hp-4voOGAnmqGtayNcDgNSWlqJ_DV126G7vPLOnqTv9f87Fllp8537vJYH6a-SU3PdNrn3qNYXV-sRdp36UsRELEUxQrJL3qjYCWUjNtfExoif1BQRtssqQ9QPWHFvZL8WNVEpR_UT6yAzvVUhPX4L8rPU88--_Wr2r8yNILKq1oDWMGDJZjev3yWwUhbl5N8Y9_Hu-JlCmzTIQ4Cr4L9M_5IpCDdJ5Mosh3m3p8do9eJUwqCJXPS-314hQmbAtv-8q4wlB5diU99mcR2Wwht17IzWs73bta4THjBPTMqE4jlPYLOE3AGDwKa_F6AqJhm1L-alY6XbYg2iFdvu7UYAWlGBRkhWSyMQBIA-mjPmNWxghg18hh-4yjTmVicjBB8ky35469fT4tcATZuQKvKrbf5q0JtnIO8PYLdlXFxseONTy-FT8ot4W0-7Q7IYoarE-ieDqykPo8l8b_2ym6eAcnghESzqzkzDVnnYZHkCDiscFY07Lx4sxphAWjpxvhzh4g4");
        HttpEntity httpEntity = new HttpEntity(httpHeaders);

        ResponseEntity<Track[]> exchange = restTemplate.exchange("https://www.barentswatch.no/bwapi/v2/geodata/ais/openpositions?Xmin=10.09094&Xmax=10.67047&Ymin=63.3989&Ymax=63.58645",
                HttpMethod.GET,
                httpEntity,
                Track[].class);
        return exchange;
    }

    @Override
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
