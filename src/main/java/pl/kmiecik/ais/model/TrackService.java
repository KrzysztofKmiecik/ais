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
                "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjBCM0I1NEUyRkQ5OUZCQkY5NzVERDMxNDBDREQ4OEI1QzA5RkFDRjMiLCJ0eXAiOiJhdCtqd3QiLCJ4NXQiOiJDenRVNHYyWi03LVhYZE1VRE4ySXRjQ2ZyUE0ifQ.eyJuYmYiOjE2MzgxNDA3MDUsImV4cCI6MTYzODE0NDMwNSwiaXNzIjoiaHR0cHM6Ly9pZC5iYXJlbnRzd2F0Y2gubm8iLCJhdWQiOiJhcGkiLCJjbGllbnRfaWQiOiJLcnp5c3p0b2Zfa21pZWNpa0B3cC5wbDprcnp5c3p0b2Zfa21pZWNpa0B3cC5wbCIsInNjb3BlIjpbImFwaSJdfQ.GUZ-NPIbmShnLyxqYrAbxL7u5KI8aWBzU4NvV9Bj3Qks0Y0wJrP2nulhV5Ca76oPrmdvVkJ1n4_jAxqnMXO8y_JNVBfeou9bgIX4pTniGjKSSeI38lk3Ukpdqs6Kb-TtChv3bmUCpXWkC56vvy458f4G9FgchmKh4XewPVq2yC7ysoFQTVybIOq1yAcxHTiwtXNBN288Cg_wGvmyG5JUp0P6bzDqwkW1rrxD7_ZOLZNPlPHis7SnBjv7jFnhzxe4tBrvhcA9Cp2XXklphqrQjoAXNX_HvSkoKkSS0cs4nIVjmc7uNGy3jiZWlb27mBrAiM_pr1Md0sNahbkwVrSpa7Dx8mkDmkwOck9MdSn9xG2N6__bnuDTiQbdaRsy-m6quBOPpVwSZXOwHipYXgiCgakp9KxvJWGSvV-iS5S-T-1fJKARwSGPlHaShKwO2OOx9Fvliewem7qH4HslSw4puwT1f6Qbacn1lQCduMKg2CR8mbUgc9HTOT_KVM_FeQv284yWTdd22uVFrjOkzm_86cX1itZf-msniuwFfE2quV2ky7GYwPebk26GxNHqJG8s35hvuLJd7oZB4JSAaTClnACB2GJ-O9INHWtYZBNqLQjZPPqyQAYTWiFD-KbOw_HWrxKia467R6RGlumZ2myRqtNxkWuhw9wdvFigPlb8f7g");
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
