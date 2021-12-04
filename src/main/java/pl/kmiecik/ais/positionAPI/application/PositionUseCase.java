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
                "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjBCM0I1NEUyRkQ5OUZCQkY5NzVERDMxNDBDREQ4OEI1QzA5RkFDRjMiLCJ0eXAiOiJhdCtqd3QiLCJ4NXQiOiJDenRVNHYyWi03LVhYZE1VRE4ySXRjQ2ZyUE0ifQ.eyJuYmYiOjE2Mzg1NzQ5NTAsImV4cCI6MTYzODU3ODU1MCwiaXNzIjoiaHR0cHM6Ly9pZC5iYXJlbnRzd2F0Y2gubm8iLCJhdWQiOiJhcGkiLCJjbGllbnRfaWQiOiJLcnp5c3p0b2Zfa21pZWNpa0B3cC5wbDprcnp5c3p0b2Zfa21pZWNpa0B3cC5wbCIsInNjb3BlIjpbImFwaSJdfQ.ZoMLI9ZQOoc9Az1Dd3V8rVncbNvHd1ONce7BYR-908mPwlsX7yTKoXnEcpVfrQnQjMdtir9lScEgFIPHFnxTNkjbFjyYHq5RGoBlR9hQFL_6sCEmiBpRA5IWCMnflm1t5C1ypjnW-RP1V531G6JKbuLWnCcaRB10H_sY55r4KkDx9peFOEPwdjiUX1cccT1GQBrclMv9D_00_4-ObONbPMyxuEmngMCVQe98nBh-WNS1C8FdapBN2rEow-cAuk56cAEPCfX_Zy_VRHqmpMx2xPMcALOxqzqqJ0ktZNzuMmXmmGUvkwz9rfjL1QnHBfcWjkbVBRF_107-7Fj9jSs-u5DLUPj7WJTCMlZ8cpqGozqdCbR7RRcuqnfK0H1Rhpcat7qFGMVYwqsK86A91Ndxr6dmjW9y7shnd_V8zYVfe0VTYFQyBocPvUIIGuyf_a1nbh1smUxS74_fg5aopJikjNORegtcwkKpNfHbJDHm0oB67jz3lsjxn8BKtzIM4hDZX-1Rbt0nIuPOnC9xUCpwoaFoCEguNJSrvjYbwYFQBJeKOKUuSY_h0DjI78E13pNP9IS8UJ4xWSvIT0fyCb0srD77M6Mj_wsqiPFZfN9sBrb362Obzwv1uh8e2bM436hE237VnVuy6Jx5lxwNpnq2V-itGBtrAnSGQ9mkJJJA6kk");
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
