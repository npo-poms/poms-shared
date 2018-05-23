package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import org.junit.Ignore;
import org.junit.Test;

import nl.vpro.nep.domain.NEPItemizeRequest;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Slf4j
@Ignore
public class NEPItemizeServiceImplITest {

    @Test
    public void itemize() {
        NEPItemizeServiceImpl itemizer = new NEPItemizeServiceImpl(
            "https://itemizer1-npocdn-stg.twobridges.io/v1/api/itemizer/job",
            "Bearer ***REMOVED***.***REMOVED***.***REMOVED***");

        NEPItemizeRequest request = new NEPItemizeRequest();
        request.setIdentifier("AT_2073522");
        request.setStarttime("00:00:00.000");
        request.setEndtime("00:02:21.150");
        log.info("response: {}", itemizer.itemize(request));

    }
}
