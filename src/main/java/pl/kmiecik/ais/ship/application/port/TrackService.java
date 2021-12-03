package pl.kmiecik.ais.ship.application.port;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.kmiecik.ais.positionAPI.domain.Datum;
import pl.kmiecik.ais.ship.domain.Point;
import pl.kmiecik.ais.positionAPI.domain.Track;
import pl.kmiecik.ais.weatherAPI.application.WeatherService;
import pl.kmiecik.ais.ship.domain.ShipEntity;
import pl.kmiecik.ais.ship.domain.ShipRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TrackService {
    RestTemplate restTemplate = new RestTemplate();

    private final WeatherService weatherService;
    private final ShipRepository shipRepository;

    public List<Point> getPoints() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization",
                "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjBCM0I1NEUyRkQ5OUZCQkY5NzVERDMxNDBDREQ4OEI1QzA5RkFDRjMiLCJ0eXAiOiJhdCtqd3QiLCJ4NXQiOiJDenRVNHYyWi03LVhYZE1VRE4ySXRjQ2ZyUE0ifQ.eyJuYmYiOjE2Mzg1Mjc3MDMsImV4cCI6MTYzODUzMTMwMywiaXNzIjoiaHR0cHM6Ly9pZC5iYXJlbnRzd2F0Y2gubm8iLCJhdWQiOiJhcGkiLCJjbGllbnRfaWQiOiJLcnp5c3p0b2Zfa21pZWNpa0B3cC5wbDprcnp5c3p0b2Zfa21pZWNpa0B3cC5wbCIsInNjb3BlIjpbImFwaSJdfQ.r_fn8xuo24WyPvReipX6HP48dVwcUQJL4COm2hdnucshc2z59Td_CGXjAYhu6tGIFiSkC4P3sVcel-HJo4vPMaZjTedA-__Pf4X0Vaj608TGzVQwAKkowLc9FTQWceS4EvtbeZNj5mTjXOMSeSD3iyHizGuUHnMEfxvmv8W4YMWOORoZ_pM1M1tf3ulCq73lFfanbkcoKPAPZKaHRcUCqdV4ftd3Y5_uX8zG_nW44wdY6PjXJGqV9cw359lmtbW4WvSek5ZbiYAtZ2FmuWjTotSbEeH9WpQ43NJqRifWYAKi6g1wiD0YmVnaWgE574sSVarfjRzD9h3ljRbeCt_XZYct5773KJVI21VKRYie5dN-hABLWpiSnAgSov9I4LaNzfLSRJk6LWEg18D71mz8hAfgWIuFkVE6zeqqcTLu__vVxucox-9NDDBHaN2MpDLNPTbyB4sm3qw3hlWNUyD7Tq2rLlEXnckKTidC6K0fu2Vr-lQ4zywvHMc4iq4cwCBMPmDiDET9xuMaDB_XsuUDOj4pYDKMdT3CK-epozZLoU_uNY7fs3gF47wS8zNknFaFP8pEHA5Apu2-rTnBUO-x1_c1xWAlW5IzAoI_A9lD0MNRVaSiiuj5Zbrw5vNTT1432hCez8igtQv86JgTBF22zA8v9t1RM77R7Va6htwRJGU");
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

    public void saveShip(Point point) {
        ShipEntity shipEntity = mapToEntity(point);

        shipRepository.save(shipEntity);
    }

    private ShipEntity mapToEntity(Point point) {
        ShipEntity shipEntity = new ShipEntity(point.getY(), point.getX(), point.getName(), point.getDestinationY(), point.getDestinationX(), point.getVisibilityInKm());
        return shipEntity;
    }

    public void saveShips(List<Point> ships) {
        ships.forEach(ship -> saveShip(ship));
    }
}
