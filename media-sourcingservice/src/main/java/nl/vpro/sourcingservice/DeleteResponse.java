package nl.vpro.sourcingservice;

import lombok.Getter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@ToString()
public class DeleteResponse  {

    final String status;
    final String response;

    @JsonCreator
    public DeleteResponse(@JsonProperty("status") String status, @JsonProperty("response") String response) {
        this.status = status;
        this.response = response;
    }

}
