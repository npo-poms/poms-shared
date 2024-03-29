package nl.vpro.sourcingservice;

import lombok.Getter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@ToString()
public class DeleteResponse extends AbstractResponse {

    final String response;

    @JsonCreator
    public DeleteResponse(@JsonProperty("status") String status, @JsonProperty("response") String response) {
        super(status);
        this.response = response;
    }

}
