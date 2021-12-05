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
                "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjBCM0I1NEUyRkQ5OUZCQkY5NzVERDMxNDBDREQ4OEI1QzA5RkFDRjMiLCJ0eXAiOiJhdCtqd3QiLCJ4NXQiOiJDenRVNHYyWi03LVhYZE1VRE4ySXRjQ2ZyUE0ifQ.eyJuYmYiOjE2Mzg3MjkwNzYsImV4cCI6MTYzODczMjY3NiwiaXNzIjoiaHR0cHM6Ly9pZC5iYXJlbnRzd2F0Y2gubm8iLCJhdWQiOiJhcGkiLCJjbGllbnRfaWQiOiJLcnp5c3p0b2Zfa21pZWNpa0B3cC5wbDprcnp5c3p0b2Zfa21pZWNpa0B3cC5wbCIsInNjb3BlIjpbImFwaSJdfQ.Lnqca7uSVlmQbtxCpwDdQsuF8XTK-p_Tf2S-ZWG19uubWF7e9Nm1p3QypxUGbtMCnBnWjvdGEeU3dYmI5FovaW9xVOIMqt2TLsHGOs_mfb4udyFS6LHkZ2JmegIimsmpLZBrK5DKXiZW0TgpLpSpCi08iBxhtasNf__9RMBhrYlJinAemNXRNHDjUFrI8YG31Z3ojPmyP_hnBG2xdwYCZXg31rUAM4kTXfdxRSIUawXSxOInTNthvg2zREfglZLe_SdJLNLEcu6J7XBP1Q6HClU9LbO94uDPhgWF50WFzue77TcA-k8h9BN_3J1YA-UW2sOv-qnF7igxBjzGMfOeS8F8iJgEIO_Qr_jvep_mXJxHLhgJrX9jKd--H4Rcs4UrpGbxEs6rmYve-kz5LRNOfOK8YvHUadsp6pkknC1RBMVhJ7V0kLGTcfjSBXMDapos7B1K-WXc2kCYbAHqPX80aJdW_Pg8ZEjwKFij38z7U36-2haszwPPjdxwj1odCngxtUlJv0KlgSiJV2PXN1jC91jU7Am4OjYlsEe-2Xw5bsgsIwWFrH8lhYdPtkk86fJsA0dYJxDh2q1ZcfnhVeNicFJGVNm0uQy5UQbyWZFiDFlk2vvu9ylbZofVQjBAOrbmtPjN5jzwyzSipgIcHBEUfKG1_sqisFk3W6gw3TBFhLc");
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
