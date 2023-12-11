package nl.vpro.sourcingservice;

import java.util.function.Supplier;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource
public class ConfigurationService implements Supplier<Configuration> {

    private final Configuration configuration1;
    private final Configuration configuration2;
    private int version;

    public ConfigurationService(
        Configuration configuration1,
        Configuration configuration2,
        int version) {
        this.configuration1 = configuration1;
        this.configuration2 = configuration2;
        this.version = version;
    }


    @Override
    public Configuration get() {
        return version == 1 ? configuration1 : configuration2;
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
