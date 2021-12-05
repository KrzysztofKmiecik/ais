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
                "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjBCM0I1NEUyRkQ5OUZCQkY5NzVERDMxNDBDREQ4OEI1QzA5RkFDRjMiLCJ0eXAiOiJhdCtqd3QiLCJ4NXQiOiJDenRVNHYyWi03LVhYZE1VRE4ySXRjQ2ZyUE0ifQ.eyJuYmYiOjE2Mzg3MjY4NjEsImV4cCI6MTYzODczMDQ2MSwiaXNzIjoiaHR0cHM6Ly9pZC5iYXJlbnRzd2F0Y2gubm8iLCJhdWQiOiJhcGkiLCJjbGllbnRfaWQiOiJLcnp5c3p0b2Zfa21pZWNpa0B3cC5wbDprcnp5c3p0b2Zfa21pZWNpa0B3cC5wbCIsInNjb3BlIjpbImFwaSJdfQ.teZhqccYxaKJbVhBuRqCP_cJGxUnEr2HsIfR4HEr5MwTQ9O9soT8axjuuD4c5dSx-wOqbUroCmUzqY9_-HpPScPCpSnxTF41cQ41Kc3qjJXwbRxH0w53zOYcemik9WtdWaDOv5bk1QhOIAyiLjZ2FJo_mb8tKiV2rlEbr_b89xQEmF1hQG7vxdjeqEJj0zzcxfHkjIWsjAQrNkn5QDMTwLO2CqGU-GkzyH07--Xju6o8OjhQtMth3Z6UL5h4n0Ky6iZCot88a1xvjKGeCRBb1th4FUnrMCS081FEVJ3E4TwtVC1-dkenllqHjsxyicOi8lmGioax7rPFw5FxwkeFM-aNAP26GMsVh2wMq4ujkByCWQgH_SOg9C3-wMPy1esc9KonkwBFyvczTDV0838AObGwUI--3MwuC8QhJlY2bCXjk8DWmewqA8C7C5RhYjHuw0f6BFifAOSsdR8G8dcp3WFwuEOFRLjz0OUzsZdpKYVeEODcfuUfYMjNoKcAZlyIko552DLzyA7m8TVNc4TN9YYBK9ouQ5yk6fjVKcktvR5cusaoppeGNlKkdd1dg8EamObZmtQoe8Eg0XDVPYW1GP8sEipKIzjl0a12ylVyZQgyymWWS3KVmKBtJFLPv-_jXDGpyG_6IyO1MS734kRFvd8JzqyYo-R39eEopD3_4DE");
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
