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
                "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjBCM0I1NEUyRkQ5OUZCQkY5NzVERDMxNDBDREQ4OEI1QzA5RkFDRjMiLCJ0eXAiOiJhdCtqd3QiLCJ4NXQiOiJDenRVNHYyWi03LVhYZE1VRE4ySXRjQ2ZyUE0ifQ.eyJuYmYiOjE2Mzg2MzgwNTQsImV4cCI6MTYzODY0MTY1NCwiaXNzIjoiaHR0cHM6Ly9pZC5iYXJlbnRzd2F0Y2gubm8iLCJhdWQiOiJhcGkiLCJjbGllbnRfaWQiOiJLcnp5c3p0b2Zfa21pZWNpa0B3cC5wbDprcnp5c3p0b2Zfa21pZWNpa0B3cC5wbCIsInNjb3BlIjpbImFwaSJdfQ.N3zIsr-DtsQUpwPUtDPyQG4n61orfhVWjPOr80mRSKW7-tMtQnUyg4rQmyFe2u3BU92U0p22WLLKszBVEvBr_7ahk5PKEYc3WHFO7NqIYEySVW47Ngi5ZE-F-Jzw7pxd4W6qwwuInV1QVwPIuoKEqrGrUrHLVvAauzbhfpkjVQozOsVOvQ3EfGOHTdQ2Ro1SI8oJKowbsSjhfaFQb_xW_Fo7bXjrxugv1Y2QSTId0vWgecokTRLDUW4YBqp2sdKUCQd9_CMXedDdL7UBVmQxS7EHxgPsTITOoZFMu6oRhOzR2GZlPAFkmiVLyb26DDfeiFgVQKcP5WHTqVBLa3DHV_2dHv3RXLAG1btkjLZqObTZbsGajuYwUd1mr7JYkiqZjWpB7yU-DDxhNJJN5N24lsIxZhYuH87lRlruqZ3ottpqSBCxcRRSo5zN4OCWSDj_Gz39q6DGICWxDjutsTQWJm6RO0RTxWtQlT9OFUmMDWyEz2MlVaRdfDa49m6DFnzdQuyDByKlZU3cUY_TJqPcuMmCS3oDgs1lfu6Kp26ACudt5z9IBgV3mLvT7qPO7GwMXnzpz_wV8r8Znx5fA_oQUQDDLzvFMpktoZ8wGfYKrsAkySBLP1dgqJwkd0i31jJrNXEVZcSf3F89lAYHab-XgqsoFXgUXn8GzLEB1Ftt2m8");
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
