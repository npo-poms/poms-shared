package nl.vpro.util;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.Resource;
import org.springframework.util.PropertyPlaceholderHelper;

/**
 * An extension of {@link PropertyPlaceholderConfigurer} that only exposes the map of properties (for use in e.g. JSP).
 *
 * @author Michiel Meeuwissen
 */
public class PropertiesUtil extends PropertyPlaceholderConfigurer {

    private Map<String, String> propertiesMap;

    private Map<String, String> logMap = new HashMap<>();

    private Map<String, String> copyMap = new HashMap<>();


    private String[] systemProperties;

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory,
                                     Properties props) throws BeansException {
        super.processProperties(beanFactory, props);
        initMap(props);
        initSystemProperties();
        if (logMap.isEmpty()) {
            logger.debug(String.valueOf(getMap()));
        } else {
            PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper(
                placeholderPrefix, placeholderSuffix, valueSeparator, ignoreUnresolvablePlaceholders);
            for (Map.Entry<String, String> logEntry : logMap.entrySet()) {
                logger.info(String.format(helper.replacePlaceholders(logEntry.getValue(), props), getMap().get(logEntry.getKey())));
            }
        }
    }

    public Map<String, String> getMap() {
        return Collections.unmodifiableMap(propertiesMap);
    }

    public void setExposeAsSystemProperty(String properties) {
        systemProperties = properties.split("\\s*,\\s*");
    }

    public void setLog(Map<String, String> map) {
        this.logMap = map;
    }

    public void setCopy(Map<String, String> map) {
        this.copyMap = map;
    }

    @Override
    public void setLocations(Resource[] locations) {

        System.out.println("Configuring with");
        for (Resource location : locations) {
            try {
                if (location != null) {
                    File file = location.getFile();
                    if (file != null) {
                        System.out.println(location + " -> " + file + " (" + (file.canRead() ? "can be read" : "not readable") + ")");
                    }
                }
            } catch (IOException ioe) {
                System.out.println(location);
            }
        }
        super.setLocations(locations);
    }

    private void initMap(Properties props) {

        Properties p = new Properties();
        p.putAll(System.getProperties());
        p.putAll(props);

        PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper(
            placeholderPrefix, placeholderSuffix, valueSeparator, ignoreUnresolvablePlaceholders);


        propertiesMap = new HashMap<>();
        for(Object key : p.keySet()) {
            String keyStr = key.toString();
            String value = p.getProperty(keyStr);
            if (value == null && p.containsKey(keyStr)) value = "";
            String v = helper.replacePlaceholders(value, p);
            propertiesMap.put(keyStr, v);
        }
    }

    private void initSystemProperties() {
        if(systemProperties != null) {
            for(String property : systemProperties) {
                String value = propertiesMap.get(property);
                if(value != null) {
                    if(System.getProperty(property) == null || localOverride) {
                        System.setProperty(property, value);
                    } else {
                        logger.warn("Can not override System property " + property + " because it allready exists");
                    }
                } else {
                    logger.error("Property " + property + " not found, please check the property configuration");
                }
            }
        }
    }
}
