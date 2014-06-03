package nl.vpro.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * An extension of {@link PropertyPlaceholderConfigurer} that only exposes the map of properties (for use in e.g. JSP).
 *
 * @author Michiel Meeuwissen
 */
public class PropertiesUtil extends PropertyPlaceholderConfigurer {
    private static final Logger LOG = LoggerFactory.getLogger(PropertiesUtil.class);

    private Map<String, String> propertiesMap;

    private String[] systemProperties;

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory,
                                     Properties props) throws BeansException {
        super.processProperties(beanFactory, props);
        initMap(props);
        initSystemProperties();
    }

    public Map<String, String> getMap() {
        return Collections.unmodifiableMap(propertiesMap);
    }

    public void setExposeAsSystemProperty(String properties) {
        systemProperties = properties.split("\\s*,\\s*");
    }

    private void initMap(Properties props) {

        Properties p = new Properties();
        p.putAll(System.getProperties());
        p.putAll(props);

        PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper(
            placeholderPrefix, placeholderSuffix, valueSeparator, ignoreUnresolvablePlaceholders);


        propertiesMap = new HashMap<String, String>();
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
                        LOG.warn("Can not override System property {} because it allready exists", property);
                    }
                } else {
                    LOG.error("Property {} not found, please check the property configuration", property);
                }
            }
        }
    }
}
