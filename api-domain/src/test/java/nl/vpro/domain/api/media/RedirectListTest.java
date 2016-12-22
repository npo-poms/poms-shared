package nl.vpro.domain.api.media;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXB;

import org.junit.Assert;
import org.junit.Test;

import nl.vpro.jackson2.Jackson2Mapper;

public class RedirectListTest {

    private RedirectList instance = new RedirectList();

    {
        Map<String, String> redirects = new HashMap<>();
        redirects.put("a", "b");
        instance.redirects = redirects;
    }

    @Test
    public void json() throws IOException {
        //Jackson2Mapper.getInstance().writeValue(System.out, instance);
        Assert.assertEquals("{\"lastUpdate\":\"1970-01-01T01:00:00+01:00\",\"map\":{\"a\":\"b\"}}", Jackson2Mapper.getInstance().writeValueAsString(instance));
    }

    @Test
    public void jaxb() throws IOException {
        JAXB.marshal(instance, System.out);
    }

}
