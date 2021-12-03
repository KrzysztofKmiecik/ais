
package pl.kmiecik.ais.weatherAPI.domain;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "noaa",
    "sg"
})
@Generated("jsonschema2pojo")
public class Visibility {

    @JsonProperty("noaa")
    private Double noaa;
    @JsonProperty("sg")
    private Double sg;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("noaa")
    public Double getNoaa() {
        return noaa;
    }

    @JsonProperty("noaa")
    public void setNoaa(Double noaa) {
        this.noaa = noaa;
    }

    @JsonProperty("sg")
    public Double getSg() {
        return sg;
    }

    @JsonProperty("sg")
    public void setSg(Double sg) {
        this.sg = sg;
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
