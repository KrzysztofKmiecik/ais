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
                "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjBCM0I1NEUyRkQ5OUZCQkY5NzVERDMxNDBDREQ4OEI1QzA5RkFDRjMiLCJ0eXAiOiJhdCtqd3QiLCJ4NXQiOiJDenRVNHYyWi03LVhYZE1VRE4ySXRjQ2ZyUE0ifQ.eyJuYmYiOjE2Mzg2MTI4NjQsImV4cCI6MTYzODYxNjQ2NCwiaXNzIjoiaHR0cHM6Ly9pZC5iYXJlbnRzd2F0Y2gubm8iLCJhdWQiOiJhcGkiLCJjbGllbnRfaWQiOiJLcnp5c3p0b2Zfa21pZWNpa0B3cC5wbDprcnp5c3p0b2Zfa21pZWNpa0B3cC5wbCIsInNjb3BlIjpbImFwaSJdfQ.ixubdkz4hadfO5INSqN0-nXjx5SLv6TbqiIiX8T7UVIBQCVMeOTROgJ52SHN7oEw0w8ybdZJ00zU7g3Rj8mfIGUV3C3rNjuSPE-pk1yuydp2Pd8pYa77oJyLeK9VUswgBJa9GXGbnBKmnkLt3_SKs1EmDFqITdsOMUG15Umjs8W29XNBrtjo4F-YfZ5YCg3zJwxJPcIWMzay1-1cuzYuzyo89U4R01KbZxI9-TIcEIufBueLsdMljwbnqJp5QZ0S-o9zkB8WGFMBdPUerRN0UTnLO-PqbHT-vAyraNUr5WvtS2XgOX3wYccelW7Rs5cbEOinfNUnWrn2lVx9NkJhE2FrpAFTPtwZXZT56gYa_E-3zcT1Hig4DP8nQoFXvu17Go4wmr9XhqYdqT3dhAH-toKybTZqN5UWTl-W2RgCZWFTtHrzootcle2GcULND7-9TDYheMDxVt8YGjiTGmlm73LcJ9o5oNZNnoDODHv84QsDcDSF1THdK5JshaaA38WT_7ob2KvO-8V6x7oiGAZi5MvHhRkKzpAoKDNtVKrNE4R9mF5g1-i8dgxbDUfyKoDjgVy99lV5OkrAeTIpOtbBJcm2HnWMJYE3G18iNyVEL2o3Nx59IemAXxfFeOLXmoEt2Ra8iG5ImF7m6UvpV3jhhaMTSyCDwhOuAkddyiORK0c");
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
