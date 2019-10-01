package nl.vpro.beeldengeluid.gtaa;

import nl.vpro.util.ConfigUtils;
import nl.vpro.util.Env;

import static nl.vpro.beeldengeluid.gtaa.OpenskosRepository.CONFIG_FILE;

/**
 *
 */
class OpenskosRepositoryBuilder {


    public static OpenskosRepository getRealInstance(final Env env) {


        final OpenskosRepository impl = ConfigUtils.configuredInHome(env, OpenskosRepository.class, CONFIG_FILE);
        impl.init();
        return impl;
    }
}
