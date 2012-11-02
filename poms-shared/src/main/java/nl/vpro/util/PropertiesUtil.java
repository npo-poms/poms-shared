package nl.vpro.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

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
        systemProperties = properties.split(",");
    }

    private void initMap(Properties props) {
        propertiesMap = new HashMap<String, String>();
        for(Object key : props.keySet()) {
            String keyStr = key.toString();
            propertiesMap.put(
                keyStr,
                resolvePlaceholder(keyStr, props));
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
