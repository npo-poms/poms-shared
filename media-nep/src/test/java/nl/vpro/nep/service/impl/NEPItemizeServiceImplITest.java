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
    public void itemize() {
        NEPItemizeServiceImpl itemizer = new NEPItemizeServiceImpl(
            "https://itemizer1-npocdn-stg.twobridges.io/v1/api/itemizer/job",
            "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJJdGVtaXplciBBUEkiLCJzdWIiOiJwb21zIiwicHJvdmlkZXIiOiJucG8iLCJwbGF0Zm9ybSI6Im5wb3BsdXMifQ.JHuJfi94jEyNDJRJRtqhT151C5KBaA8lduMhxytMUf8");

        NEPItemizeRequest request = new NEPItemizeRequest();
        request.setIdentifier("AT_2073522");
        request.setStarttime("00:00:00.000");
        request.setEndtime("00:02:21.150");
        log.info("response: {}", itemizer.itemize(request));

    }
}
