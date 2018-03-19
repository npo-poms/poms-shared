package nl.vpro.nep.service;

import nl.vpro.nep.domain.NEPItemizeRequest;
import nl.vpro.nep.domain.NEPItemizeResponse;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
public interface  ItemizeService {

    NEPItemizeResponse itemize(NEPItemizeRequest request);

}
