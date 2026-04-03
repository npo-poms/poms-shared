package nl.vpro.sourcingservice;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@Data
public class Callback {

    final String media_id;
    final String status;
    final String asset_url;
    final JsonNode raw_data;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Callback(
        @JsonProperty("media_id") String media_id,
        @JsonProperty("status") String status,
        @JsonProperty("asset_url") String asset_url,
        @JsonProperty("raw_data") JsonNode rawData) {
        this.media_id = media_id;
        this.status = status;
        this.asset_url = asset_url;
        this.raw_data = rawData;
    }
}
