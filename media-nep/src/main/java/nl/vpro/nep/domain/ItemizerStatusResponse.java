package nl.vpro.nep.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Michiel Meeuwissen
 * @since 5.24
 */
@Data
@NoArgsConstructor
public class ItemizerStatusResponse {

    private String jobId;

    private ItemizerStatus status;

    private String statusMessage;

}
