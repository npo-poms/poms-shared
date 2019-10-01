package nl.vpro.beeldengeluid.gtaa;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.client.RestTemplate;

import nl.vpro.util.ConfigUtils;
import nl.vpro.util.Env;

/**
 * This class is duplicated in media-gtaa test
 */
public class OpenskosRepositoryBuilder {


    public static OpenskosRepository getRealInstance(final Env env) {
        MarshallingHttpMessageConverter marshallingHttpMessageConverter = new MarshallingHttpMessageConverter();
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setPackagesToScan(
            "nl.vpro.beeldengeluid.gtaa",
            "nl.vpro.w3.rdf",
            "nl.vpro.openarchives.oai"
        );

        try {
            jaxb2Marshaller.afterPropertiesSet();
        } catch (Exception ex) {
            /* Ignore */
        }
        marshallingHttpMessageConverter.setMarshaller(jaxb2Marshaller);
        marshallingHttpMessageConverter.setUnmarshaller(jaxb2Marshaller);

        RestTemplate template = new RestTemplate();
        template.setMessageConverters(Collections.singletonList(marshallingHttpMessageConverter));
        Map<String, String> properties =
        ConfigUtils.filtered(env, ConfigUtils.getPropertiesInHome("openskosrepository.properties"));

        final OpenskosRepository impl =
            OpenskosRepository.builder()
                .gtaaUrl(properties.get("gtaaUrl"))
                .gtaaKey(properties.get("gtaaKey"))
                .personsSpec(properties.get("personsSpec"))
                .template(template)
                .build();

        impl.init();
        impl.setUseXLLabels(true);
        impl.setTenant("beng");

        return impl;
    }
}
