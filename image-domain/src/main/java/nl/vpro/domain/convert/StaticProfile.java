package nl.vpro.domain.convert;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.UnaryOperator;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.image.Dimension;


public interface StaticProfile extends ParameterizedProfile<StaticProfile.StaticConversion> {


    @Override
    default TestResult<StaticConversion> dynamicTest(@NonNull String request) {
        StaticConversion staticConversion = Service.INSTANCE.getMap().get(request);
        if (staticConversion == null){
            return new TestResult<>(false, null);
        }
        return new TestResult<>(TestResult.MatchResult.MATCH, staticConversion);
    }

    @Override
    default Dimension convertedDimension(Object s, Dimension in) {
        if (s instanceof StaticConversion staticConversion) {
            return staticConversion.dimensionConversion.apply(in);
        }
        return in;
    }


    @Getter()
    class StaticConversion {
        final String name;
        final UnaryOperator<Dimension> dimensionConversion;
        final String description;
        final List<String> commands;

        public StaticConversion(
            String name,
            List<String> commands,
            UnaryOperator<Dimension> dimensionConversion
        ) {
            this.name = name;
            this.dimensionConversion = dimensionConversion;
            this.commands = commands;
            this.description = String.join(" ", commands);
        }
    }

    @Slf4j
    class Service {
        public static Service INSTANCE = new Service();
        @Getter
        Map<String, StaticConversion> map;

        private Service() {
            Map<String, StaticConversion> conversion = new LinkedHashMap<>();
            Properties properties = new Properties();
            try (InputStream in = this.getClass().getResourceAsStream("/image-conversions.properties")) {
                properties.load(in);

                properties.forEach((k, v) -> {
                    // Split on whitespace from last match, not preceded by a quotation mark or on whitespace preceded by a quotation
                    // mark from last match with a second closing quotation before matching whitespace. Look behind 30 characters

                    String key = k.toString();
                    if (key.startsWith("profile.")) {
                        key = key.substring("profile.".length());
                        List<String> commands = Arrays.asList(v.toString().split("(?<!\\G'.{0,30})\\s|(?<=\\G'[^']{0,30}')\\s"));
                        conversion.put(key, new StaticConversion(key, commands, dim -> dim));
                    }
                });

            } catch (IOException e) {
                log.warn(e.getMessage(), e);
            }
            map = Collections.unmodifiableMap(conversion);
        }
    }

}
