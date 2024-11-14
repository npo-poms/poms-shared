package nl.vpro.sourcingservice;

import java.util.Arrays;
import java.util.function.Supplier;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource
public class ConfigurationService implements Supplier<Configuration> {

    private final Configuration[] configurations;
    private int version;

    public ConfigurationService(
        int version,
        Configuration... configurations) {
        this.configurations = configurations;
        this.version = version;
        assert this.version == 2;
    }


    @Override
    public Configuration get() {
        return Arrays.stream(configurations).filter(c -> c.version() == version).findFirst().orElseThrow();
    }

    @ManagedAttribute
    public int getVersion() {
        return version;
    }

    @ManagedAttribute
    public void setVersion(int version) {
        this.version = version;
    }
}
