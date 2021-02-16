package nl.vpro.nep.domain;

import lombok.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.Builder
public class ItemizerStatusResponse {

    private String jobId;

    private ItemizerStatus status;

    private String statusMessage;

}
