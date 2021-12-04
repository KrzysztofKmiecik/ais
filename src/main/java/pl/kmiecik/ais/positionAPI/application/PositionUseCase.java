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
                "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjBCM0I1NEUyRkQ5OUZCQkY5NzVERDMxNDBDREQ4OEI1QzA5RkFDRjMiLCJ0eXAiOiJhdCtqd3QiLCJ4NXQiOiJDenRVNHYyWi03LVhYZE1VRE4ySXRjQ2ZyUE0ifQ.eyJuYmYiOjE2Mzg2NDk4NDAsImV4cCI6MTYzODY1MzQ0MCwiaXNzIjoiaHR0cHM6Ly9pZC5iYXJlbnRzd2F0Y2gubm8iLCJhdWQiOiJhcGkiLCJjbGllbnRfaWQiOiJLcnp5c3p0b2Zfa21pZWNpa0B3cC5wbDprcnp5c3p0b2Zfa21pZWNpa0B3cC5wbCIsInNjb3BlIjpbImFwaSJdfQ.RPDw3KPbQtvDgOw4hEz9WeHSMrQQg9d6FYF66oNWk-NxG9_sks0pYoTJ_511Exf9aNEPUYs3U0ryrDeD3bFnWd5SGJdBW04FYZLGUOY5wRReHQuj_JNm7eSrDksnUImtdnuUR3LlM0zfhU8hwoni7u_fR0iX4K44gaVZPlgu8I4bDhOlLQk9H_uruOcEGcI-GpxX01Y3ZzeF6iP46q0avWcN73KCD_awoTPgfeyt-VkRgxJkD3GiWodelDf2hfImMPi8HAsAUWKXoRel0xuMjsA6FpHRfwMco8Bl2FgiVAsUihiezBOR4y4PGEi3gyVLKb-4IwytGUM9q6hTXLe1Z8cRVRwoEZbTACAIAtrrZzp29bqyaBEffoIkxtFv8CtqATToDERHSw4aWFZD6F75QR-rUZUGdZuUbt9IUffWM5yaxXJrjrG9IRQ80CcDbF2T1gK36BIe9NA9asUlPzMjEdCFS8rYwk3Qg0RftC7MRm-KVw7G6SpG9OOQd7D7hpLoH-AyGVBSwD9zPu9-hYk9eUVc_cT9Ot8L20hH4LplO-aqm_s5zodBjylAD-T3A8sDXy2pB8e__Fkxmc3oSde1R1eUcU2rwICcLJJsAX89ukznu-OWXDBvauH0Xb7kp94HKWTH_daeJMJ4vTD4mZsfxFakmEJ5YV5fTvprs6oMdmA");
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
