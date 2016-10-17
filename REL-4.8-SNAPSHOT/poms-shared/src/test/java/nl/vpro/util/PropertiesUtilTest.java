package nl.vpro.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class PropertiesUtilTest {

    @Autowired
    private ApplicationContext applicationContext;


    @Test
    public void testGetMap() throws Exception {
        PropertiesUtil properties = applicationContext.getBean(PropertiesUtil.class);

        assertThat(properties.getMap().get("b")).isEqualTo("B");
        assertThat(properties.getMap().get("c")).isEqualTo("A/B/C");

    }

    @Test
    public void testSetExposeAsSystemProperty() throws Exception {


        assertThat(System.getProperty("b")).isEqualTo("B");
        assertThat(System.getProperty("a")).isNull();
        assertThat(System.getProperty("c")).isNull();

    }
}
