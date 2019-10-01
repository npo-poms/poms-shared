package nl.vpro.beeldengeluid.gtaa;

import java.util.Map;

import nl.vpro.util.ConfigUtils;
import nl.vpro.util.Env;

/**
 *
 */
class OpenskosRepositoryBuilder {


    public static OpenskosRepository getRealInstance(final Env env) {
        Map<String, String> properties =
            ConfigUtils.filtered(env, ConfigUtils.getPropertiesInHome("openskosrepository.properties"));

        final OpenskosRepository impl =
            OpenskosRepository.builder()
                .gtaaUrl(properties.get("gtaaUrl"))
                .gtaaKey(properties.get("gtaaKey"))
                .personsSpec(properties.get("personsSpec"))
                .build();

        impl.init();
        impl.setUseXLLabels(true);
        impl.setTenant("beng");

        return impl;
    }
}
