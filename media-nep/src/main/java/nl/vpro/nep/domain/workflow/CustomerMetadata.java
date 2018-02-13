package nl.vpro.nep.domain.workflow;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@lombok.Builder
@Data
public class CustomerMetadata {

    public CustomerMetadata() {

    }
    String mid;

    String broadcaster;

}
