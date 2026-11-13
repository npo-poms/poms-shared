package nl.vpro.sourcingservice;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks the wiring of {@code META-INF/vpro/sourcingservice-context.xml}
 */
class SourcingServiceContextTest {

    @Test
    void defaults() {
        try (GenericXmlApplicationContext ctx = context(new Properties())) {
            AudioSourcingServiceImpl audio = ctx.getBean(AudioSourcingServiceImpl.class);
            assertThat(audio.getStatusAllowedPerWindow()).isEqualTo(50);
            assertThat(audio.getStatusWindow()).isEqualTo("PT10S");
            assertThat(audio.getStatusMaxPerBucket()).isEqualTo(5);
            assertThat(audio.getStatusBucketDuration()).isEqualTo("PT0.5S");
            assertThat(ctx.getBean(VideoSourcingServiceImpl.class).getStatusAllowedPerWindow()).isEqualTo(50);
        }
    }

    @Test
    void configured() {
        Properties properties = new Properties();
        properties.setProperty("sourcingservice.status.allowedPerWindow", "300");
        properties.setProperty("sourcingservice.status.window", "1m");
        properties.setProperty("sourcingservice.status.maxPerBucket", "0");
        try (GenericXmlApplicationContext ctx = context(properties)) {
            AudioSourcingServiceImpl audio = ctx.getBean(AudioSourcingServiceImpl.class);
            assertThat(audio.getStatusAllowedPerWindow()).isEqualTo(300);
            assertThat(audio.getStatusWindow()).isEqualTo("PT1M");
            assertThat(audio.getStatusMaxPerBucket()).isEqualTo(0);
        }
    }

    @Test
    void unlimited() {
        Properties properties = new Properties();
        properties.setProperty("sourcingservice.status.allowedPerWindow", "0");
        try (GenericXmlApplicationContext ctx = context(properties)) {
            assertThat(ctx.getBean(AudioSourcingServiceImpl.class).getStatusAllowedPerWindow()).isEqualTo(0);
        }
    }

    private GenericXmlApplicationContext context(Properties extra) {
        Properties properties = new Properties();
        properties.setProperty("sourcingservice.baseUrl", "http://localhost/");
        properties.setProperty("sourcingservice.token", "token");
        properties.putAll(extra);

        PropertySourcesPlaceholderConfigurer placeholders = new PropertySourcesPlaceholderConfigurer();
        placeholders.setProperties(properties);

        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        AnnotationConfigUtils.registerAnnotationConfigProcessors(ctx);
        ctx.getBeanFactory().registerSingleton("placeholders", placeholders);
        ctx.registerBean(MeterRegistry.class, SimpleMeterRegistry::new);
        ctx.load("classpath:META-INF/vpro/sourcingservice-context.xml");
        ctx.refresh();
        return ctx;
    }
}
