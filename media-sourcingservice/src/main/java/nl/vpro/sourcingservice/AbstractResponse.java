package nl.vpro.sourcingservice;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(onlyExplicitlyIncluded = false)
public class AbstractResponse {

    final String status;

    public AbstractResponse(String status) {
        this.status = status;
    }
}
