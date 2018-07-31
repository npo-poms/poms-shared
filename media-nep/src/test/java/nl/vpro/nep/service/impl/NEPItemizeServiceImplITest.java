package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;

import nl.vpro.nep.domain.NEPItemizeRequest;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Slf4j
public class NEPItemizeServiceImplITest {

    @Test
    public void itemizeStaging() {
        NEPItemizeServiceImpl itemizer = new NEPItemizeServiceImpl(
            "https://itemizer1-npocdn-stg.twobridges.io/v1/api/itemizer/job",
            "Bearer ***REMOVED***.***REMOVED***.***REMOVED***");

        NEPItemizeRequest request = new NEPItemizeRequest();
        request.setIdentifier("WO_VPRO_13061051");
        request.setStarttime("00:00:00.000");
        request.setEndtime("00:02:21.151");
        log.info("response: {}", itemizer.itemize(request));

    }


    @Test
    public void itemizeProductie() {
        NEPItemizeServiceImpl itemizer = new NEPItemizeServiceImpl(
            "https://itemizer1-npocdn-prd.twobridges.io/v1/api/itemizer/job",
            "Bearer ***REMOVED***.***REMOVED***.***REMOVED***");

        NEPItemizeRequest request = NEPItemizeRequest.builder()
            .identifier("WO_VPRO_13061051")
            .starttime("00:00:00.000")
            .endtime("24:00:00.000")
            .build();
        log.info("response: {}", itemizer.itemize(request));

    }
}
