package pl.kmiecik.ais.positionAPI.application;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.kmiecik.ais.Config.CustomProperties;
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
@EnableConfigurationProperties(CustomProperties.class)
class PositionUseCase implements pl.kmiecik.ais.positionAPI.application.port.PositionService {
    RestTemplate restTemplate = new RestTemplate();
    private final WeatherService weatherService;
    private final CustomProperties customProperties;

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
                "Bearer " + customProperties.getPositionapikey());
        HttpEntity httpEntity = new HttpEntity(httpHeaders);


        final String POSITION_URL = "https://www.barentswatch.no/bwapi/v2/geodata/ais/openpositions?Xmin=10.09094&Xmax=10.67047&Ymin=63.3989&Ymax=63.58645";
        ResponseEntity<Track[]> exchange = restTemplate.exchange(POSITION_URL,
                HttpMethod.GET,
                httpEntity,
                Track[].class);
        return exchange;
    }

    @Override
    public Datum getDestination(String destinationName, List<Double> coordinates) {
        try {

            final String DESTINATION_URL = "http://api.positionstack.com/v1/forward?access_key=f9aae45e031a1e66eac64db90ffda427&query=";
            String url = DESTINATION_URL + destinationName;
            JsonNode data = restTemplate.getForObject(url, JsonNode.class).get("data").get(0);
            double latitude = data.get("latitude").asDouble();
            double longitude = data.get("longitude").asDouble();
            return new Datum(latitude, longitude);

        } catch (Exception ex) {
            return new Datum(coordinates.get(1), coordinates.get(0));
        }
    }
}
