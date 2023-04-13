package nl.vpro.sourcingservice;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

@Data
public class Callback {

    final String media_id;
    final String status;
    final String asset_url;
    final JsonNode raw_data;

    @JsonCreator
    public Callback(String mediaId, String status, String assetUrl, JsonNode rawData) {
        media_id = mediaId;
        this.status = status;
        asset_url = assetUrl;
        raw_data = rawData;
    }
}
