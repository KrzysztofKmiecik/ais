
package pl.kmiecik.ais.weatherAPI.domain;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "cost",
    "dailyQuota",
    "end",
    "lat",
    "lng",
    "params",
    "requestCount",
    "start"
})
@Generated("jsonschema2pojo")
public class Meta {

    @JsonProperty("cost")
    private Integer cost;
    @JsonProperty("dailyQuota")
    private Integer dailyQuota;
    @JsonProperty("end")
    private String end;
    @JsonProperty("lat")
    private Double lat;
    @JsonProperty("lng")
    private Double lng;
    @JsonProperty("params")
    private List<String> params = null;
    @JsonProperty("requestCount")
    private Integer requestCount;
    @JsonProperty("start")
    private String start;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("cost")
    public Integer getCost() {
        return cost;
    }

    @JsonProperty("cost")
    public void setCost(Integer cost) {
        this.cost = cost;
    }

    @JsonProperty("dailyQuota")
    public Integer getDailyQuota() {
        return dailyQuota;
    }

    @JsonProperty("dailyQuota")
    public void setDailyQuota(Integer dailyQuota) {
        this.dailyQuota = dailyQuota;
    }

    @JsonProperty("end")
    public String getEnd() {
        return end;
    }

    @JsonProperty("end")
    public void setEnd(String end) {
        this.end = end;
    }

    @JsonProperty("lat")
    public Double getLat() {
        return lat;
    }

    @JsonProperty("lat")
    public void setLat(Double lat) {
        this.lat = lat;
    }

    @JsonProperty("lng")
    public Double getLng() {
        return lng;
    }

    @JsonProperty("lng")
    public void setLng(Double lng) {
        this.lng = lng;
    }

    @JsonProperty("params")
    public List<String> getParams() {
        return params;
    }

    @JsonProperty("params")
    public void setParams(List<String> params) {
        this.params = params;
    }

    @JsonProperty("requestCount")
    public Integer getRequestCount() {
        return requestCount;
    }

    @JsonProperty("requestCount")
    public void setRequestCount(Integer requestCount) {
        this.requestCount = requestCount;
    }

    @JsonProperty("start")
    public String getStart() {
        return start;
    }

    @JsonProperty("start")
    public void setStart(String start) {
        this.start = start;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
