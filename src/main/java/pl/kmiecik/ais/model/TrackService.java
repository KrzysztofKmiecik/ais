package pl.kmiecik.ais.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class TrackService {
    RestTemplate restTemplate = new RestTemplate();

    private final WeatherService weatherService;

    public List<Point> getTracks() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization",
                "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjBCM0I1NEUyRkQ5OUZCQkY5NzVERDMxNDBDREQ4OEI1QzA5RkFDRjMiLCJ0eXAiOiJhdCtqd3QiLCJ4NXQiOiJDenRVNHYyWi03LVhYZE1VRE4ySXRjQ2ZyUE0ifQ.eyJuYmYiOjE2MzgzMTAxNDQsImV4cCI6MTYzODMxMzc0NCwiaXNzIjoiaHR0cHM6Ly9pZC5iYXJlbnRzd2F0Y2gubm8iLCJhdWQiOiJhcGkiLCJjbGllbnRfaWQiOiJLcnp5c3p0b2Zfa21pZWNpa0B3cC5wbDprcnp5c3p0b2Zfa21pZWNpa0B3cC5wbCIsInNjb3BlIjpbImFwaSJdfQ.cAuny2Q8iL9TgzLq6VisPOM2PiU2oYIYTaa4CrgzVTFYXKao3XWKDjB4L1_5pxglZ2KQ3IOb12M-b53kcuRUg7hbkFuaWX93lMPexbDeTQS28bnP92bI-OKyH205y7dhTHnaP2qhIDr5Wj7FDVbpHwtmrbuwZYRoSO1iSqp6ONAxwTF7z7pcjnYcAUsujKxIgrSzYfLSEYYCJnt3CkM_eKFl42wT8s_xRZCkfT3gJKQHjLYu4932fOBv5F0HIyZhXYEVTjoK3y6NA8HkoFBIgoQVPYtUgLWjVB5-hfKXOd3oIGG8eUnRmB7UFohdL3R4bg3wE5OIzERZXvEwM79Q0Oa6Lv1k0sdfDldcqkOcTcIaP8Vt4tiT7O8rUqjoGgGJ-hXvzLVcd46F5Up1jXGrCDDnTLzEPoW0WFPnu-XdrIis6OVAE19wBG_CjHIptYDrm6DNetvO8C1-IVkiaEdsGheQFKZWd6gmrOoMDCi8wBPjB9N65kCX4vI7rA99b236GO8RR8eXYqxcAICuFbV1FtpGnaZ6I6b1iTD3Jk4Bypam4lXoy-vs3T5zCF4N8YHxHKHjZGMJ5edCelIuLsAnFhf5MDW0qaQBxPcbpBOtumeHC1UUMDojQWPOTtyfT9B3TrQkFgvmQ1lqQ5xdsltBM_nQh_2iNcuDY_U-FB6UIck");
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
}
