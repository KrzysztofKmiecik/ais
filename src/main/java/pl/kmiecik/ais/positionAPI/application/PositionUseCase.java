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
                        weatherService.getVisibility(track.getGeometry().getCoordinates().get(0), track.getGeometry().getCoordinates().get(1)).intValue()
                )
        ).collect(Collectors.toList());
        return collect;
    }
    private ResponseEntity<Track[]> getPositionsFromAPI() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization",
                "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjBCM0I1NEUyRkQ5OUZCQkY5NzVERDMxNDBDREQ4OEI1QzA5RkFDRjMiLCJ0eXAiOiJhdCtqd3QiLCJ4NXQiOiJDenRVNHYyWi03LVhYZE1VRE4ySXRjQ2ZyUE0ifQ.eyJuYmYiOjE2Mzg2MjE1MTksImV4cCI6MTYzODYyNTExOSwiaXNzIjoiaHR0cHM6Ly9pZC5iYXJlbnRzd2F0Y2gubm8iLCJhdWQiOiJhcGkiLCJjbGllbnRfaWQiOiJLcnp5c3p0b2Zfa21pZWNpa0B3cC5wbDprcnp5c3p0b2Zfa21pZWNpa0B3cC5wbCIsInNjb3BlIjpbImFwaSJdfQ.iR1UlEwXP3d5ziyI-rbAkR6_yGg8Pfv8AK-W6Rb8aC76Gtf2EqVnsMARKO1oHn7_ILswPUPYXdeujO_RKVpu7OD_hEjNixFIgaocwandKSc_ba_mQbqhGVQq5bZ76kS8W9-tX4QdBCmsF9pWsGpmOSRG94kcygxcOHOR5STwwAmz-eq7HQyGiYRJXPJgW4SaP60k_Z6Lszdo1cDHNOQIv8d_KQ5h5KDjkd8RThjHBrlTKEFI9c99hR4YxKGfiLUGjQX_FFUqnwX6PcpJs14jGCPXIGeQcl4CXZJzeJS9_9_1Np8z9HOYDyr0WI_GQ1Sx_X78vN3IbsroMHkdrTP43WNgQFz9las8bzoD1vwS9gyzlAljSlnJdvaSLEENoSOPaFZS78mk2r0ID2EReGkZ579VXN9fvdrrbo9rhz9aG8q1mnVy_5ah9IXNXL9FDDeCQ5I-WJynReRAUGJwRweEJCwR8X8ZWaMpWm9z3PRewKmbVzUU5beQVfKZF5m94jXbvrDOp1wn1owU3sDsaT4EI6l5nmd-LiqLW9cTCIWczffcU4sCT2sJhfGTYZTxPf0fmd2tsA8adm-qiHu4_AqIr10ALtMSsZTYA9S4q8Cp4zCrZA0GifyshPr0PRogwm0bpWcVNB0IUnVUV7XQ8LHv84BYDMxsDC4UZmz6Y_wcSLs");
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
