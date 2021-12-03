package pl.kmiecik.ais.ship.application;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.kmiecik.ais.positionAPI.domain.Datum;
import pl.kmiecik.ais.ship.application.port.ShipService;
import pl.kmiecik.ais.ship.domain.Ship;
import pl.kmiecik.ais.positionAPI.domain.Track;
import pl.kmiecik.ais.ship.domain.ShipStatus;
import pl.kmiecik.ais.weatherAPI.application.WeatherService;
import pl.kmiecik.ais.ship.domain.ShipEntity;
import pl.kmiecik.ais.ship.domain.ShipRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ShipServiceUseCase implements ShipService {
    RestTemplate restTemplate = new RestTemplate();

    private final WeatherService weatherService;
    private final ShipRepository shipRepository;

    @Override
    public List<Ship> getShips() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization",
                "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjBCM0I1NEUyRkQ5OUZCQkY5NzVERDMxNDBDREQ4OEI1QzA5RkFDRjMiLCJ0eXAiOiJhdCtqd3QiLCJ4NXQiOiJDenRVNHYyWi03LVhYZE1VRE4ySXRjQ2ZyUE0ifQ.eyJuYmYiOjE2Mzg1MzY5MTYsImV4cCI6MTYzODU0MDUxNiwiaXNzIjoiaHR0cHM6Ly9pZC5iYXJlbnRzd2F0Y2gubm8iLCJhdWQiOiJhcGkiLCJjbGllbnRfaWQiOiJLcnp5c3p0b2Zfa21pZWNpa0B3cC5wbDprcnp5c3p0b2Zfa21pZWNpa0B3cC5wbCIsInNjb3BlIjpbImFwaSJdfQ.jD7hdAsAVG2TdMModSN8sFSUvIlu2CKZW6sehiyvn7Uvc7gEmmzpyp-y6Zt8CYVE3CSTrhvhbhSXOFtnEnUEZQEpWR7Z4iHtJ1Ja1fq4Y-sQpwaYycxduIGHNfysl_-dUsa59oDgkgu9JzY8I89Mngu3FJUrtp9FixIFUL1nXMb9UWfWbEYOnbvyuS2JhqriCV6G0Ja7thYAR_5UdaLmG3GPx9My6-aruxeMcxJosWMri9OcsW-zHScpLxaL3MsFWCZsNOYNzNwZJBnZud9_OZn6-zs5ii7CWM2xqERHQzDY-ghe2E3Tz-my_40W7vBIG2Jb31-GbXrjHMNDxvZOEmUubflmUfv43yuUJNUrX2xLpgB3YMuQfIaTvlxRCamBv2wQocz7_0T_fDPcAcp5cSK6LGv9imNigpY03TXN5tauIUQ8iAtSuaxLX65-ap6GxCdPg5OX3UmWo7OKgL8WaySTjVlOeq-JZCLUt3eWnWp3xpw7eJf6JMYPm_mj6JiQt-Kkik5STkxNB2wup7016jOKKfZe-1BBBNTcYcM4NOWgRzMj6lNcu7knscns2uTJqWynAvuFggYH5sf5WUPEEFlJagmdvZtlf0pwMixEl4HZ5Z2DSy09LPuj1LtJeLsktCyYjZvsurt_n2abelo8fS99sm8a6DL3Wjk0S0nroxM");
        HttpEntity httpEntity = new HttpEntity(httpHeaders);

        ResponseEntity<Track[]> exchange = restTemplate.exchange("https://www.barentswatch.no/bwapi/v2/geodata/ais/openpositions?Xmin=10.09094&Xmax=10.67047&Ymin=63.3989&Ymax=63.58645",
                HttpMethod.GET,
                httpEntity,
                Track[].class);

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

    @Override
    public void saveShip(Ship ship) {
        ShipEntity shipEntity = mapToEntity(ship);

        shipRepository.save(shipEntity);
    }

    @Override
    public void saveShips(List<Ship> ships) {
        ships.forEach(ship -> saveShip(ship));
    }
}
