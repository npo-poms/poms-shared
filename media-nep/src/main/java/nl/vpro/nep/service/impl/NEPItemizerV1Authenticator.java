package nl.vpro.nep.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
public class NEPItemizerV1Authenticator implements Supplier<String> {

    private final String bearerToken;

    public NEPItemizerV1Authenticator(
        @Value("${nep.itemizer-api.key}") String key
        ) {
        this.bearerToken = key;

    }


    @Override
    public String get() {
        return bearerToken;
    }

}
