package pl.kmiecik.ais.model;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TrackService {
    RestTemplate restTemplate = new RestTemplate();


    public List<Point> getTracks() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization",
                "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjBCM0I1NEUyRkQ5OUZCQkY5NzVERDMxNDBDREQ4OEI1QzA5RkFDRjMiLCJ0eXAiOiJhdCtqd3QiLCJ4NXQiOiJDenRVNHYyWi03LVhYZE1VRE4ySXRjQ2ZyUE0ifQ.eyJuYmYiOjE2MzgxMzEwNTMsImV4cCI6MTYzODEzNDY1MywiaXNzIjoiaHR0cHM6Ly9pZC5iYXJlbnRzd2F0Y2gubm8iLCJhdWQiOiJhcGkiLCJjbGllbnRfaWQiOiJLcnp5c3p0b2Zfa21pZWNpa0B3cC5wbDprcnp5c3p0b2Zfa21pZWNpa0B3cC5wbCIsInNjb3BlIjpbImFwaSJdfQ.KDZ-Jm9PC-dSFMDEej4SOvbePmDYyAoegqOXf71FWvwWsqverlFOCEVwpy9ba773TH_PKLvXXgzrvKhoeWfW-1U0yxYyBT5rlzA5hNZ9PFLRAHCaI47DweeyqkxMjJNqxKsS6KJqxbAbBQ0zINbubmCE9Wt-XHkGopNaf5HuiivjUE2qhfNhJ5gqeVRoNNmXuOiK2n_C9dfc6W8Dv5ED5BIeLmoF2AA4UUvM__Vl12ZwqR4K7MXi58d2TsuTD7U4F5hGmbpkZI_y6LRiv6BP4iCB1faiO9VXFFnIxIrTvCp1Bc8N-dI-ARcj1lVPpGYlb9qSXXyqtvxdLMQn4MyjXfQDP3rSw8huNPTayqZW2P7CTeKNYZitDWhjQLn37sICZ_xb8QjUucWlclmDTe3j9YoGMii-8gbPIFSk8PAGzpAX_7ktAwjNWdo7IjK0hyPo-62WxW_Vl72Byrc2B7pzGPOxQ4pdxmsEf3PnX2WyG-SnPZGI4S-Z7_mcnl-zQbU15W-FwoAOPUqiqoGqIO-_ZSspLUkAxUNBuWCQamQTX3NMVqfqK-qaCRBlt0LtNThPeoHUI8tu_OWLpBG-ez_Nh66JvucMwReP7a4Ly7IQ_deE45ajjsZFYt5s12sgT9O_BefqZMiCWB6KhzaLoFD2jpPwKOpleWhrRc2BmwRaJfE");
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
                        getDestination(track.getDestination(), track.getGeometry().getCoordinates()).getLatitude()
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
}
