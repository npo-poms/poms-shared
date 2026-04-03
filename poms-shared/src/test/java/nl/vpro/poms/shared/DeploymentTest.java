package nl.vpro.poms.shared;

import lombok.extern.log4j.Log4j2;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import nl.vpro.util.Env;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
class DeploymentTest {

    @ParameterizedTest
    @EnumSource(Deployment.class)
    void getBaseUrl(Deployment deployment) {
        for (Env env : List.of(Env.PROD, Env.ACC, Env.TEST, Env.LOCALHOST)) {
            String baseUrl = deployment.getBaseUrl(env);
            log.info("{} {} {}", deployment, env, baseUrl);
            if (baseUrl != null) {
                assertThat(baseUrl.isEmpty()).isFalse();
                assertThat(baseUrl).doesNotEndWith("/");
            }
        }
    }

}
